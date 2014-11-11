package com.irewind.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.irewind.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class IRForgotPasswordActivity extends Activity implements View.OnClickListener {

    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.submit)
    Button mSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irforgot_password);
        ButterKnife.inject(this);

        mSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                Intent intent = new Intent(this, IRLoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}
