package com.rwan.spellproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;

public class PopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);
        startPopUpApplication();
        finish();
    }
    void startPopUpApplication()
    {
        Intent i = new Intent(this, PopSpellActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT|Intent.FLAG_ACTIVITY_MULTIPLE_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        Rect rect = new Rect(100, 800, 900, 700);
        ActivityOptions options = ActivityOptions.makeBasic();
        ActivityOptions bounds = options.setLaunchBounds(rect);
        startActivity(i, bounds.toBundle());
    }
}
