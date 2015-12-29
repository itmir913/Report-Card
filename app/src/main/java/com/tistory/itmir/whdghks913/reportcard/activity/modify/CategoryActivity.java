package com.tistory.itmir.whdghks913.reportcard.activity.modify;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.tool.Database;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.util.Random;

public class CategoryActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {
    /**
     * type
     */
    private int type; // 0 : 생성, 1 : 편집
    private boolean isExam; // true : 시험, false : 과목

    /**
     * Create type
     */
    private int color;
    private TextInputLayout mTextInputLayout;
    private EditText mEditText;
    private GradientDrawable categoryColorGradient;

    /**
     * Edit type
     */
    private int _id;
    private String name;
    private boolean isDelete = true;

    private Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        Intent mIntent = getIntent();
        type = mIntent.getIntExtra("type", 0);
        isExam = mIntent.getBooleanExtra("isExam", true);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        if (type == 0 && !isExam)
            mToolbar.setTitle(getString(R.string.title_activity_create_subject_category));
        if (type == 1 && isExam)
            mToolbar.setTitle(getString(R.string.title_activity_edit_exam_category));
        else if (type == 1)
            mToolbar.setTitle(getString(R.string.title_activity_edit_subject_category));
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

        View mCategoryCircleView = findViewById(R.id.mCategoryCircleView);
        mTextInputLayout = (TextInputLayout) findViewById(R.id.mTextInputLayout);
        mTextInputLayout.setErrorEnabled(true);
        mEditText = (EditText) findViewById(R.id.mEditText);
        categoryColorGradient = (GradientDrawable) mCategoryCircleView.getBackground();

        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    addData();
                    return true;
                }
                return false;
            }
        });

        mDatabase = new Database();
        mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);

        if (type == 0) {
            Random randomGenerator = new Random();
            int red = randomGenerator.nextInt(256);
            int green = randomGenerator.nextInt(256);
            int blue = randomGenerator.nextInt(256);
            color = Color.rgb(red, green, blue);
        } else if (type == 1) {
            _id = mIntent.getIntExtra("_id", 0);
            color = mIntent.getIntExtra("color", 0);
            name = mIntent.getStringExtra("name");

            mEditText.setText(name);
            findViewById(R.id.removeButton).setVisibility(View.VISIBLE);
            canDeleteCategory();
        }

        categoryColorGradient.setColor(color);
    }

    public void categoryColorView(View v) {
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
        categoryColorGradient.setColor(color);
    }

    private void canDeleteCategory() {
        Cursor mDetailList;
        if (isExam) {
            mDetailList = mDatabase.getFirstData(ExamDataBaseInfo.examListTableName, "category");
        } else {
            mDetailList = mDatabase.getFirstData(ExamDataBaseInfo.subjectTableName, "category");
        }

        for (int i = 0; i < mDetailList.getCount(); i++) {
            if (_id == mDetailList.getInt(0)) {
                isDelete = false;
                break;
            }

            mDetailList.moveToNext();
        }
    }

    public void remove(View v) {
        if (mDatabase != null) {
            if (_id == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatErrorAlertDialogStyle);
                builder.setTitle(R.string.not_delete_category_title);
                builder.setMessage(R.string.not_delete_basic_category_message);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setCancelable(false);
                builder.show();

                return;
            }

            if (isDelete) {
                if (isExam) mDatabase.remove(ExamDataBaseInfo.categoryExamTableName, "_id", _id);
                else mDatabase.remove(ExamDataBaseInfo.categorySubjectTableName, "_id", _id);

                finish();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatErrorAlertDialogStyle);
                builder.setTitle(R.string.not_delete_category_title);
                builder.setMessage(R.string.not_delete_category_message);
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
            addData();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addData() {
        String categoryName = mEditText.getText().toString();

        if (categoryName.isEmpty() || categoryName.length() == 0 || (categoryName.replaceAll("\\s", "")).length() == 0) {
            mTextInputLayout.setError("카테고리 이름은 필수로 입력해야 합니다.");
            return;
        }

        if (mDatabase == null) {
            mDatabase = new Database();
        }
        mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);

        if (isExam) {
            if (type == 0 || ((type == 1) && (!name.equals(categoryName)))) {
                Cursor mCursor = mDatabase.getData(ExamDataBaseInfo.categoryExamTableName, "name");
                for (int i = 0; i < mCursor.getCount(); i++) {
                    mCursor.moveToNext();

                    if (categoryName.equals(mCursor.getString(0))) {
                        mTextInputLayout.setError("이미 존재하는 카테고리 이름입니다.");
                        return;
                    }
                }
            }

            if (type == 0) {
                mDatabase.addData("name", categoryName);
                mDatabase.addData("color", color);
                mDatabase.commit(ExamDataBaseInfo.categoryExamTableName);
            } else if (type == 1) {
                mDatabase.update(ExamDataBaseInfo.categoryExamTableName, "name", categoryName, "_id", _id);
                mDatabase.update(ExamDataBaseInfo.categoryExamTableName, "color", color, "_id", _id);
            }

        } else {
            if (type == 0 || ((type == 1) && (!name.equals(categoryName)))) {
                Cursor mCursor = mDatabase.getData(ExamDataBaseInfo.categorySubjectTableName, "name");
                for (int i = 0; i < mCursor.getCount(); i++) {
                    mCursor.moveToNext();

                    if (categoryName.equals(mCursor.getString(0))) {
                        mTextInputLayout.setError("이미 존재하는 카테고리 이름입니다.");
                        return;
                    }
                }
            }

            if (type == 0) {
                mDatabase.addData("name", categoryName);
                mDatabase.addData("color", color);
                mDatabase.commit(ExamDataBaseInfo.categorySubjectTableName);
            } else if (type == 1) {
                mDatabase.update(ExamDataBaseInfo.categorySubjectTableName, "name", categoryName, "_id", _id);
                mDatabase.update(ExamDataBaseInfo.categorySubjectTableName, "color", color, "_id", _id);
            }
        }

        mDatabase.release();
        finish();
    }


}
