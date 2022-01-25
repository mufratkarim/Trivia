package com.mka.trivia.data;

import com.mka.trivia.model.Questions;

import java.util.ArrayList;

public interface AnswerListAsyncResponse {
    void processFinished(ArrayList<Questions> questionsArrayList);
}
