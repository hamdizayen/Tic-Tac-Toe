package com.example.projetdevmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class PlayerSetupActivity extends AppCompatActivity {
    private EditText etJoueur1;
    private EditText etJoueur2;
    private Button btnCommencer;
    private FallingSymbolsView fallingBg;
    private int nbPartiesTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_setup);

        nbPartiesTotal = getIntent().getIntExtra("NB_PARTIES", 5);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etJoueur1 = findViewById(R.id.etJoueur1);
        etJoueur2 = findViewById(R.id.etJoueur2);
        btnCommencer = findViewById(R.id.btnCommencer);
        fallingBg = findViewById(R.id.fallingBackground);

        fallingBg.startAnimation();
    }

    private void setupListeners() {
        btnCommencer.setOnClickListener(view -> {
            String nomJoueur1 = etJoueur1.getText().toString().trim();
            String nomJoueur2 = etJoueur2.getText().toString().trim();

            if (nomJoueur1.isEmpty() || nomJoueur2.isEmpty()) {
                Toast.makeText(this, "⚠️ Veuillez entrer les deux noms", Toast.LENGTH_SHORT).show();
                return;
            }

            if (nomJoueur1.equals(nomJoueur2)) {
                Toast.makeText(this, "⚠️ Les noms doivent être différents", Toast.LENGTH_SHORT).show();
                return;
            }

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

            view.postDelayed(() -> {
                Intent intent = new Intent(PlayerSetupActivity.this, GameActivity.class);
                intent.putExtra("NB_PARTIES", nbPartiesTotal);
                intent.putExtra("JOUEUR_1", nomJoueur1);
                intent.putExtra("JOUEUR_2", nomJoueur2);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 200);
        });
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