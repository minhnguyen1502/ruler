package com.example.ruler;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private OneDimensionRulerView rulerView;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rulerView = findViewById(R.id.oneDimensionRulerView);
        resultTextView = findViewById(R.id.resultTextView);

        rulerView.setOnRulerChangeListener(new OneDimensionRulerView.OnRulerChangeListener() {
            @Override
            public void onDistanceChanged(float distance) {
                resultTextView.setText("Distance: " + distance + " " );
            }
        });
    }
}
