package com.example.cs125finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends AppCompatActivity {

    Button[] bDiffArr = new Button [3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        bDiffArr[0] = findViewById(R.id.id_bEasy);
        bDiffArr[1] = findViewById(R.id.id_bMedium);
        bDiffArr[2] = findViewById(R.id.id_bHard);

        for (int i = 0; i < bDiffArr.length; i++) {
            bDiffArr[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent backIntent = new Intent();
                    backIntent.putExtra("newDifficulty", ((Button) view).getText().toString());
                    setResult(RESULT_OK, backIntent);
                    finish();
                }
            });
        }
    }
}
