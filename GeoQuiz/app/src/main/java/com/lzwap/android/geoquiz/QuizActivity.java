package com.lzwap.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWERED = "answered";
    private static final String KEY_CORRECT = "correct";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;
    private int userAnswerCorrect = 0;
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            boolean answerIsAnswered[] = savedInstanceState.getBooleanArray(KEY_ANSWERED);
            for(int i = 0; i < mQuestionBank.length;i++)
            {
                mQuestionBank[i].setAnswered(answerIsAnswered[i]);
            }
            userAnswerCorrect = savedInstanceState.getInt(KEY_CORRECT);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                updateQuestion();
            }
        });
        //int question = mQuestionBank[mCurrentIndex].getTextResId();
        //mQuestionTextView.setText(question);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(QuizActivity.this, R.string.correct_toast,
                // Toast.LENGTH_SHORT).show();
                //Toast toast = Toast.makeText(QuizActivity.this, R.string.correct_toast,
                //        Toast.LENGTH_SHORT);
                //toast.setGravity(Gravity.BOTTOM, 0, 0);
                //toast.show();
                checkAnswer(true);
                showRecord();
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(QuizActivity.this, R.string.incorrect_toast,
                // Toast.LENGTH_SHORT).show();
                //Toast toast = Toast.makeText(QuizActivity.this, R.string.incorrect_toast,
                //        Toast.LENGTH_SHORT);
                //toast.setGravity(Gravity.TOP, 0, 0);
                //toast.show();
                checkAnswer(false);
                showRecord();
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = mCurrentIndex-1 < 0? mQuestionBank.length - 1 : mCurrentIndex - 1;
                updateQuestion();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                //int question = mQuestionBank[mCurrentIndex].getTextResId();
                //mQuestionTextView.setText(question);
                mIsCheater = false;
                updateQuestion();
                showRecord();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start CheatActivity
                //Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this,
                        answerIsTrue);
                //startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        boolean answerIsAnswered[] = new boolean[mQuestionBank.length];
        for (int i = 0; i < mQuestionBank.length; i++) {
            answerIsAnswered[i] = mQuestionBank[i].isAnswered();
        }
        outState.putBooleanArray(KEY_ANSWERED, answerIsAnswered);
        outState.putInt(KEY_CORRECT, userAnswerCorrect);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        checkIfAnswered();
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;

        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                userAnswerCorrect++;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        mQuestionBank[mCurrentIndex].setAnswered(true);
        checkIfAnswered();

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    private void checkIfAnswered() {
        boolean answerIsAnswered = mQuestionBank[mCurrentIndex].isAnswered();

        if (answerIsAnswered) {
            //如果题目被回答，则按键设置不可按下
            mTrueButton.setEnabled(false);
            mFalseButton.setEnabled(false);
        } else {
            //如果题目未被回答，则按键设置为可按
            mTrueButton.setEnabled(true);
            mFalseButton.setEnabled(true);
        }
    }

    private void showRecord() {
        boolean allAnswered = true;
        String message = null;
        double correctMark = 0;  //百分比形式的评分
        //int correctAnswerNum = 0;  //答对的题目数量
        for (int i = 0; i < mQuestionBank.length; i++) {
            if (!mQuestionBank[i].isAnswered()) {
                allAnswered = false;
                break;
            }
        }

        if (allAnswered == true) {
            correctMark = (double)userAnswerCorrect/mQuestionBank.length;
            //保留后两位
            correctMark = (double)((int)(correctMark * 10000)/100.0);
            message = "正确率" + String.valueOf(correctMark) + "%";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
