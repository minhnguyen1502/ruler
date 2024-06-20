package com.example.ruler;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREFS_NAME = "RulerPrefs";
    private static final String PREF_UNIT = "unit";
    private static final String PREF_DISTANCE = "distance";

    private SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUnit(RulerUnit unit) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_UNIT, unit.ordinal());
        editor.apply();
    }

    public RulerUnit getSavedUnit() {
        int unitOrdinal = sharedPreferences.getInt(PREF_UNIT, RulerUnit.MM.ordinal());
        return RulerUnit.values()[unitOrdinal];
    }

    public void saveDistance(float distance) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(PREF_DISTANCE, distance);
        editor.apply();
    }

    public float getSavedDistance() {
        return sharedPreferences.getFloat(PREF_DISTANCE, 0f);
    }
}
