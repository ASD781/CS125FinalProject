package com.example.cs125finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final int MAIN_REQUEST_CODE = 123;
    String diff = "";

    TextView tDifficulty;
    Button bStart, bSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tDifficulty = findViewById(R.id.id_tDifficulty);
        bStart = findViewById(R.id.id_bStart);
        bSettings = findViewById(R.id.id_bSettings);

        setDifficulty("Easy");
        bStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
                gameIntent.putExtra("difficulty", diff);
                startActivity(gameIntent);
                finish();
            }
        });
        bSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(settingsIntent, MAIN_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == MAIN_REQUEST_CODE) {
            setDifficulty(data.getStringExtra("newDifficulty"));
        }
    }

    public void setDifficulty(String str) {
        diff = str;
        tDifficulty.setText("Difficulty: " + diff);
    }
}
