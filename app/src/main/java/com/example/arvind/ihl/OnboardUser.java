package com.example.arvind.ihl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hololo.tutorial.library.Step;
import com.hololo.tutorial.library.TutorialActivity;

public class OnboardUser extends TutorialActivity {

    private Boolean firstTime = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFirstTime();

        addFragment(new Step.Builder().setTitle("This is header")
                .setContent("This is content")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setSummary("This is summary")
                .build());

        addFragment(new Step.Builder().setTitle("This is header 2")
                .setContent("This is content 2")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setSummary("This is summary")
                .build());

        addFragment(new Step.Builder().setTitle("This is header 3")
                .setContent("This is content 3")
                .setBackgroundColor(Color.parseColor("#FF0957")) // int background color
                .setSummary("This is summary")
                .build());
    }
    @Override
    public void finishTutorial() {
        // Your implementation
        Intent intent=new Intent(OnboardUser.this,Login.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void currentFragmentPosition(int i) {

    }

    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
            else{
                Intent intent=new Intent(OnboardUser.this,Login.class);
                startActivity(intent);
                finish();
            }
        }
        return firstTime;
    }
}

