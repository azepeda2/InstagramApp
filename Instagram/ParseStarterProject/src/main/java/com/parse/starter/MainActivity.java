/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.w3c.dom.Text;

import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    TextView changeSignUpTextView;
    Boolean signupMode = true;
    EditText usernameEditText;
    EditText passwordEditText;

    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.changeToLoginEditText) {
            Button signUpButton = (Button) findViewById(R.id.button1);

            if (signupMode) {
                signupMode = false;
                signUpButton.setText("Login");
                changeSignUpTextView.setText("or Sign up");
            } else {
                signupMode = true;
                signUpButton.setText("Sign Up");
                changeSignUpTextView.setText("or Login");
            }
        } else if (view.getId() == R.id.backgroundRelativeLayout || view.getId() == R.id.logoImageView) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            signUp(v);
        }

        return false;
    }

    public void signUp(View view) {
        if (usernameEditText.getText().toString().matches("") || passwordEditText.getText().toString().matches("")) {
            Toast.makeText(this, "A username and password are required.", Toast.LENGTH_SHORT).show();
        } else {
            if (signupMode) {
                ParseUser user = new ParseUser();

                user.setUsername(usernameEditText.getText().toString());
                user.setPassword(passwordEditText.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.i("Signup", "Successfull");
                            Toast.makeText(getApplicationContext(), usernameEditText.getText().toString() + " has signed up.", Toast.LENGTH_SHORT).show();
                            showUserList();
                        } else if (e.getCode() == 202) {
                            Log.i("Signup", "Failed. Error: " + e.toString());
                            Toast.makeText(getApplicationContext(), "This username already exists.", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("Signup", "Failed. Error: " + e.toString());
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                ParseUser.logInInBackground(usernameEditText.getText().toString(), passwordEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            Log.i("Login", "Successful");
                            Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                            showUserList();
                        } else {
                            Log.i("Login", "Failed. Error: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Instagram");

        RelativeLayout backgroundRelativeLayout = (RelativeLayout) findViewById(R.id.backgroundRelativeLayout);
        ImageView logoImageView = (ImageView) findViewById(R.id.logoImageView);

        backgroundRelativeLayout.setOnClickListener(this);
        logoImageView.setOnClickListener(this);

        usernameEditText = (EditText) findViewById(R.id.usernmaeEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        passwordEditText.setOnKeyListener(this);

        changeSignUpTextView = (TextView) findViewById(R.id.changeToLoginEditText);
        changeSignUpTextView.setOnClickListener(this);

        if (ParseUser.getCurrentUser() != null) {
            showUserList();
        }
    }
}