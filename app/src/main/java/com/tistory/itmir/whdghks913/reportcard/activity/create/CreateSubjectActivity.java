package com.tistory.itmir.whdghks913.reportcard.activity.create;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
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

import java.util.Random;

public class CreateSubjectActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {
    private int color;
    private TextInputLayout mTextInputLayout;
    private EditText mEditText;
    private GradientDrawable subjectColorGradient;

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

        Random randomGenerator = new Random();
        int red = randomGenerator.nextInt(256);
        int green = randomGenerator.nextInt(256);
        int blue = randomGenerator.nextInt(256);
        color = Color.rgb(red, green, blue);

        View mSubjectCircleView = findViewById(R.id.mSubjectCircleView);
        mTextInputLayout = (TextInputLayout) findViewById(R.id.mTextInputLayout);
        mTextInputLayout.setErrorEnabled(true);
        mEditText = (EditText) findViewById(R.id.mEditText);

        subjectColorGradient = (GradientDrawable) mSubjectCircleView.getBackground();
        subjectColorGradient.setColor(color);
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
            String examName = mEditText.getText().toString();

            if (examName.isEmpty() || examName.length() == 0 || (examName.replaceAll("\\s", "")).length() == 0) {
                mTextInputLayout.setError("시험 이름은 필수로 입력해야 합니다.");
                return true;
            }

            Database mDatabase = new Database();
            mDatabase.openOrCreateDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName,
                    ExamDataBaseInfo.subjectTableName, ExamDataBaseInfo.subjectColumn);

            Cursor mCursor = mDatabase.getData(ExamDataBaseInfo.subjectTableName, "name");
            for (int i = 0; i < mCursor.getCount(); i++) {
                mCursor.moveToNext();

                if (examName.equals(mCursor.getString(0))) {
                    mTextInputLayout.setError("이미 존재하는 시험 이름입니다.");
                    return true;
                }
            }

            mDatabase.addData("name", examName);
            mDatabase.addData("color", color);
            mDatabase.commit(ExamDataBaseInfo.subjectTableName);
            mDatabase.release();

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
