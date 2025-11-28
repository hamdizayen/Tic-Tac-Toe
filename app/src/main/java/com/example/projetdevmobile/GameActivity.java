package com.example.projetdevmobile;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private TextView tvPartieNumero;
    private TextView tvJoueurXNom;
    private TextView tvJoueurONom;
    private TextView tvScoreX;
    private TextView tvScoreO;
    private TextView tvScoreNul;
    private GridLayout gridLayout;
    private FallingSymbolsView fallingBg;

    private String[][] grille = new String[3][3];
    private String joueurActuel = "X";
    private boolean partieEnCours = true;

    private int partieActuelle = 1;
    private int nbPartiesTotal = 5;
    private int scoreJoueur1 = 0;
    private int scoreJoueur2 = 0;
    private int partiesNulles = 0;

    private String nomJoueur1 = "Joueur 1";
    private String nomJoueur2 = "Joueur 2";
    private boolean joueur1EstX = true;

    private List<Button> buttons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Tournoi en cours");
        }

        nbPartiesTotal = getIntent().getIntExtra("NB_PARTIES", 5);
        nomJoueur1 = getIntent().getStringExtra("JOUEUR_1");
        if (nomJoueur1 == null) nomJoueur1 = "Joueur 1";
        nomJoueur2 = getIntent().getStringExtra("JOUEUR_2");
        if (nomJoueur2 == null) nomJoueur2 = "Joueur 2";

        joueur1EstX = new Random().nextBoolean();
        joueurActuel = "X";

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grille[i][j] = "";
            }
        }

        initViews();
        creerGrille();
        mettreAJourAffichage();
        animateEntrance();
        afficherMessagePremierJoueur();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            afficherConfirmationQuitter();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        tvPartieNumero = findViewById(R.id.tvPartieNumero);
        tvJoueurXNom = findViewById(R.id.tvJoueurXNom);
        tvJoueurONom = findViewById(R.id.tvJoueurONom);
        tvScoreX = findViewById(R.id.tvScoreX);
        tvScoreO = findViewById(R.id.tvScoreO);
        tvScoreNul = findViewById(R.id.tvScoreNul);
        gridLayout = findViewById(R.id.gridLayout);
        fallingBg = findViewById(R.id.fallingBackgroundGame);

        fallingBg.startAnimation();
    }

    private void creerGrille() {
        gridLayout.removeAllViews();
        buttons.clear();

        gridLayout.setColumnCount(3);
        gridLayout.setRowCount(3);

        for (int i = 0; i < 9; i++) {
            Button button = new Button(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = 0;
            params.columnSpec = GridLayout.spec(i % 3, 1f);
            params.rowSpec = GridLayout.spec(i / 3, 1f);
            params.setMargins(12, 12, 12, 12);
            button.setLayoutParams(params);

            button.setTextSize(56f);
            button.setText("");
            button.setTextColor(Color.WHITE);
            button.setElevation(12f);
            button.setBackgroundColor(Color.parseColor("#2C3E50"));

            int row = i / 3;
            int col = i % 3;
            button.setOnClickListener(v -> onCaseClick(row, col, button));

            button.setAlpha(0f);
            button.setScaleX(0f);
            button.setScaleY(0f);

            buttons.add(button);
            gridLayout.addView(button);
        }
    }

    // Suite dans le prochain artifact...
// Continuez à partir de la fin de GameActivity.java (Partie 1)
// Ajoutez ces méthodes à la classe GameActivity

    private void animateEntrance() {
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            button.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(300)
                    .setStartDelay(i * 50L)
                    .setInterpolator(new OvershootInterpolator())
                    .start();
        }
    }

    private void onCaseClick(int ligne, int colonne, Button button) {
        if (!partieEnCours || !grille[ligne][colonne].isEmpty()) {
            button.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(100)
                    .withEndAction(() -> button.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                    .start();
            return;
        }

        grille[ligne][colonne] = joueurActuel;
        button.setText(joueurActuel);

        if (joueurActuel.equals("X")) {
            button.setTextColor(Color.parseColor("#E74C3C"));
            button.setBackgroundColor(Color.parseColor("#C0392B"));
            animateSymbol(button, 360f);
        } else {
            button.setTextColor(Color.parseColor("#3498DB"));
            button.setBackgroundColor(Color.parseColor("#2980B9"));
            animateSymbol(button, -360f);
        }

        if (verifierVictoire(joueurActuel)) {
            // Attribuer le score au JOUEUR réel, pas au symbole
            if (joueurActuel.equals("X")) {
                if (joueur1EstX) {
                    scoreJoueur1++;
                } else {
                    scoreJoueur2++;
                }
            } else {
                if (joueur1EstX) {
                    scoreJoueur2++;
                } else {
                    scoreJoueur1++;
                }
            }

            String nomGagnant = joueurActuel.equals("X")
                    ? (joueur1EstX ? nomJoueur1 : nomJoueur2)
                    : (joueur1EstX ? nomJoueur2 : nomJoueur1);

            partieEnCours = false;
            mettreAJourAffichage();
            afficherVictoireAuCentre(nomGagnant, joueurActuel);

        } else if (estGrillePleine()) {
            partiesNulles++;
            partieEnCours = false;
            mettreAJourAffichage();
            afficherMatchNulAuCentre();

        } else {
            joueurActuel = joueurActuel.equals("X") ? "O" : "X";
            highlightJoueurActuel();
        }
    }

    private void afficherVictoireAuCentre(String nomGagnant, String symbole) {
        highlightWinningCells();

        android.view.View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_victory, null);
        TextView tvMessage = dialogView.findViewById(R.id.tvVictoryMessage);
        TextView tvNom = dialogView.findViewById(R.id.tvVictoryName);
        Button btnContinuer = dialogView.findViewById(R.id.btnContinuer);

        tvMessage.setText(symbole.equals("X") ? "❌ VICTOIRE ! ❌" : "⭕ VICTOIRE ! ⭕");
        tvMessage.setTextColor(symbole.equals("X") ? Color.parseColor("#E74C3C") : Color.parseColor("#3498DB"));
        tvNom.setText(nomGagnant);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().getAttributes().gravity = android.view.Gravity.CENTER;
        }

        dialog.show();

        dialogView.setScaleX(0f);
        dialogView.setScaleY(0f);
        dialogView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();

        btnContinuer.setOnClickListener(v -> {
            dialog.dismiss();
            nouvellePartie();
        });
    }

    private void afficherMatchNulAuCentre() {
        android.view.View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_victory, null);
        TextView tvMessage = dialogView.findViewById(R.id.tvVictoryMessage);
        TextView tvNom = dialogView.findViewById(R.id.tvVictoryName);
        Button btnContinuer = dialogView.findViewById(R.id.btnContinuer);

        tvMessage.setText("⚖️ MATCH NUL ⚖️");
        tvMessage.setTextColor(Color.parseColor("#95A5A6"));
        tvNom.setText("Aucun gagnant");

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().getAttributes().gravity = android.view.Gravity.CENTER;
        }

        dialog.show();

        dialogView.setScaleX(0f);
        dialogView.setScaleY(0f);
        dialogView.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(400)
                .setInterpolator(new OvershootInterpolator())
                .start();

        btnContinuer.setOnClickListener(v -> {
            dialog.dismiss();
            nouvellePartie();
        });
    }

    private void animateSymbol(Button button, float rotation) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(button, "scaleX", 0f, 1.2f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(button, "scaleY", 0f, 1.2f, 1f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(button, "rotation", 0f, rotation);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY, rotate);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new BounceInterpolator());
        animatorSet.start();
    }

    private void highlightJoueurActuel() {
        if (joueurActuel.equals("X")) {
            tvJoueurXNom.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(200)
                    .withEndAction(() -> tvJoueurXNom.animate().scaleX(1f).scaleY(1f).setDuration(200).start())
                    .start();
        } else {
            tvJoueurONom.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(200)
                    .withEndAction(() -> tvJoueurONom.animate().scaleX(1f).scaleY(1f).setDuration(200).start())
                    .start();
        }
    }

    private boolean verifierVictoire(String symbole) {
        // Vérifier les lignes
        for (int i = 0; i <= 2; i++) {
            if (grille[i][0].equals(symbole) && grille[i][1].equals(symbole) && grille[i][2].equals(symbole)) {
                return true;
            }
        }

        // Vérifier les colonnes
        for (int i = 0; i <= 2; i++) {
            if (grille[0][i].equals(symbole) && grille[1][i].equals(symbole) && grille[2][i].equals(symbole)) {
                return true;
            }
        }

        // Vérifier les diagonales
        if (grille[0][0].equals(symbole) && grille[1][1].equals(symbole) && grille[2][2].equals(symbole)) {
            return true;
        }
        if (grille[0][2].equals(symbole) && grille[1][1].equals(symbole) && grille[2][0].equals(symbole)) {
            return true;
        }

        return false;
    }

    private void highlightWinningCells() {
        String symbole = joueurActuel;
        List<Integer> indices = new ArrayList<>();

        for (int i = 0; i <= 2; i++) {
            if (grille[i][0].equals(symbole) && grille[i][1].equals(symbole) && grille[i][2].equals(symbole)) {
                indices.add(i * 3);
                indices.add(i * 3 + 1);
                indices.add(i * 3 + 2);
            }
        }
        for (int i = 0; i <= 2; i++) {
            if (grille[0][i].equals(symbole) && grille[1][i].equals(symbole) && grille[2][i].equals(symbole)) {
                indices.add(i);
                indices.add(i + 3);
                indices.add(i + 6);
            }
        }
        if (grille[0][0].equals(symbole) && grille[1][1].equals(symbole) && grille[2][2].equals(symbole)) {
            indices.add(0);
            indices.add(4);
            indices.add(8);
        }
        if (grille[0][2].equals(symbole) && grille[1][1].equals(symbole) && grille[2][0].equals(symbole)) {
            indices.add(2);
            indices.add(4);
            indices.add(6);
        }

        for (int index : indices) {
            Button button = buttons.get(index);
            button.setBackgroundColor(Color.parseColor("#F39C12"));
            ObjectAnimator animator = ObjectAnimator.ofFloat(button, "alpha", 1f, 0.3f, 1f);
            animator.setDuration(500);
            animator.setRepeatCount(3);
            animator.start();
        }
    }

    private boolean estGrillePleine() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (grille[i][j].isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void nouvellePartie() {
        if (partieActuelle < nbPartiesTotal) {
            partieActuelle++;
            joueur1EstX = new Random().nextBoolean();
            reinitialiserGrille();
            joueurActuel = "X";
            partieEnCours = true;
            mettreAJourAffichage();
            afficherMessagePremierJoueur();
        } else {
            afficherResultatFinal();
        }
    }

    private void afficherMessagePremierJoueur() {
        String nomPremier = joueur1EstX ? nomJoueur1 : nomJoueur2;
        Toast.makeText(this, "🎮 " + nomPremier + " commence avec ❌", Toast.LENGTH_LONG).show();
    }

    private void reinitialiserGrille() {
        grille = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grille[i][j] = "";
            }
        }

        for (Button button : buttons) {
            button.setText("");
            button.setRotation(0f);
            button.setBackgroundColor(Color.parseColor("#2C3E50"));
            button.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(200)
                    .withEndAction(() -> button.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setDuration(200)
                            .setInterpolator(new BounceInterpolator())
                            .start())
                    .start();
        }
    }

    private void mettreAJourAffichage() {
        tvPartieNumero.setText("Partie " + partieActuelle + " / " + nbPartiesTotal);

        if (joueur1EstX) {
            tvJoueurXNom.setText("❌ " + nomJoueur1);
            tvJoueurONom.setText("⭕ " + nomJoueur2);
            tvScoreX.setText(String.valueOf(scoreJoueur1));
            tvScoreO.setText(String.valueOf(scoreJoueur2));
        } else {
            tvJoueurXNom.setText("❌ " + nomJoueur2);
            tvJoueurONom.setText("⭕ " + nomJoueur1);
            tvScoreX.setText(String.valueOf(scoreJoueur2));
            tvScoreO.setText(String.valueOf(scoreJoueur1));
        }

        tvScoreNul.setText("⚪ Nul: " + partiesNulles);

        tvJoueurXNom.setAlpha(joueurActuel.equals("X") ? 1f : 0.5f);
        tvJoueurONom.setAlpha(joueurActuel.equals("O") ? 1f : 0.5f);
    }

    private void afficherResultatFinal() {
        String vainqueur;
        if (scoreJoueur1 > scoreJoueur2) {
            vainqueur = nomJoueur1 + " remporte le tournoi !";
        } else if (scoreJoueur2 > scoreJoueur1) {
            vainqueur = nomJoueur2 + " remporte le tournoi !";
        } else {
            vainqueur = "Égalité parfaite !";
        }

        String message = "📊 SCORES FINAUX\n\n" +
                "🎮 " + nomJoueur1 + ": " + scoreJoueur1 + " victoires\n" +
                "🎮 " + nomJoueur2 + ": " + scoreJoueur2 + " victoires\n" +
                "⚪ Parties nulles: " + partiesNulles + "\n\n" +
                "🏆 " + vainqueur;

        new AlertDialog.Builder(this)
                .setTitle("🎯 FIN DU TOURNOI")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("💾 Sauvegarder", (dialog, which) -> {
                    sauvegarderTournoi(vainqueur);
                    finish();
                })
                .setNegativeButton("🏠 Accueil", (dialog, which) -> finish())
                .show();
    }

    private void sauvegarderTournoi(String vainqueur) {
        try {
            TournoiData tournoi = new TournoiData(scoreJoueur1, scoreJoueur2, partiesNulles, nbPartiesTotal, vainqueur);
            FileOutputStream fos = openFileOutput("tournoi_data.ser", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(tournoi);
            oos.close();
            Toast.makeText(this, "✅ Tournoi sauvegardé !", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "❌ Erreur de sauvegarde", Toast.LENGTH_SHORT).show();
        }
    }

    private void afficherConfirmationQuitter() {
        new AlertDialog.Builder(this)
                .setTitle("⚠️ Attention")
                .setMessage("Si vous quittez maintenant, le tournoi en cours sera perdu et ne sera pas sauvegardé.\n\nÊtes-vous sûr de vouloir quitter ?")
                .setPositiveButton("Oui, quitter", (dialog, which) -> {
                    try {
                        deleteFile("tournoi_data.ser");
                    } catch (Exception e) {
                        // Fichier n'existe pas
                    }
                    finish();
                })
                .setNegativeButton("Non, continuer", (dialog, which) -> dialog.dismiss())
                .show();
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