package com.example.android.goalist;

/**
 * Created by Pranav on 05-Oct-17.
 */

public class User {
    public String displayName;
    public String phoneNumber;
    public String about;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public User(String displayName, String phoneNumber, String about){
        this.displayName=displayName;
        this.phoneNumber=phoneNumber;
        this.about=about;

    }
    public User(){
    }
}
