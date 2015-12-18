package com.tistory.itmir.whdghks913.reportcard.activity.modify;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.tool.Database;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExamScoreActivity extends AppCompatActivity {
    /**
     * type
     */
    private int type;

    /**
     * Create type
     */
    private int _id;
    private ArrayList<ExamDataBaseInfo.subjectData> subjectExamData = new ArrayList<>();

    private GradientDrawable subjectColorGradient;

    private Spinner mSubjectSpinner;
    private EditText mScore, mClass, mRank, mApplicants, mAverage, mStandardDeviation;
    private TextInputLayout mScoreTextInputLayout, mApplicantsTextInputLayout;

    /**
     * Edit Type
     */
    private int subjectId;
    private Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam_score_data);

        Intent mIntent = getIntent();
        type = mIntent.getIntExtra("type", 0);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        if (type == 1) mToolbar.setTitle(getString(R.string.title_activity_edit_subject_data));
        setSupportActionBar(mToolbar);

        _id = mIntent.getIntExtra("_id", 0);
        if (type == 1) {
            subjectId = mIntent.getIntExtra("subjectId", 0);
        }

        if (_id == 0) {
            // ERROR
            return;
        }

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

        mScore = (EditText) findViewById(R.id.mScore);
        mClass = (EditText) findViewById(R.id.mClass);
        mRank = (EditText) findViewById(R.id.mRank);
        mApplicants = (EditText) findViewById(R.id.mApplicants);
        mAverage = (EditText) findViewById(R.id.mAverage);
        mStandardDeviation = (EditText) findViewById(R.id.mStandardDeviation);
        mSubjectSpinner = (Spinner) findViewById(R.id.mSubjectSpinner);

        mScoreTextInputLayout = (TextInputLayout) findViewById(R.id.mScoreTextInputLayout);
        mApplicantsTextInputLayout = (TextInputLayout) findViewById(R.id.mApplicantsTextInputLayout);

        mScoreTextInputLayout.setErrorEnabled(true);
        mApplicantsTextInputLayout.setErrorEnabled(true);

        View mSubjectColorCircleView = findViewById(R.id.mSubjectColorCircleView);
        subjectColorGradient = (GradientDrawable) mSubjectColorCircleView.getBackground();

        if (type == 0) {
            getSubjectList();

            if (subjectExamData.size() == 0 || subjectExamData == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatErrorAlertDialogStyle);
                builder.setTitle(R.string.not_exists_subject_title);
                builder.setMessage(R.string.not_exists_subject_message);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent mIntent = new Intent(getApplicationContext(), SubjectActivity.class);
                        mIntent.putExtra("type", 0);
                        startActivity(mIntent);
                        finish();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
                builder.setCancelable(false);
                builder.show();

                return;
            }

            String[] mSubjectName = new String[subjectExamData.size()];
            for (int i = 0; i < mSubjectName.length; i++) {
                mSubjectName[i] = subjectExamData.get(i).name;
            }

            ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, mSubjectName);
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSubjectSpinner.setAdapter(mAdapter);
            mSubjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                    subjectColorGradient.setColor(subjectExamData.get(position).color);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            mSubjectSpinner.setSelection(0);

        } else if (type == 1) {
            Cursor mCursor = mDatabase.getData(ExamDataBaseInfo.subjectTableName, "color", "_id", subjectId);
            mCursor.moveToNext();

            int color = mCursor.getInt(0);
            subjectColorGradient.setColor(color);

            ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{mIntent.getStringExtra("name")});
            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSubjectSpinner.setAdapter(mAdapter);

            findViewById(R.id.removeButton).setVisibility(View.VISIBLE);

            mScore.setText(String.valueOf(mIntent.getFloatExtra("score", 0)));
            mClass.setText(String.valueOf(mIntent.getIntExtra("mClass", 0)));
            mRank.setText(String.valueOf(mIntent.getIntExtra("rank", 0)));
            mApplicants.setText(String.valueOf(mIntent.getIntExtra("applicants", 0)));
            mAverage.setText(String.valueOf(mIntent.getFloatExtra("average", 0)));
            mStandardDeviation.setText(String.valueOf(mIntent.getFloatExtra("standardDeviation", 0)));
        }
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

    private void getSubjectList() {
        ArrayList<Integer> addedSubject = new ArrayList<>();
        ArrayList<ExamDataBaseInfo.subjectInExamData> mValues = ExamDataBaseInfo.getSubjectDataByExamId(_id);
        ArrayList<ExamDataBaseInfo.subjectData> subjectList = ExamDataBaseInfo.getSubjectList();

        if (mValues == null || subjectList == null)
            return;

        for (int i = 0; i < mValues.size(); i++) {
            addedSubject.add(mValues.get(i)._subjectId);
        }

        for (int i = 0; i < subjectList.size(); i++) {
            ExamDataBaseInfo.subjectData mData = subjectList.get(i);

            int _subjectId = mData._subjectId;
            String name = mData.name;
            int color = mData.color;

            if (!addedSubject.contains(_subjectId)) {
                ExamDataBaseInfo.subjectData addInfo = new ExamDataBaseInfo.subjectData();
                addInfo.name = name;
                addInfo._subjectId = _subjectId;
                addInfo.color = color;

                subjectExamData.add(addInfo);
            }
        }

        Collections.sort(subjectExamData, ALPHA_COMPARATOR);
    }

    /**
     * 알파벳순으로 정렬
     */
    public final Comparator<ExamDataBaseInfo.subjectData> ALPHA_COMPARATOR = new Comparator<ExamDataBaseInfo.subjectData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ExamDataBaseInfo.subjectData arg1, ExamDataBaseInfo.subjectData arg2) {
            return sCollator.compare(arg1.name, arg2.name);
        }
    };

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
            String averageText = mAverage.getText().toString();
            String standardDeviationText = mStandardDeviation.getText().toString();

            /**
             * private EditText mScore, mClass, mRank, mApplicants;
             * private TextInputLayout mScoreTextInputLayout, mClassTextInputLayout, mRankTextInputLayout, mApplicantsTextInputLayout;
             */
            if (scoreText.isEmpty() || scoreText.length() == 0) {
                mScoreTextInputLayout.setError("받은 점수는 필수로 입력해야 합니다.");
                return true;
            }

            float score = Float.parseFloat(scoreText);
            float average = (averageText.isEmpty() || averageText.length() == 0) ? 0 : Float.parseFloat(averageText);
            float standardDeviation = (standardDeviationText.isEmpty() || standardDeviationText.length() == 0) ? 0 : Float.parseFloat(standardDeviationText);
            int mClass = (classText.isEmpty() || classText.length() == 0) ? 0 : Integer.parseInt(classText);
            int rank = (rankText.isEmpty() || rankText.length() == 0) ? 0 : Integer.parseInt(rankText);
            int applicants = (applicantsText.isEmpty() || applicantsText.length() == 0) ? 0 : Integer.parseInt(applicantsText);

            if (rank > applicants) {
                mApplicantsTextInputLayout.setError("전체 응시자 수는 과목 석차수보다 커야 합니다.");
                return true;
            }

            if (mDatabase == null) {
                mDatabase = new Database();
            }
            mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);

            if (type == 0) {
                mDatabase.addData("name", subjectExamData.get(mSubjectSpinner.getSelectedItemPosition())._subjectId);
                mDatabase.addData("score", score);
                mDatabase.addData("rank", rank);
                mDatabase.addData("applicants", applicants);
                mDatabase.addData("class", mClass);
                mDatabase.addData("average", average);
                mDatabase.addData("standardDeviation", standardDeviation);
                mDatabase.commit(ExamDataBaseInfo.getExamTable(_id));

            } else if (type == 1) {
                mDatabase.update(ExamDataBaseInfo.getExamTable(_id), "score", score, "name", subjectId);
                mDatabase.update(ExamDataBaseInfo.getExamTable(_id), "rank", rank, "name", subjectId);
                mDatabase.update(ExamDataBaseInfo.getExamTable(_id), "applicants", applicants, "name", subjectId);
                mDatabase.update(ExamDataBaseInfo.getExamTable(_id), "class", mClass, "name", subjectId);
                mDatabase.update(ExamDataBaseInfo.getExamTable(_id), "average", average, "name", subjectId);
                mDatabase.update(ExamDataBaseInfo.getExamTable(_id), "standardDeviation", standardDeviation, "name", subjectId);
            }

            mDatabase.release();
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
