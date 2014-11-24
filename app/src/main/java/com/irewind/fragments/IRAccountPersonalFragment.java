package com.irewind.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.IntentCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.irewind.Injector;
import com.irewind.R;
import com.irewind.activities.IRLoginActivity;
import com.irewind.activities.IRTabActivity;
import com.irewind.sdk.api.ApiClient;
import com.irewind.sdk.api.event.NoActiveUserEvent;
import com.irewind.sdk.api.event.UserDeleteFailEvent;
import com.irewind.sdk.api.event.UserDeleteSuccessEvent;
import com.irewind.sdk.api.event.UserInfoLoadedEvent;
import com.irewind.sdk.api.event.UserInfoUpdateFailEvent;
import com.irewind.sdk.api.event.UserInfoUpdateSuccessEvent;
import com.irewind.sdk.model.User;
import com.irewind.ui.views.RoundedImageView;
import com.irewind.utils.CheckUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class IRAccountPersonalFragment extends Fragment implements View.OnClickListener {

    @Inject
    ApiClient apiClient;

    @Inject
    ImageLoader imageLoader;

    @InjectView(R.id.profileImageView)
    RoundedImageView profileImageView;

    @InjectView(R.id.nameTextView)
    TextView nameTextView;

    @InjectView(R.id.emailTextView)
    TextView emailTextView;

    @InjectView(R.id.contentView)
    View contentView;
    @InjectView(R.id.progressView)
    View progressView;

    @InjectView(R.id.editFirst)
    EditText mFirst;
    @InjectView(R.id.editLast)
    EditText mLast;

    @InjectView(R.id.btnDelete)
    Button btnDelete;

    @InjectView(R.id.btnChange)
    Button btnChange;

    public static IRAccountPersonalFragment newInstance() {
        IRAccountPersonalFragment fragment = new IRAccountPersonalFragment();
        return fragment;
    }

    public IRAccountPersonalFragment() {
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
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_iraccount_personal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        btnChange.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        apiClient.getEventBus().register(this);
        apiClient.loadActiveUserInfo();

        IRTabActivity.abBack.setVisibility(View.VISIBLE);
        IRTabActivity.abBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IRTabActivity.mAccountFragment = IRAccountFragment.newInstance();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_right);
                ft.replace(R.id.container, IRTabActivity.mAccountFragment)
                        .disallowAddToBackStack()
                        .commit();
            }
        });
        IRTabActivity.abSearch.setVisibility(View.GONE);
        IRTabActivity.abTitle.setText(getString(R.string.personal_data));
        IRTabActivity.abAction.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();

        apiClient.getEventBus().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChange:
                change();
                break;
            case R.id.btnDelete:
                delete();
                break;
        }
    }

    /**
     * Shows the progress view and hides the content view.
     */
    public void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        contentView.setVisibility(show ? View.GONE : View.VISIBLE);
        contentView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                contentView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        progressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void change() {
        View focusView = null;
        boolean cancel = false;

        String firstname = mFirst.getText().toString();
        String lastname = mLast.getText().toString();

        if (TextUtils.isEmpty(firstname)){
            mFirst.setError(getString(R.string.error_field_required));
            focusView = mFirst;
            cancel = true;
        } else if (TextUtils.isEmpty(lastname)){
            mLast.setError(getString(R.string.error_field_required));
            focusView = mLast;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            apiClient.updateUser(apiClient.getActiveUser(), firstname, lastname);
        }
    }

    public void delete() {
        showProgress(true);

        apiClient.deleteUser(apiClient.getActiveUser());
    }

    private void updateUserInfo(User user) {
        if (user != null) {
            if (user.getPicture() != null && user.getPicture().length() > 0) {
                imageLoader.displayImage(user.getPicture(), profileImageView);
            } else {
                profileImageView.setImageResource(R.drawable.img_default_picture);
            }
            nameTextView.setText(user.getFirstname() + " " + user.getLastname());
            emailTextView.setText(user.getEmail());
            mFirst.setText(user.getFirstname());
            mLast.setText(user.getLastname());
        } else {
            profileImageView.setImageResource(R.drawable.img_default_picture);
            nameTextView.setText("");
            emailTextView.setText("");
            mFirst.setText("");
            mLast.setText("");
        }
    }

    // --- Events ---//

    @Subscribe
    public void onEvent(UserInfoLoadedEvent event) {
        updateUserInfo(event.user);
    }

    @Subscribe
    public void onEvent(NoActiveUserEvent event) {
        updateUserInfo(null);
    }

    @Subscribe
    public void onEvent(UserInfoUpdateFailEvent event) {
        showProgress(false);

        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEvent(UserInfoUpdateSuccessEvent event) {
        showProgress(false);

        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.profile_update_succeded), Toast.LENGTH_LONG).show();
    }

    @Subscribe
    public void onEvent(UserDeleteSuccessEvent event) {
        apiClient.closeSessionAndClearTokenInformation();

        Intent intent = new Intent(getActivity(), IRLoginActivity.class);
        intent.putExtra(IRLoginActivity.EXTRA_SHOULD_LOGOUT_FIRST, true);
        intent.addFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Subscribe
    public void onEvent(UserDeleteFailEvent event) {
        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.error_unknown), Toast.LENGTH_LONG).show();
    }
}
