package com.example.android.goalist;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * Created by DELL on 21-10-2017.
 */

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar_about);
        myToolbar.setTitle("About");
        setSupportActionBar(myToolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.about_layout);
        ImageView member1 = (ImageView) findViewById(R.id.pranav_image);

        Picasso.with(AboutActivity.this).load(R.drawable.pranav).transform(new CircleTransformActivity()).fit().centerCrop().into(member1);

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setDescription("Goal-ist is a efficient way of connecting with your teammates and planning for a long run by using task manager listing.")
                .addItem(new Element().setTitle("Version 1.0"))
                .addGroup("Connect with us")
                .addEmail("pranavparge@gmail.com")
                .addTwitter("pranavparge")
                .create();
        aboutPage.setBackgroundColor(Color.parseColor("#000000"));
        linearLayout.addView(aboutPage,0);
    }
}
