package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class QuizActivity extends AppCompatActivity {
    double answer=0.0;
    double finalanswer=0.0;
    private Button mSubmitButton;
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private Button mCheatButton;
    private TextView questionView;
    private boolean mIsCheater;
    private static final int REQUEST_CODE_CHEAT=0;
    private static final String TAG ="QuizActivity";
    private static final String KEY_INDEX = "index";
    private TextView mQuestionTextView;

    private Question[] mQuestionBank=new Question[]
            {
                    new Question(R.string.question_india, true),
                    new Question(R.string.question_oceans, true),
                    new Question(R.string.question_africa, false),
                    new Question(R.string.question_continent, true),
                    new Question(R.string.question_capital,false)};
    private int mCurrentIndex=0;
    private boolean[] mQuestionsAnswered =new boolean[mQuestionBank.length];
             @Override
    protected void onCreate(Bundle savedInstanceState) {

                 FirebaseDatabase database = FirebaseDatabase.getInstance();
                 DatabaseReference myRef = database.getReference("message");

                 myRef.setValue("Hello, World!");
                 myRef.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         // This method is called once with the initial value and again
                         // whenever data at this location is updated.x
                         String value = dataSnapshot.getValue(String.class);
                         Log.d(TAG, "Value is: " + value);
                     }

                     @Override
                     public void onCancelled(DatabaseError error) {
                         // Failed to read value
                         Log.w(TAG, "Failed to read value.", error.toException());
                     }
                 });
                 super.onCreate(savedInstanceState);
                 Log.d(TAG, "onCreate(Bundle) called");
                 setContentView(R.layout.activity_quiz);
                 AdView mAdView;
                 MobileAds.initialize(this,
                         "ca-app-pub-3940256099942544~3347511713");
                 mAdView = findViewById(R.id.adView);
                 AdRequest adRequest = new AdRequest.Builder().build();
                 mAdView.loadAd(adRequest);
                 if (savedInstanceState != null) {
                     mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
                     mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
                     mQuestionsAnswered = savedInstanceState.getBooleanArray(KEY_INDEX);
                 }
                 mQuestionTextView = (TextView)
                         findViewById(R.id.question_text_view);

                 mTrueButton = (Button) findViewById(R.id.true_button);
                 mTrueButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         checkAnswer(true);

                     }

                 });


                 mFalseButton=(Button)findViewById(R.id.false_button);
                 mFalseButton.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                 checkAnswer(false);

                   }
                });

                 mNextButton = (ImageButton) findViewById(R.id.next_button);
                 mNextButton.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                                  mIsCheater=false;
                                  updateQuestion();
                              }
                 });

                 questionView = (TextView) findViewById(R.id.question_text_view);
                 int question =mQuestionBank[mCurrentIndex].getTextResId();
                 questionView.setText(question);
                 questionView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                         updateQuestion();
                     }
                 });



                 mPrevButton = (ImageButton) findViewById(R.id.prev_button);
                 mPrevButton.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {

                                 mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
                                 updateQuestion();
                             }
                 });
                 mSubmitButton=(Button)findViewById(R.id.submit_button);
                 mSubmitButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         submit(answer);

                     }
                 });
                 mCheatButton = (Button)findViewById(R.id.cheat_button);
                 mCheatButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                         Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                         startActivityForResult(intent,REQUEST_CODE_CHEAT); }        });


                 updateQuestion();


             }

               @Override
             protected void onActivityResult(int requestCode, int resultCode, Intent data){
                 if(resultCode!= Activity.RESULT_OK){
                     return;
                 }
                 if(requestCode==REQUEST_CODE_CHEAT) {
                     if (data == null) {
                         return;
                     }
                     mIsCheater = CheatActivity.wasAnswerShown(data);
                 }
                 }



    private void updateQuestion() {
                 int question = mQuestionBank[mCurrentIndex].getTextResId();
                 mQuestionTextView.setText(question);
                 mTrueButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);
                 mFalseButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);

             }
    private void checkAnswer(boolean userPressedTrue) {
                 boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                 int messageResId;
                 if (mIsCheater) {
                     messageResId = R.string.judgment_toast;

                 }
                 else {
                     if (userPressedTrue == answerIsTrue) {
                         answer++;
                         messageResId = R.string.correct_toast;

                     } else {
                         messageResId = R.string.incorrect_toast;
                     }
                 }
                 Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
                 mQuestionsAnswered[mCurrentIndex] = true;
                 mTrueButton.setEnabled(false);
                 mFalseButton.setEnabled(false);


    }
             private void submit(double answer)
             {
               finalanswer =(answer/mQuestionBank.length)*100;

               Toast.makeText(this,"You scored  "+(double)Math.round(finalanswer) ,Toast.LENGTH_LONG).show();
                 mTrueButton.setEnabled(false);
                 mFalseButton.setEnabled(false);
                 mCheatButton.setEnabled(false);
                 mNextButton.setEnabled(false);
                 mPrevButton.setEnabled(false);
             }
    @Override
    public void onStart() {
                 super.onStart();
                 Log.d(TAG, "onStart() called");
             }
    @Override
    public void onResume() {
                 super.onResume();
                 Log.d(TAG, "onResume() called");
             }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }
        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
            super.onSaveInstanceState(savedInstanceState);
            Log.i(TAG, "onSaveInstanceState");
            savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
            savedInstanceState.putBooleanArray(KEY_INDEX,mQuestionsAnswered);
        }

    @Override
    public void onStop() {
                 super.onStop();
                 Log.d(TAG, "onStop() called");
             }
    @Override
    public void onDestroy() {
                 super.onDestroy();
                 Log.d(TAG, "onDestroy() called");
             }
}
