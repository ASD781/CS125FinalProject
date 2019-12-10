package com.example.cs125finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class GameOverActivity extends AppCompatActivity {
    Button bRestart, bMainMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TAG", "GAME OVER");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        bRestart = findViewById(R.id.id_bRestart);
        bMainMenu = findViewById(R.id.id_bMainMenu);

        bRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gameIntent = new Intent(GameOverActivity.this, GameActivity.class);
                startActivity(gameIntent);
                finish();
            }
        });

        bMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(GameOverActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

    }
}
