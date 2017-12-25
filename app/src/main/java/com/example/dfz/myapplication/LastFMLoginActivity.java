package com.example.dfz.myapplication;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dfz.myapplication.MUtils.LastFMUtil;
import com.example.dfz.myapplication.Service.MusicService;
import com.vpaliy.last_fm_api.auth.LastFmAuth;
import com.vpaliy.last_fm_api.model.Response;
import com.vpaliy.last_fm_api.model.Session;

import org.w3c.dom.Text;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class LastFMLoginActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsername;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    SharedPreferences pref;
    private TextInputLayout mUsernameLayout;
    private TextInputLayout mPasswordLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_last_fmlogin);

        mUsernameLayout = findViewById(R.id.username_input_layout);
        mPasswordLayout = findViewById(R.id.password_input_layout);

        pref = getSharedPreferences(PREFS_NAME, 0);
        setupActionBar();
        // Set up the login form.
        mUsername = findViewById(R.id.username);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);



    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        // Show the Up button in the action bar.
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
//        mUsername.setError(null);
//        mPasswordView.setError(null);
        mUsernameLayout.setError(null);
        mPasswordLayout.setError(null);


        // Store values at the time of the login attempt.
        String username = mUsername.getText().toString();
        String password = mPasswordView.getText().toString();

        showProgress(true);
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setIndeterminate(true);
//        progressDialog.setMessage("Authenticating...");
//        progressDialog.show();

        mAuthTask = new UserLoginTask(username, password);
        mAuthTask.execute((Void) null);

    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

//        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
//                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//            }
//        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private String mUsername;
        private String mPassword;

        UserLoginTask(String Username, String password) {
            mUsername = Username;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final boolean[] returnValue = {false};


            Response<Session> error = new Response<>();
            error.error = 1;


            LastFmAuth.create(LastFMUtil.API_KEY, LastFMUtil.SHARED_SECERT)
                    .auth(LastFMLoginActivity.this, mUsername, mPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).onErrorReturnItem(error)
                    .subscribe(response -> {
                        if (response.error == 1) {
//                            Toast.makeText(LastFMLoginActivity.this, "log in fail", Toast.LENGTH_LONG).show();
                            returnValue[0] = false;
                        } else {
                            Session session = response.result;
//                            Toast.makeText(LastFMLoginActivity.this, "" + session, Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("key", Session.convertToString(session)).apply();
                            editor.putString("username", mUsername).apply();
                            returnValue[0] = true;
                        }

                    });

            try {
                // Simulate network access.
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                return false;
            }

            LastFMUtil.isLogin=returnValue[0];
            return returnValue[0];

        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(LastFMLoginActivity.this, LastFMActivity.class);

                startActivity(intent);
            } else {
//                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordLayout.setError("This password is incorrect");
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

