package com.codepath.edurelate.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivitySignUpBinding;
import com.codepath.edurelate.models.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends LoginActivity {

    public static final String TAG = "SignUpActivity";

    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        String username = getIntent().getStringExtra(User.KEY_USERNAME);
        String password = getIntent().getStringExtra(User.KEY_PASSWORD);
        binding.etUsername.setText(username);
        binding.etPassword.setText(password);

        setClickListeners();
    }

    private void setClickListeners() {
        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                String firstName = binding.etFirstName.getText().toString();
                String lastName = binding.etLastName.getText().toString();
                signUpUser(username,password,firstName,lastName);
            }
        });

        binding.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goLoginActivity();
            }
        });
    }

    private void goLoginActivity() {
        Intent i = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(i);
    }


    private void signUpUser(String username,String password,String firstName,String lastName) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG,"Successful sign up");
                    Toast.makeText(SignUpActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                    loginUser(username,password);
                    // Hooray! Let them use the app now.
                } else {
                    Log.i(TAG,"Failed sign up");
                    Toast.makeText(SignUpActivity.this, "Error signing up!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}