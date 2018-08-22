package com.lzwap.android.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.lzwap.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN =
            "com.lzwap.android.geoquiz.answer_shown";
    private static final String EXTRA_CHEAT_CHANCE =
            "com.lzwap.android.geoquiz.cheat_chance";
    private static final String QUESTION_SHOWN = "answer_shown";
    private static final String QUESTION_ANSWER = "question_answer";

    private static int mCheatChance = 3;

    private boolean cheatedFlag ;
    private boolean mAnswerIsTrue;
    private String answerText = "Answer";

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;
    private TextView mApiLevelTV;


    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        //解析结果，是否按下作弊按钮
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    //储存是否作弊的标志，以免在旋转之后清除(还可以把答案也存起来，暂时没编，同理)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(QUESTION_SHOWN, cheatedFlag);
        outState.putString(QUESTION_ANSWER, answerText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        if (savedInstanceState != null) {
            cheatedFlag = savedInstanceState.getBoolean(QUESTION_SHOWN);
            setAnswerShownResult(cheatedFlag);
            answerText = savedInstanceState.getString(QUESTION_ANSWER);
        }

        //省略Intent intent = getIntent();
        mCheatChance = getIntent().getIntExtra(EXTRA_CHEAT_CHANCE, 3);
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        mAnswerTextView.setText(answerText);

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheatChance--;
                if (mAnswerIsTrue) {
                    mAnswerTextView.setText(R.string.true_button);
                    answerText = getResources().getString(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                    answerText = getResources().getString(R.string.false_button);
                }
                setAnswerShownResult(true);
                cheatedFlag = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        mApiLevelTV = (TextView) findViewById(R.id.show_api_level);
        CharSequence cs = "API Level " + Build.VERSION.SDK_INT;
        mApiLevelTV.setText(cs);
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        data.putExtra(EXTRA_CHEAT_CHANCE, mCheatChance);
        setResult(RESULT_OK, data);
    }
}
