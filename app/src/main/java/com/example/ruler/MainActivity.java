package com.example.ruler;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private OneDimensionRulerView rulerView;
    private TextView distanceTextView;
    private Button buttonMM, buttonCM, buttonIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rulerView = findViewById(R.id.oneDimensionRulerView);
        rulerView.restore(getSharedPreferences("RulerPositions", Context.MODE_PRIVATE));

        distanceTextView = findViewById(R.id.distanceTextView);
        buttonMM = findViewById(R.id.buttonMM);
        buttonCM = findViewById(R.id.buttonCM);
        buttonIN = findViewById(R.id.buttonIN);

        // Set the custom listener to update distanceTextView
        rulerView.setOnRulerChangeListener((distance, distanceString) -> {
            distanceTextView.setText(distanceString);
        });

        // Set button click listeners to change the unit and update distanceTextView
        buttonMM.setOnClickListener(view -> {
            rulerView.setUnitAndUpdate(RulerUnit.MM);
        });

        buttonCM.setOnClickListener(view -> {
            rulerView.setUnitAndUpdate(RulerUnit.CM);
        });

        buttonIN.setOnClickListener(view -> {
            rulerView.setUnitAndUpdate(RulerUnit.IN);
        });

        SharedPreferences sharedPreferences = getSharedPreferences("RulerPrefs", Context.MODE_PRIVATE);
        rulerView.restore(sharedPreferences);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("RulerPrefs", MODE_PRIVATE);
        rulerView.save(sharedPreferences);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("RulerPrefs", MODE_PRIVATE);
        rulerView.restore(sharedPreferences);
    }
}
