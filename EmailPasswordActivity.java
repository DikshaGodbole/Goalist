package com.example.android.goalist;

/**
 * Created by Pranav on 23-Sep-17.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import es.dmoral.toasty.Toasty;
public class EmailPasswordActivity extends BaseActivity implements
        View.OnClickListener {

    public static final String TAG = "EmailPassword";

    public EditText mEmailField;
    public EditText mPasswordField;

    // [START declare_auth]
    public FirebaseAuth mAuth;
    // [END declare_auth]
    public String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coodinator);

        Toasty.Config.getInstance().setTextColor(getResources().getColor(R.color.secondaryColor)).apply();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_email);
        myToolbar.setTitle("Email Authentication");
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);


        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    public void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            sendEmailVerification();
                            Toasty.custom(EmailPasswordActivity.this,"Success",R.drawable.t_success,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
                            profileActivity();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toasty.custom(EmailPasswordActivity.this,"Authentication Failed | Check Fields",R.drawable.t_error,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    public void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toasty.custom(EmailPasswordActivity.this,"Authentication Failed | Invalid User",R.drawable.t_error,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
                        }
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }


    public void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        if (task.isSuccessful()) {
                            Toasty.custom(EmailPasswordActivity.this,"Verification email sent to"+ user.getEmail(),R.drawable.t_success,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toasty.custom(EmailPasswordActivity.this,"Failed to Send Verification",R.drawable.t_error,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    public boolean validateForm() {
        boolean valid = true;

        email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    public void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            finish();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

    public void profileActivity() {
            Intent intent_profile = new Intent(this,ProfileActivity.class);
            startActivity(intent_profile);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}