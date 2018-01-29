package com.example.android.goalist;

/**
 * Created by Pranav on 23-Sep-17.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;


public class LoginOptionActivity extends BaseActivity implements  GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {


    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public  boolean isConnectingToInternet(Context context) {

        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_opt);

        Toasty.Config.getInstance().setTextColor(getResources().getColor(R.color.secondaryColor)).apply();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("users");

        TextView textView1 = (TextView) findViewById(R.id.appnamefont);
        TextView textView2 = (TextView) findViewById(R.id.tag);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Righteous-Regular.ttf");
        textView1.setTypeface(typeface);
        textView2.setTypeface(typeface);

        findViewById(R.id.emailpassword_sign_in_button).setOnClickListener(this);
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        boolean status = isConnectingToInternet(this);
        if(!status){
            Toasty.custom(LoginOptionActivity.this,"No Internet",R.drawable.t_internet,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Toasty.custom(LoginOptionActivity.this,"Authentication failed",R.drawable.t_error,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
                // [END_EXCLUDE]
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // [START_EXCLUDE silent]
        showProgressDialog();
        // [END_EXCLUDE]

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toasty.custom(LoginOptionActivity.this,"Authentication Success",R.drawable.t_success,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toasty.custom(LoginOptionActivity.this,"Authentication failed",R.drawable.t_error,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void updateUI(final FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            showProgressDialog();
            databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        hideProgressDialog();
                        UserExists();
                    }else {
                        hideProgressDialog();
                        UserDoesNotExists();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void UserExists(){
        finish();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    public void UserDoesNotExists(){
        finish();
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toasty.custom(LoginOptionActivity.this,"Google Play Services error",R.drawable.t_error,getResources().getColor(R.color.button_purple),Toast.LENGTH_SHORT,true,true).show();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.google_sign_in_button) {
             signIn();
        } else if (i == R.id.emailpassword_sign_in_button) {
            Intent intent = new Intent(this,EmailPasswordActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
