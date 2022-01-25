package com.mka.trivia;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.mka.trivia.data.Repository;
import com.mka.trivia.databinding.ActivityMainBinding;
import com.mka.trivia.model.Questions;
import com.mka.trivia.model.Score;
import com.mka.trivia.util.Pref;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int index = 0;
    private int arrayIndex;
    private Score score;
    private Animation fadeIn, fadeOut;
    private int scoreCounter;
    private SoundPool soundPool;
    private int rightSound, wrongSound;
    Pref pref;
    List<Questions> questionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        soundPoolSetup();
        score = new Score();
        pref = new Pref(this);
        index = pref.getTitleIndex();
        arrayIndex = pref.getArrayIndex();

        questionsList = new Repository().getQuestions(this::updateTextViews);

        binding.nextButton.setOnClickListener(v -> {
            index += 1;
            arrayIndex = new Random().nextInt(questionsList.size());
            updateTextViews((ArrayList<Questions>) questionsList);
            enableTrueFalseButton(true);

        });
        binding.trueButton.setOnClickListener(v -> checkUserChoice(true));
        binding.falseButton.setOnClickListener(v -> checkUserChoice(false));

    }

    private void soundPoolSetup() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        rightSound = soundPool.load(this, R.raw.dizzy, 0);
        wrongSound = soundPool.load(this, R.raw.operator_error, 0);
    }

    private void setScore(Pref pref) {
        binding.scoreTextView.setText(String.format(Locale.getDefault(), "Score: %d", score.getScore()));
        binding.highestScoreTextView.setText(String.format(Locale.getDefault(), "Highest: %d", pref.getHighestScore()));
    }

    @Override
    protected void onPause() {
        pref.saveHighestScore(score.getScore());
        pref.setArrayIndex(arrayIndex);
        pref.setTitleIndex(index);
        super.onPause();
    }

    private void addScore() {
        scoreCounter += 10;
        score.setScore(scoreCounter);
    }

    private void deductScore() {
        pref.saveHighestScore(score.getScore());
        if (scoreCounter > 0) {
            scoreCounter -= 10;

        } else {
            scoreCounter = 0;
        }

        score.setScore(scoreCounter);


    }

    private void enableTrueFalseButton(boolean onOff) {
        binding.trueButton.setEnabled(onOff);
        binding.falseButton.setEnabled(onOff);

        if (!onOff) {
            binding.trueButton.setAnimation(fadeOut);
            binding.falseButton.setAnimation(fadeOut);
            binding.nextButton.setAnimation(fadeOut);
        } else {
            binding.trueButton.setAnimation(fadeIn);
            binding.falseButton.setAnimation(fadeIn);
            binding.nextButton.setAnimation(fadeIn);
        }


    }

    private void checkUserChoice(boolean userChoice) {
        boolean rightAns = questionsList.get(arrayIndex).isAnswerTrue();
        if (userChoice == rightAns) {
            soundPool.play(rightSound, 1, 1, 0, 0, 1);
            fadeInAnimation();
            addScore();
            enableTrueFalseButton(false);
            Toast.makeText(this.getApplicationContext(), "Right Answer!", Toast.LENGTH_SHORT).show();
        } else {
            soundPool.play(wrongSound, 1, 1, 0, 0, 1);
            shakeAnimation();
            fadeOutAnimation();
            deductScore();
            enableTrueFalseButton(false);
            Toast.makeText(this.getApplicationContext(), "Wrong Answer!", Toast.LENGTH_SHORT).show();
        }
        updateTextViews((ArrayList<Questions>) questionsList);

    }

    private void updateTextViews(ArrayList<Questions> questionsArrayList) {
        if (index == 0) {
            index = 1;
            arrayIndex = new Random().nextInt(questionsList.size());
        }
        binding.questionsTextView.setText(questionsArrayList.get(arrayIndex).getAnswer());
        binding.questionTitleTextView.setText(String.format(Locale.getDefault(),"Question: %d/%d", index, questionsArrayList.size()));
        setScore(pref);
    }

    private void fadeOutAnimation() {
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.layout.setBackgroundColor(Color.BLACK);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.layout.setBackgroundColor(Color.parseColor("#D5390707"));
                binding.nextButton.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fadeInAnimation() {
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        binding.cardView.setAnimation(fadeIn);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.layout.setBackgroundColor(Color.RED);
                binding.questionsTextView.setBackgroundColor(Color.BLACK);
                binding.nextButton.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.layout.setBackgroundColor(Color.parseColor("#D5390707"));
                binding.questionsTextView.setBackgroundColor(Color.TRANSPARENT);
                binding.nextButton.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        binding.cardView.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.questionsTextView.setBackgroundColor(Color.MAGENTA);
                binding.nextButton.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.questionsTextView.setBackgroundColor(Color.TRANSPARENT);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundPool != null) {
            soundPool.release();
        }
        soundPool = null;
    }
}