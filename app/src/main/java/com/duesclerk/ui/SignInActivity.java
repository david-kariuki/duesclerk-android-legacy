package com.duesclerk.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.duesclerk.R;
import com.duesclerk.ui.fragment_signin.FragmentSignIn;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, FragmentSignIn.newInstance())
                    .commitNow();
        }
    }
}