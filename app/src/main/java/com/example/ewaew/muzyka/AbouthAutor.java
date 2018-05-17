package com.example.ewaew.muzyka;

import android.graphics.drawable.TransitionDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class AbouthAutor extends AppCompatActivity {

    ImageView lightBulb;
    Button authorButton;
    boolean turnedOn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abouth_autor);

        if(savedInstanceState!=null)
        {
            turnedOn = savedInstanceState.getBoolean("turnedOn", false);
        }
        else
            turnedOn = false;

        initialize();
        onClick();
    }

    private void onClick() {
        authorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!turnedOn)
                {
                    lightBulb.setImageResource(R.drawable.trans_off);
                    ((TransitionDrawable) lightBulb.getDrawable()).startTransition(3000);
                    turnedOn = true;
                }
                else
                {
                    lightBulb.setImageResource(R.drawable.trans_on);
                    ((TransitionDrawable) lightBulb.getDrawable()).startTransition(3000);
                    turnedOn = false;
                }

            }
        });
    }

    private void initialize() {
        lightBulb = findViewById(R.id.light);
        authorButton = findViewById(R.id.author_button);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("turnedOn",turnedOn);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.getBoolean("turnedOn");
    }
}
