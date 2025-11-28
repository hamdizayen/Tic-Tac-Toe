package com.example.projetdevmobile;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class ResultsActivity extends AppCompatActivity {
    private TextView tvScoreXValue;
    private TextView tvScoreOValue;
    private TextView tvNulValue;
    private TextView tvTotalValue;
    private TextView tvVainqueur;
    private Button btnRetour;
    private FallingSymbolsView fallingBg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        initViews();
        chargerResultats();
    }

    private void initViews() {
        tvScoreXValue = findViewById(R.id.tvScoreXValue);
        tvScoreOValue = findViewById(R.id.tvScoreOValue);
        tvNulValue = findViewById(R.id.tvNulValue);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvVainqueur = findViewById(R.id.tvVainqueur);
        btnRetour = findViewById(R.id.btnRetourResults);
        fallingBg = findViewById(R.id.fallingBackgroundResults);

        fallingBg.startAnimation();

        btnRetour.setOnClickListener(v -> finish());
    }

    private void chargerResultats() {
        try {
            FileInputStream fis = openFileInput("tournoi_data.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            TournoiData tournoi = (TournoiData) ois.readObject();
            ois.close();

            tvScoreXValue.setText(String.valueOf(tournoi.getScoreX()));
            tvScoreOValue.setText(String.valueOf(tournoi.getScoreO()));
            tvNulValue.setText(String.valueOf(tournoi.getPartiesNulles()));
            tvTotalValue.setText(String.valueOf(tournoi.getTotalParties()));
            tvVainqueur.setText(tournoi.getVainqueur());

        } catch (Exception e) {
            tvScoreXValue.setText("-");
            tvScoreOValue.setText("-");
            tvNulValue.setText("-");
            tvTotalValue.setText("-");
            tvVainqueur.setText("Aucun tournoi sauvegardé");
        }
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