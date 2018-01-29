package com.example.android.goalist;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Pranav on 29-Sep-17.
 */

public class SplashActivity extends AppCompatActivity {
        // Splash screen timer
        private static int SPLASH_TIME_OUT = 5000;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);
            FirebaseDatabase.getInstance().getReference().keepSynced(true);

            TextView textView = (TextView) findViewById(R.id.splash_text);
            Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Righteous-Regular.ttf");
            textView.setTypeface(typeface);

            /*TextView textView1 = (TextView) findViewById(R.id.by);
            textView1.setTypeface(typeface);

            TextView textView3 = (TextView) findViewById(R.id.name1);
            textView3.setTypeface(typeface);

            TextView textView4 = (TextView) findViewById(R.id.name2);
            textView4.setTypeface(typeface);

            TextView textView5 = (TextView) findViewById(R.id.name3);
            textView5.setTypeface(typeface);

            TextView textView6 = (TextView) findViewById(R.id.name4);
            textView6.setTypeface(typeface);*/

            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    Intent i = new Intent(SplashActivity.this, LoginOptionActivity.class);
                    startActivity(i);

                    // close this activity
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }

}
