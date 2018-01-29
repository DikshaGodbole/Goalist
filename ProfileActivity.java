package com.example.android.goalist;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import es.dmoral.toasty.Toasty;

/**
 * Created by Pranav on 23-Sep-17.
 */

public class ProfileActivity extends BaseActivity implements View.OnClickListener{

    private   ProgressDialog mProgressDialog;
    private   FirebaseDatabase database;
    private   DatabaseReference databaseReference;
    private   FirebaseAuth mAuth;
    private   StorageReference mStorageReference;
    private static final int GALLERY_INTENT=2;

    private EditText eDisplay;
    private EditText ePhone;
    private EditText eAbout;
    private TextView eEmail;
    private ImageView profile_photo;
    private ImageButton profile_photo_button;

    public String userId;
    EmailPasswordActivity emailPasswordActivity = new EmailPasswordActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mProgressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference().child("users");

        mStorageReference = FirebaseStorage.getInstance().getReference();

        eDisplay = (EditText) findViewById(R.id.field_displayName);
        ePhone = (EditText) findViewById(R.id.field_phone);
        eAbout = (EditText) findViewById(R.id.field_about);
        eEmail = (TextView) findViewById(R.id.display_email);
        profile_photo_button = (ImageButton) findViewById(R.id.button_image);

        findViewById(R.id.button_done).setOnClickListener(this);
        findViewById(R.id.button_image).setOnClickListener(this);

    }

    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null) {
                userId = user.getUid();
                String email = user.getEmail();
                eEmail.setText("@"+email+"!");
        }
        else {
            Toasty.custom(ProfileActivity.this,"Restart",R.drawable.t_error,getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT,true,true).show();
            Intent intent = new Intent(this,LoginOptionActivity.class);
            startActivity(intent);
        }
    }

    private boolean validateForm() {
        boolean valid = true;

        String displayName = eDisplay.getText().toString();
        if (TextUtils.isEmpty(displayName)) {
            eDisplay.setError("Required.");
            valid = false;
        } else {
            eDisplay.setError(null);
        }

        String phone = ePhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            ePhone.setError("Required.");
            valid = false;
        } else {
            ePhone.setError(null);
        }

        String about = eAbout.getText().toString();
        if (TextUtils.isEmpty(about)) {
            eAbout.setError("Required.");
            valid = false;
        } else {
            eAbout.setError(null);
        }

        return valid;
    }

        @Override
        public void onClick(View v) { //OPEN BELOW COMMENTS FOR SECURITY
            int i = v.getId();
            if (i == R.id.button_done) {
                if(validateForm()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        //if (user.isEmailVerified()) {
                            findViewById(R.id.button_done).setEnabled(true);
                            databaseReference.child(userId).setValue(new User(eDisplay.getText().toString(), ePhone.getText().toString(), eAbout.getText().toString()));
                            Intent intent = new Intent(this,MainActivity.class);
                            startActivity(intent);
                        /*} else {
                            //findViewById(R.id.button_done).setEnabled(false);
                            Toasty.custom(ProfileActivity.this, "Verify Email to Proceed", R.drawable.t_error, getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT, true, true).show();
                        }*/
                    }
                }
                else
                    Toasty.custom(ProfileActivity.this,"Authentication Failed | Check Fields",R.drawable.t_error,getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT,true,true).show();
            }
            else if(i==R.id.button_image){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            mProgressDialog.setMessage("Uploading");
            mProgressDialog.show();

            Uri uri = data.getData();
            StorageReference filepath = mStorageReference.child("profilePhotos").child(userId).child("photo");
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toasty.custom(ProfileActivity.this,"Uploaded!",R.drawable.t_success,getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT,true,true).show();
                    mProgressDialog.hide();
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Picasso.with(ProfileActivity.this).load(downloadUri).transform(new CircleTransformActivity()).fit().centerCrop().into(profile_photo_button);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toasty.custom(ProfileActivity.this,"Failed!",R.drawable.t_error,getResources().getColor(R.color.button_purple), Toast.LENGTH_SHORT,true,true).show();
                    mProgressDialog.hide();
                }
            });
        }
    }
}