package com.mka.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mka.trivia.controller.AppController;
import com.mka.trivia.model.Questions;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Repository {

    ArrayList<Questions> questionsArrayList = new ArrayList<>();

    String url = "https://raw.githubusercontent.com/mufratkarim/mufratkarim/main/simple-random-quiz.json";

    public List<Questions> getQuestions(final AnswerListAsyncResponse callback) {

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    Questions questions = new Questions(response.getJSONArray(i).getString(0), response.getJSONArray(i).getBoolean(1));
                    questionsArrayList.add(questions);

                    //Log.d("Repo: ", "onCreate: " + questionsArrayList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (callback != null) callback.processFinished(questionsArrayList);

        }, error -> {
            Log.d("Repo: ", "onCreate: failed");
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);

        return questionsArrayList;
    }
}
