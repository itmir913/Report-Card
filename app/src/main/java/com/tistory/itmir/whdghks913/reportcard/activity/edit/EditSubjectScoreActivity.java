package com.tistory.itmir.whdghks913.reportcard.activity.edit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.tool.Database;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

public class EditSubjectScoreActivity extends AppCompatActivity {
    private int _id, subjectId;

    private Database mDatabase;
    private EditText mScore, mClass, mRank, mApplicants;
    private TextInputLayout mScoreTextInputLayout, mApplicantsTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam_score_data);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        ActionBar mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayHomeAsUpEnabled(true);

            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        mDatabase = new Database();
        mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);

        Intent mIntent = getIntent();
        _id = mIntent.getIntExtra("_id", 0);
        subjectId = mIntent.getIntExtra("subjectId", 0);
        String name = mIntent.getStringExtra("name");

        Cursor mCursor = mDatabase.getData(ExamDataBaseInfo.subjectTableName, "color", "_id", subjectId);
        mCursor.moveToNext();

        int color = mCursor.getInt(0);

        View mSubjectColorCircleView = findViewById(R.id.mSubjectColorCircleView);
        GradientDrawable subjectColorGradient = (GradientDrawable) mSubjectColorCircleView.getBackground();
        subjectColorGradient.setColor(color);

        Spinner mSubjectSpinner = (Spinner) findViewById(R.id.mSubjectSpinner);
        ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{name});
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSubjectSpinner.setAdapter(mAdapter);

        mScore = (EditText) findViewById(R.id.mScore);
        mClass = (EditText) findViewById(R.id.mClass);
        mRank = (EditText) findViewById(R.id.mRank);
        mApplicants = (EditText) findViewById(R.id.mApplicants);
        findViewById(R.id.removeButton).setVisibility(View.VISIBLE);

        mScore.setText(String.valueOf(mIntent.getFloatExtra("score", 0)));
        mClass.setText(String.valueOf(mIntent.getIntExtra("mClass", 0)));
        mRank.setText(String.valueOf(mIntent.getIntExtra("rank", 0)));
        mApplicants.setText(String.valueOf(mIntent.getIntExtra("applicants", 0)));

        mScoreTextInputLayout = (TextInputLayout) findViewById(R.id.mScoreTextInputLayout);
        mApplicantsTextInputLayout = (TextInputLayout) findViewById(R.id.mApplicantsTextInputLayout);

        mScoreTextInputLayout.setErrorEnabled(true);
        mApplicantsTextInputLayout.setErrorEnabled(true);
    }

    public void remove(View v) {
        if (mDatabase == null) {
            mDatabase = new Database();
            mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);
        }

        mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);

        mDatabase.remove(ExamDataBaseInfo.getExamTable(_id), "name", subjectId);
        mDatabase.release();

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            String scoreText = mScore.getText().toString();
            String classText = mClass.getText().toString();
            String rankText = mRank.getText().toString();
            String applicantsText = mApplicants.getText().toString();

            /**
             * private EditText mScore, mClass, mRank, mApplicants;
             * private TextInputLayout mScoreTextInputLayout, mClassTextInputLayout, mRankTextInputLayout, mApplicantsTextInputLayout;
             */
            if (scoreText.isEmpty() || scoreText.length() == 0 || (scoreText.replaceAll("\\s", "")).length() == 0) {
                mScoreTextInputLayout.setError("받은 점수는 필수로 입력해야 합니다.");
                return true;
            }

            float score = Float.parseFloat(scoreText);
            int mClass = (classText.isEmpty() || classText.length() == 0 || (classText.replaceAll("\\s", "")).length() == 0) ? 0 : Integer.parseInt(classText);
            int rank = (rankText.isEmpty() || rankText.length() == 0 || (rankText.replaceAll("\\s", "")).length() == 0) ? 0 : Integer.parseInt(rankText);
            int applicants = (applicantsText.isEmpty() || applicantsText.length() == 0 || (applicantsText.replaceAll("\\s", "")).length() == 0) ? 0 : Integer.parseInt(applicantsText);

            if (rank > applicants) {
                mApplicantsTextInputLayout.setError("전체 응시자 수는 과목 석차수보다 커야 합니다.");
                return true;
            }

            if (mDatabase == null) {
                mDatabase = new Database();
                mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);
            }

            mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);

            mDatabase.update(ExamDataBaseInfo.getExamTable(_id), "score", score, "name", subjectId);
            mDatabase.update(ExamDataBaseInfo.getExamTable(_id), "rank", rank, "name", subjectId);
            mDatabase.update(ExamDataBaseInfo.getExamTable(_id), "applicants", applicants, "name", subjectId);
            mDatabase.update(ExamDataBaseInfo.getExamTable(_id), "class", mClass, "name", subjectId);

            mDatabase.release();

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
