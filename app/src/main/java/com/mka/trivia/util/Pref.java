package com.mka.trivia.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Pref {

    private static final String HIGHEST_SCORE = "highest_score";
    private static final String ARRAY_INDEX = "array_index";
    private static final String TITLE_INDEX = "title_index";
    private SharedPreferences preferences;

    public Pref(Activity context) {
        this.preferences = context.getPreferences(Context.MODE_PRIVATE);
    }

    public void saveHighestScore(int currentScore) {

        int lastScore = preferences.getInt(HIGHEST_SCORE, 0);

        if (currentScore > lastScore) {
            preferences.edit().putInt(HIGHEST_SCORE, currentScore).apply();
        }

    }

    public void setArrayIndex(int arrayIndex) {
        preferences.edit().putInt(ARRAY_INDEX, arrayIndex).apply();
    }

    public int getArrayIndex() {
        return preferences.getInt(ARRAY_INDEX, 0);
    }

    public int getTitleIndex() {
        return preferences.getInt(TITLE_INDEX, 0);
    }

    public void setTitleIndex(int titleIndex) {
        preferences.edit().putInt(TITLE_INDEX, titleIndex).apply();
    }



    public int getHighestScore() {
        return preferences.getInt(HIGHEST_SCORE, 0);
    }
}
