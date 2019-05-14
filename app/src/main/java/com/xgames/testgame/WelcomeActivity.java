package com.xgames.testgame;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {

    Settings settings;
    HighScores easy,medium,hard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        settings = new Settings(this);
        easy = new HighScores("HighScoreEasy",this);
        medium = new HighScores("HighScoreMedium",this);
        hard = new HighScores("HighScoreHard",this);

        if(!settings.getApplyChanges())
            settings.setDefaultData();

        if(!easy.isInserted())
            easy.setDefaultData();

        if(!medium.isInserted())
            medium.setDefaultData();

        if(!hard.isInserted())
            hard.setDefaultData();

        openMenu();
    }

    private void openMenu(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(WelcomeActivity.this, MainMenuActivity.class);
                startActivity(i);
            }
        }, 1500);
    }
}
