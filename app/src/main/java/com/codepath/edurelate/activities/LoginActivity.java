package com.codepath.edurelate.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.edurelate.R;
import com.codepath.edurelate.databinding.ActivityLoginBinding;
import com.codepath.edurelate.models.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    public static final String KEY_CURRENT_USER = "current_user";
    String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            + "0123456789"
            + "abcdefghijklmnopqrstuvxyz";
    private static final int RC_SIGN_IN = 28;

    private ActivityLoginBinding binding;
    static GoogleSignInClient googleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.googleClientId))
                .requestEmail()
                .build();
        googleClient = GoogleSignIn.getClient(this,gso);

        getEdurelateBot();
        User.googleAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (User.googleAccount != null) {

        }
        if (ParseUser.getCurrentUser() != null) {
            queryCurrUser();
            goHomeActivity();
        }

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                loginUser(username,password);
            }
        });

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString();
                String password = binding.etPassword.getText().toString();
                goSignUpActivity(username,password);
            }
        });

        binding.btnLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserWithGoogle();
            }
        });
    }

    private void queryCurrUser() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include(User.KEY_INTERESTS);
        query.getInBackground(ParseUser.getCurrentUser().getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while querying for current user object: " + e.getMessage(),e);
                    return;
                }
                Log.i(TAG,"Current user found");
                User.currentUser = object;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void goSignUpActivity(String username, String password) {
        Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
        i.putExtra("username",username);
        i.putExtra("password",password);
        startActivity(i);
    }

    protected void loginUserWithGoogle() {
        Intent signInIntent = googleClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    protected void loginUser(String username, String password) {
        Log.i(TAG, "Trying to login in user: " + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    // TODO: Notify the user of the error
                    Toast.makeText(LoginActivity.this,"Error logging in.",Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"Login error occured",e);
                    return;
                }
                queryCurrUser();
                User.currUserGroups = new ArrayList<>();
                goHomeActivity();
                Toast.makeText(LoginActivity.this,"Successful!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void getEdurelateBot() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(User.BOT_OBJECT_ID, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser object, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error getting bot from query.");
                    return;
                }
                Log.i(TAG, "Bot getting successful: " + object.getObjectId());
                User.edurelateBot = object;
            }
        });
    }

    public static void logoutUser(Activity activity) {
        ParseUser.logOut();
        googleClient.signOut()
                .addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG,"Google logout successful");
                    }
                });
        Log.i(TAG,"clear current user data after logout");
        User.clearCurrUserData();
        Intent i = new Intent(activity, LoginActivity.class);
        activity.startActivity(i);
        activity.finish();
        Log.i(TAG,"in login activity after logout");
        User.currentUser = null;
        User.googleAccount = null;
    }

    protected void goHomeActivity() {
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);;
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            User.googleAccount = completedTask.getResult(ApiException.class);
            getParseUser(User.googleAccount);
        }
        catch (ApiException e) {
            Log.e(TAG, "signInResult:failed code=" + e.getStatusCode() + ": " + e.getMessage(),e);
            Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void getParseUser(GoogleSignInAccount googleAccount) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo(User.KEY_EMAIL,googleAccount.getEmail());
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG,"Error while finding parse user for google account: " + e.getMessage(),e);
                    return;
                }
                if (objects.size() > 0) {
                    Log.i(TAG,"A parse user exists for this google account");
                    String username = objects.get(0).getUsername();
                    String password = objects.get(0).getString(User.GOOGLE_PASSWORD);
                    loginUser(username,password);
                }
                signUpWithParse(googleAccount);
            }
        });
    }

    private void signUpWithParse(GoogleSignInAccount googleAccount) {
        String email = googleAccount.getEmail();
        String username = email.substring(0,email.lastIndexOf("@")).toLowerCase();
        String password = getAlphaNumericString(16);
        ParseUser newGoogleUser = new ParseUser();
        newGoogleUser.setUsername(username);
        newGoogleUser.setPassword(password);
        newGoogleUser.setEmail(googleAccount.getEmail());
        newGoogleUser.put(User.KEY_FIRST_NAME,googleAccount.getDisplayName());
        newGoogleUser.put(User.KEY_LAST_NAME,googleAccount.getFamilyName());
        newGoogleUser.put(User.GOOGLE_PASSWORD,password);
        newGoogleUser.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG,"Successful sign up");
                    Toast.makeText(LoginActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                    loginUser(email,password);
                    // Hooray! Let them use the app now.
                } else {
                    Log.i(TAG,"Failed sign up");
                    Toast.makeText(LoginActivity.this, "Error signing up!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getAlphaNumericString(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int)(ALPHANUMERIC_STRING.length() * Math.random());
            sb.append(ALPHANUMERIC_STRING.charAt(index));
        }
        return sb.toString();
    }

    @Override
    public void onBackPressed() {

    }
}