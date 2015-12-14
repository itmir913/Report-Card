package com.tistory.itmir.whdghks913.reportcard.activity.edit;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.tool.Database;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EditExamActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {
    private int _id;

    private Database mDatabase;
    private int color;
    private Calendar mCalendar;
    private TextInputLayout mTextInputLayout;
    private EditText mEditText;

    private GradientDrawable examColorGradient, examCategoryGradient;
    private ArrayList<Integer> categoryId = new ArrayList<>();
    private ArrayList<String> categoryName = new ArrayList<>();
    private ArrayList<Integer> categoryColor = new ArrayList<>();
    private int categoryIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exam);
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
        if (_id == 0)
            return;
        String title = mIntent.getStringExtra("name");

        findViewById(R.id.removeButton).setVisibility(View.VISIBLE);

        mCalendar = Calendar.getInstance();
        mTextInputLayout = (TextInputLayout) findViewById(R.id.mTextInputLayout);
        mTextInputLayout.setErrorEnabled(true);
        mEditText = (EditText) findViewById(R.id.mEditText);
        mEditText.setText(title);

        mDatabase = new Database();
        mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);

        Cursor mCategoryCursor = mDatabase.getFirstData(ExamDataBaseInfo.categoryExamTableName);

        for (int i = 0; i < mCategoryCursor.getCount(); i++) {
            categoryId.add(mCategoryCursor.getInt(0));
            categoryName.add(mCategoryCursor.getString(1));
            categoryColor.add(mCategoryCursor.getInt(2));
            mCategoryCursor.moveToNext();
        }

        Cursor mExamData = mDatabase.getData(ExamDataBaseInfo.examListTableName, "*", "_id", _id);
        mExamData.moveToNext();

        int category = mExamData.getInt(2);

        int year = mExamData.getInt(3);
        int month = mExamData.getInt(4);
        int day = mExamData.getInt(5);

        color = mExamData.getInt(6);

        mCalendar.set(year, month, day);
        SimpleDateFormat mFormat = new SimpleDateFormat("시험 날짜 : yyyy.MM.dd E요일", Locale.KOREA);
        ((Button) findViewById(R.id.mDatePickerButton)).setText(mFormat.format(mCalendar.getTime()));

        View mExamColorCircleView = findViewById(R.id.mExamColorCircleView);
        View mCategoryColorCircleView = findViewById(R.id.mCategoryColorCircleView);

        examCategoryGradient = (GradientDrawable) mCategoryColorCircleView.getBackground();
        examColorGradient = (GradientDrawable) mExamColorCircleView.getBackground();
        examColorGradient.setColor(color);

        if (categoryId.contains(category))
            categoryColorView(categoryName.get(categoryId.indexOf(category)).substring(0, 1), categoryColor.get(categoryId.indexOf(category)));
        else
            categoryColorView(categoryName.get(0).substring(0, 1), categoryColor.get(0));
    }


    public void examColorView(View v) {
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
        examColorGradient.setColor(color);
    }

    public void categoryColorView(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.edit_category);
        builder.setItems(categoryName.toArray(new String[categoryName.size()]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categoryIndex = which;
                categoryColorView(categoryName.get(which).substring(0, 1), categoryColor.get(which));
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void categoryColorView(String name, int color) {
        ((TextView) findViewById(R.id.mCategoryName)).setText(name);
        examCategoryGradient.setColor(color);
    }

    public void examDate(final View v) {
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        mCalendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat mFormat = new SimpleDateFormat("시험 날짜 : yyyy.MM.dd E요일", Locale.KOREA);
                        ((Button) v).setText(mFormat.format(mCalendar.getTime()));
                    }
                },
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH)
        );
        dpd.vibrate(false);
        dpd.showYearPickerFirst(false);
        dpd.show(getFragmentManager(), null);
    }

    public void remove(View v) {
        if (mDatabase != null) {
            mDatabase.remove(ExamDataBaseInfo.examListTableName, "_id", _id);
            mDatabase.removeTable(ExamDataBaseInfo.getExamTable(_id));

            setResult(999);
            finish();
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
            String examName = mEditText.getText().toString();

            if (examName.isEmpty() || examName.length() == 0 || (examName.replaceAll("\\s", "")).length() == 0) {
                mTextInputLayout.setError("시험 이름은 필수로 입력해야 합니다.");
                return true;
            }

            if (mDatabase == null)
                return true;

            mDatabase.update(ExamDataBaseInfo.examListTableName, "name", examName);
            mDatabase.update(ExamDataBaseInfo.examListTableName, "category", categoryId.get(categoryIndex));
            mDatabase.update(ExamDataBaseInfo.examListTableName, "year", mCalendar.get(Calendar.YEAR));
            mDatabase.update(ExamDataBaseInfo.examListTableName, "month", mCalendar.get(Calendar.MONTH));
            mDatabase.update(ExamDataBaseInfo.examListTableName, "day", mCalendar.get(Calendar.DAY_OF_MONTH));
            mDatabase.update(ExamDataBaseInfo.examListTableName, "color", color);

            mDatabase.release();

            setResult(1234);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
