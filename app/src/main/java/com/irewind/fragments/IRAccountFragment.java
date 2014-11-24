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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRLoginActivity;
import com.irewind.activities.IRTabActivity;
import com.irewind.adapters.IRAccountAdapter;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.irewind.utils.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRAccountFragment extends Fragment implements View.OnClickListener {

    @Inject
    ApiClient apiClient;

    @Inject
    ImageLoader imageLoader;

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

    @InjectView(R.id.changePassword)
    Button changePassword;

    @InjectView(R.id.changeName)
    Button changeName;

    @InjectView(R.id.editNotifs)
    Button editNotifs;

    private IRAccountAdapter mAccountAdapter;
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
                IRTabActivity.mAccountFragment = IRAccountNotificationFragment.newInstance();
                FragmentManager fragmentManager3 = getActivity().getSupportFragmentManager();
                FragmentTransaction ft3 = fragmentManager3.beginTransaction();
                ft3.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft3.replace(R.id.container, IRTabActivity.mAccountFragment)
                        .disallowAddToBackStack()
                        .commit();
                break;
            case R.id.changeName:
                IRTabActivity.mAccountFragment = IRAccountPersonalFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft.replace(R.id.container, IRTabActivity.mAccountFragment)
                        .disallowAddToBackStack()
                        .commit();
                break;
            case R.id.changePassword:
                IRTabActivity.mAccountFragment = IRAccountPasswordFragment.newInstance();
                FragmentManager fragmentManager2 = getActivity().getSupportFragmentManager();
                FragmentTransaction ft2 = fragmentManager2.beginTransaction();
                ft2.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left);
                ft2.replace(R.id.container, IRTabActivity.mAccountFragment)
                        .disallowAddToBackStack()
                        .commit();
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

    private void attemptLogout() {
        apiClient.closeSessionAndClearTokenInformation();

        Intent intent = new Intent(getActivity(), IRLoginActivity.class);
        intent.putExtra(IRLoginActivity.EXTRA_SHOULD_LOGOUT_FIRST, true);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateUserInfo(User user) {
        if (user != null) {
            if (user.getPicture() != null && user.getPicture().length() > 0) {
                imageLoader.displayImage(user.getPicture(), profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img_default_picture);
            }
            nameTextView.setText(user.getDisplayName());
            date.setText(DateUtils.getRelativeTimeSpanString(user.getCreatedDate(), new Date().getTime(), DateUtils.SECOND_IN_MILLIS));
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
