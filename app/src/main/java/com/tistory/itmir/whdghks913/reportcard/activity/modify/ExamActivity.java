package com.tistory.itmir.whdghks913.reportcard.activity.modify;

import android.content.DialogInterface;
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
import java.util.Random;

public class ExamActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {
    /**
     * type
     */
    public int type;

    /**
     * Create Type
     */
    private int color;
    private Calendar mCalendar;
    private TextInputLayout mTextInputLayout;
    private EditText mEditText;

    private GradientDrawable examColorGradient, examCategoryGradient;
    private ArrayList<Integer> categoryId = new ArrayList<>();
    private ArrayList<String> categoryName = new ArrayList<>();
    private ArrayList<Integer> categoryColor = new ArrayList<>();
    private int categoryIndex = 0;

    /**
     * Edit Type
     */
    private int _id;
    private String title;

    private Database mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exam);

        Intent mIntent = getIntent();
        type = mIntent.getIntExtra("type", 0);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        if (type == 1) mToolbar.setTitle(getString(R.string.title_activity_edit_exam));
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

        mCalendar = Calendar.getInstance();
        mTextInputLayout = (TextInputLayout) findViewById(R.id.mTextInputLayout);
        mTextInputLayout.setErrorEnabled(true);
        mEditText = (EditText) findViewById(R.id.mEditText);
        View mExamColorCircleView = findViewById(R.id.mExamColorCircleView);
        View mCategoryColorCircleView = findViewById(R.id.mCategoryColorCircleView);
        examColorGradient = (GradientDrawable) mExamColorCircleView.getBackground();
        examCategoryGradient = (GradientDrawable) mCategoryColorCircleView.getBackground();

        mDatabase = new Database();
        mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);

        ArrayList<ExamDataBaseInfo.categoryData> mCategoryList = ExamDataBaseInfo.getCategoryList();

        for (int i = 0; i < mCategoryList.size(); i++) {
            ExamDataBaseInfo.categoryData mData = mCategoryList.get(i);

            categoryId.add(mData._categoryId);
            categoryName.add(mData.name);
            categoryColor.add(mData.color);
        }

        if (type == 0) {
            Random randomGenerator = new Random();

            int red = randomGenerator.nextInt(256);
            int green = randomGenerator.nextInt(256);
            int blue = randomGenerator.nextInt(256);
            color = Color.rgb(red, green, blue);

            examColorGradient.setColor(color);

            categoryColorView(categoryName.get(0).substring(0, 1), categoryColor.get(0));
        } else if (type == 1) {
            _id = mIntent.getIntExtra("_id", 0);

            if (_id == 0)
                return;

            title = mIntent.getStringExtra("name");
            mEditText.setText(title);

            findViewById(R.id.removeButton).setVisibility(View.VISIBLE);

            Cursor mExamData = mDatabase.getData(ExamDataBaseInfo.examListTableName, "*", "_id", _id);
            mExamData.moveToNext();

            int category = mExamData.getInt(2);
            int year = mExamData.getInt(3);
            int month = mExamData.getInt(4);
            int day = mExamData.getInt(5);
            categoryIndex = categoryId.indexOf(category);

            color = mExamData.getInt(6);
            examColorGradient.setColor(color);

            mCalendar.set(year, month, day);
            SimpleDateFormat mFormat = new SimpleDateFormat("시험 날짜 : yyyy.MM.dd E요일", Locale.KOREA);
            ((Button) findViewById(R.id.mDatePickerButton)).setText(mFormat.format(mCalendar.getTime()));

            if (categoryId.contains(category))
                categoryColorView(categoryName.get(categoryId.indexOf(category)).substring(0, 1), categoryColor.get(categoryId.indexOf(category)));
            else
                categoryColorView(categoryName.get(0).substring(0, 1), categoryColor.get(0));
        }
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

            if (mDatabase == null) {
                mDatabase = new Database();
            }
            mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);

            Cursor mCursor = mDatabase.getData(ExamDataBaseInfo.examListTableName, "name");

            if (type == 0 || ((type == 1) && (!title.equals(examName)))) {
                for (int i = 0; i < mCursor.getCount(); i++) {
                    mCursor.moveToNext();

                    if (examName.equals(mCursor.getString(0))) {
                        mTextInputLayout.setError("이미 존재하는 시험 이름입니다.");
                        return true;
                    }
                }
            }

            if (type == 0) {
                mDatabase.addData("name", examName);
                mDatabase.addData("category", categoryId.get(categoryIndex));
                mDatabase.addData("year", mCalendar.get(Calendar.YEAR));
                mDatabase.addData("month", mCalendar.get(Calendar.MONTH));
                mDatabase.addData("day", mCalendar.get(Calendar.DAY_OF_MONTH));
                mDatabase.addData("color", color);

                mDatabase.commit(ExamDataBaseInfo.examListTableName);

                mCursor = mDatabase.getLastData(ExamDataBaseInfo.examListTableName, "_id");

                mDatabase.createTable(ExamDataBaseInfo.getExamTable(mCursor.getInt(0)), ExamDataBaseInfo.examDetailedColumn);

                finish();

            } else if (type == 1) {
                mDatabase.update(ExamDataBaseInfo.examListTableName, "name", examName, "_id", _id);
                mDatabase.update(ExamDataBaseInfo.examListTableName, "category", categoryId.get(categoryIndex), "_id", _id);
                mDatabase.update(ExamDataBaseInfo.examListTableName, "year", mCalendar.get(Calendar.YEAR), "_id", _id);
                mDatabase.update(ExamDataBaseInfo.examListTableName, "month", mCalendar.get(Calendar.MONTH), "_id", _id);
                mDatabase.update(ExamDataBaseInfo.examListTableName, "day", mCalendar.get(Calendar.DAY_OF_MONTH), "_id", _id);
                mDatabase.update(ExamDataBaseInfo.examListTableName, "color", color, "_id", _id);

                setResult(1234);
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
