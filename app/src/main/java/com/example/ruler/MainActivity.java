package com.example.ruler;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private OneDimensionRulerView rulerView;
    private TextView distanceTextView;
    private Button buttonMM, buttonCM, buttonIN;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferencesManager = new PreferencesManager(this);

        rulerView = findViewById(R.id.oneDimensionRulerView);
        distanceTextView = findViewById(R.id.distanceTextView);
        buttonMM = findViewById(R.id.buttonMM);
        buttonCM = findViewById(R.id.buttonCM);
        buttonIN = findViewById(R.id.buttonIN);

        // Set the custom listener to update distanceTextView
        rulerView.setOnRulerChangeListener((distance, distanceString) -> {
            distanceTextView.setText(String.format("Distance: %s", distanceString));
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
    }
}
