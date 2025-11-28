package com.example.projetdevmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class MainActivity extends AppCompatActivity {
    private Button btnJouer;
    private Button btnPrincipe;
    private Button btnScores;
    private Spinner spinnerParties;
    private FallingSymbolsView fallingBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupSpinner();
        setupListeners();
        animateEntrance();
    }

    private void initViews() {
        btnJouer = findViewById(R.id.btnJouer);
        btnPrincipe = findViewById(R.id.btnPrincipe);
        btnScores = findViewById(R.id.btnScores);
        spinnerParties = findViewById(R.id.spinnerParties);
        fallingBg = findViewById(R.id.fallingBackground);

        fallingBg.startAnimation();
    }

    private void setupSpinner() {
        String[] parties = {"⚡ 5 parties", "🔥 10 parties", "💎 15 parties"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, parties);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerParties.setAdapter(adapter);
    }

    private void setupListeners() {
        btnJouer.setOnClickListener(view -> {
            animateButton(view);
            view.postDelayed(() -> {
                int nbParties;
                switch (spinnerParties.getSelectedItemPosition()) {
                    case 0: nbParties = 5; break;
                    case 1: nbParties = 10; break;
                    default: nbParties = 15; break;
                }

                Intent intent = new Intent(MainActivity.this, PlayerSetupActivity.class);
                intent.putExtra("NB_PARTIES", nbParties);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 200);
        });

        btnPrincipe.setOnClickListener(view -> {
            animateButton(view);
            view.postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, RulesActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 200);
        });

        btnScores.setOnClickListener(view -> {
            animateButton(view);
            view.postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }, 200);
        });
    }

    private void animateButton(View view) {
        view.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start())
                .start();
    }

    private void animateEntrance() {
        LinearLayout symbolsLayout = findViewById(R.id.symbolsLayout);
        if (symbolsLayout != null) {
            symbolsLayout.setAlpha(0f);
            symbolsLayout.setScaleX(0.5f);
            symbolsLayout.setScaleY(0.5f);
            symbolsLayout.setRotation(-180f);
            symbolsLayout.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .rotation(0f)
                    .setDuration(800)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        }

        View[] views = {btnJouer, btnPrincipe, btnScores, spinnerParties};
        for (int i = 0; i < views.length; i++) {
            View view = views[i];
            view.setAlpha(0f);
            view.setTranslationY(100f);
            view.setScaleX(0.9f);
            view.setScaleY(0.9f);
            view.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(600)
                    .setStartDelay((i * 100 + 400))
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        }

        btnJouer.postDelayed(() -> animatePulse(btnJouer), 1500);
    }

    private void animatePulse(View view) {
        view.animate()
                .scaleX(1.05f)
                .scaleY(1.05f)
                .setDuration(800)
                .withEndAction(() -> view.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(800)
                        .withEndAction(() -> animatePulse(view))
                        .start())
                .start();
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