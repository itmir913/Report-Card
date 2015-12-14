package com.tistory.itmir.whdghks913.reportcard.activity.edit;

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
import android.widget.EditText;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.tool.Database;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

public class EditSubjectActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {
    private int _id;
    private String name;
    private boolean isDelete = true;

    private int color;
    private TextInputLayout mTextInputLayout;
    private EditText mEditText;
    private GradientDrawable subjectColorGradient;

    private Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_subject);
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

        Intent mIntent = getIntent();
        _id = mIntent.getIntExtra("_id", 0);
        color = mIntent.getIntExtra("color", 0);
        name = mIntent.getStringExtra("name");

        View mSubjectCircleView = findViewById(R.id.mSubjectCircleView);
        mTextInputLayout = (TextInputLayout) findViewById(R.id.mTextInputLayout);
        mTextInputLayout.setErrorEnabled(true);
        mEditText = (EditText) findViewById(R.id.mEditText);
        mEditText.setText(name);

        subjectColorGradient = (GradientDrawable) mSubjectCircleView.getBackground();
        subjectColorGradient.setColor(color);

        mDatabase = new Database();
        mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);

        canDeleteSubject();

        findViewById(R.id.removeButton).setVisibility(View.VISIBLE);
    }

    public void subjectColorView(View v) {
        // Pass AppCompatActivity which implements ColorCallback, along with the title of the dialog
        new ColorChooserDialog.Builder(this, R.string.edit_exam_color)
                .accentMode(true)  // when true, will display accent palette instead of primary palette
                .doneButton(android.R.string.ok)  // changes label of the done button
                .cancelButton(android.R.string.cancel)  // changes label of the cancel button
                .backButton(R.string.back)  // changes label of the back button
                .customButton(R.string.custom)
                .presetsButton(R.string.basic)
                .dynamicButtonColor(true)  // defaults to true, false will disable changing action buttons' color to currently selected color
                .show();
    }

    @Override
    public void onColorSelection(ColorChooserDialog dialog, int color) {
        this.color = color;
        subjectColorGradient.setColor(color);
    }

    private void canDeleteSubject() {
        Cursor mExamNameList = mDatabase.getFirstData(ExamDataBaseInfo.examListTableName, "_id");
        label:
        for (int i = 0; i < mExamNameList.getCount(); i++) {
            Cursor mExamDetailList = mDatabase.getFirstData(ExamDataBaseInfo.getExamTable(mExamNameList.getInt(0)), "name");
            for (int j = 0; j < mExamDetailList.getCount(); j++) {
                if (_id == mExamDetailList.getInt(0)) {
                    isDelete = false;
                    break label;
                }
                mExamDetailList.moveToNext();
            }
            mExamNameList.moveToNext();
        }

//        v.setEnabled(isDelete);
    }

    public void remove(View v) {
        if (mDatabase != null) {
            if (isDelete) {
                mDatabase.remove(ExamDataBaseInfo.subjectTableName, "_id", _id);
                finish();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatErrorAlertDialogStyle);
                builder.setTitle(R.string.not_delete_subject_title);
                builder.setMessage(R.string.not_delete_subject_message);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setCancelable(false);
                builder.show();
            }
        }
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
            String subjectName = mEditText.getText().toString();

            if (subjectName.isEmpty() || subjectName.length() == 0 || (subjectName.replaceAll("\\s", "")).length() == 0) {
                mTextInputLayout.setError("시험 이름은 필수로 입력해야 합니다.");
                return true;
            }

            if (mDatabase == null) {
                mDatabase = new Database();
                mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);
            }

            if (!name.equals(subjectName)) {
                Cursor mCursor = mDatabase.getData(ExamDataBaseInfo.subjectTableName, "name");
                for (int i = 0; i < mCursor.getCount(); i++) {
                    mCursor.moveToNext();

                    if (subjectName.equals(mCursor.getString(0))) {
                        mTextInputLayout.setError("이미 존재하는 시험 이름입니다.");
                        return true;
                    }
                }
            }

            mDatabase.update(ExamDataBaseInfo.subjectTableName, "name", subjectName, "_id", _id);
            mDatabase.update(ExamDataBaseInfo.subjectTableName, "color", color, "_id", _id);

            mDatabase.release();

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
