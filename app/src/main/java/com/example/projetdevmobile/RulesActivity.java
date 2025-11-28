package com.example.projetdevmobile;

import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RulesActivity extends AppCompatActivity {
    private Button btnRetour;
    private FallingSymbolsView fallingBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);

        btnRetour = findViewById(R.id.btnRetour);
        fallingBg = findViewById(R.id.fallingBackgroundRules);

        fallingBg.startAnimation();

        btnRetour.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fallingBg.startAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fallingBg.stopAnimation();
    }
}