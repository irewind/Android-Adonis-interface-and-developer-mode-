package com.irewind.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRAccountNotificationActivity;
import com.irewind.activities.IRAccountPasswordActivity;
import com.irewind.activities.IRAccountPersonalActivity;
import com.irewind.activities.IRLoginActivity;
import com.irewind.activities.IRTabActivity;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.irewind.utils.AppStatus;
import com.irewind.utils.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import utils.IrewindBackend;

public class IRAccountFragment extends Fragment implements View.OnClickListener {

    @Inject
    ApiClient apiClient;

    @InjectView(R.id.btnLogout)
    Button mLogout;
    @InjectView(R.id.photo)
    ImageButton mChangePhoto;

    @InjectView(R.id.profileImageView)
    RoundedImageView profileImageView;

    @InjectView(R.id.nameTextView)
    TextView nameTextView;

    @InjectView(R.id.date)
    TextView date;

    @InjectView(R.id.change_password_section)
    ViewGroup changePasswordSection;

    @InjectView(R.id.changePassword)
    Button changePassword;

    @InjectView(R.id.changeName)
    Button changeName;

    @InjectView(R.id.editNotifs)
    Button editNotifs;

    private String realPath;
    private File sdImageMainDirectory;
    private Uri mImageCaptureUri;
    private AlertDialog dialog;

    public static IRAccountFragment newInstance() {
        IRAccountFragment fragment = new IRAccountFragment();
        return fragment;
    }

    public IRAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Injector.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_iraccount, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mLogout.setOnClickListener(this);
        mChangePhoto.setOnClickListener(this);
        changeName.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        editNotifs.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);
        apiClient.loadActiveUserInfo();

        if (IRTabActivity.searchItem != null)
            IRTabActivity.searchItem.collapseActionView();

        IRTabActivity.abBack.setVisibility(View.GONE);
        IRTabActivity.abSearch.setVisibility(View.GONE);
        IRTabActivity.abAction.setVisibility(View.GONE);
        IRTabActivity.abTitle.setText(getString(R.string.account));
    }

    @Override
    public void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogout:
                attemptLogout();
                break;
            case R.id.photo:
                attemptChangePhoto();
                break;
            case R.id.editNotifs:
                Intent notifIntent = new Intent(getActivity(), IRAccountNotificationActivity.class);
                startActivity(notifIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.changeName:
                Intent personalIntent = new Intent(getActivity(), IRAccountPersonalActivity.class);
                startActivity(personalIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
            case R.id.changePassword:
                Intent passwordIntent = new Intent(getActivity(), IRAccountPasswordActivity.class);
                startActivity(passwordIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                break;
        }
    }

    private void attemptChangePhoto() {
        if (dialog == null)
            makePictureChooser();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.show();
            }
        }, 100);
    }

    public boolean isCheckInEnabled() {
        return IrewindBackend.Instance != null && IrewindBackend.Instance.recordingState;
    }

    private void attemptLogout() {
        if (!AppStatus.getInstance(getActivity()).isOnline()) {
            Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
            return;
        }
        apiClient.closeSessionAndClearTokenInformation();

        if (isCheckInEnabled()) {
            IrewindBackend.Instance.stopRecording();
        }
        IrewindBackend.Instance = null;

        Intent intent = new Intent(getActivity(), IRLoginActivity.class);
        intent.putExtra(IRLoginActivity.EXTRA_SHOULD_LOGOUT_FIRST, true);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateUserInfo(User user) {
        if (user != null) {

            if (user.getAuthProvider() != null && (user.getAuthProvider().equals("GOOGLE") || user.getAuthProvider().equals("FACEBOOK"))) {
                changePasswordSection.setVisibility(View.GONE);
            } else {
                changePasswordSection.setVisibility(View.VISIBLE);
            }

            if (user.getPicture() != null && user.getPicture().trim().length() > 0) {
                Picasso.with(getActivity()).load(user.getPicture()).placeholder(R.drawable.img_default_picture).into(profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img_default_picture);
            }
            nameTextView.setText(user.getDisplayName());
            SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            try {
                Date parsedDate = fromUser.parse(user.getCreatedDate());
                date.setText("Joined: " + DateUtils.getRelativeTimeSpanString(parsedDate.getTime(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //   date.setText("Joined: " + user.getCreatedDate());
        } else {
            profileImageView.setImageResource(R.drawable.img_default_picture);
            nameTextView.setText("");
            date.setText("");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                Uri selectedImage = data.getData();
                realPath = Util.getPath(getActivity(), selectedImage);
                //TODO change picture on server;
            }
            if (requestCode == 200) {
                Uri takenImage = mImageCaptureUri;
                realPath = Util.getPath(getActivity(), takenImage);
                //TODO change picture on server;
            }
            Log.d("PICTURE", realPath == null ? "is null" : realPath);
        }
    }

    private void makePictureChooser() {
        final String[] items = new String[]{"Take from camera", "Select from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { //pick from camera
                if (item == 0) {
                    File root = new File(Environment
                            .getExternalStorageDirectory()
                            + File.separator + "iRewind" + File.separator);
                    root.mkdirs();
                    UUID uuid = UUID.randomUUID();

                    sdImageMainDirectory = new File(root, uuid.toString() + ".jpg");

                    mImageCaptureUri = Uri.fromFile(sdImageMainDirectory);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    startActivityForResult(intent, 200);
                } else { //pick from file
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), 100);
                }
            }
        });

        dialog = builder.create();
    }

    // --- Events --- //

    @Subscribe
    public void onEvent(UserInfoLoadedEvent event) {
        updateUserInfo(event.user);
    }

    @Subscribe
    public void onEvent(NoActiveUserEvent event) {
        updateUserInfo(null);
    }

}
