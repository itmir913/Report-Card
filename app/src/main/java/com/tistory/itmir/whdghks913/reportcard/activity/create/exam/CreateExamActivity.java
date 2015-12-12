package com.tistory.itmir.whdghks913.reportcard.activity.create.exam;

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
import android.widget.Button;
import android.widget.EditText;

import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.tool.Database;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class CreateExamActivity extends AppCompatActivity {
    private int red, green, blue;
    private Calendar mCalendar;
    private TextInputLayout mTextInputLayout;
    private EditText mEditText;

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

        mCalendar = Calendar.getInstance();
        mTextInputLayout = (TextInputLayout) findViewById(R.id.mTextInputLayout);
        mTextInputLayout.setErrorEnabled(true);
        mEditText = (EditText) findViewById(R.id.mEditText);

        Random randomGenerator = new Random();
        red = randomGenerator.nextInt(256);
        green = randomGenerator.nextInt(256);
        blue = randomGenerator.nextInt(256);

        View mCircleView = findViewById(R.id.mCircleView);
        GradientDrawable bgShape = (GradientDrawable) mCircleView.getBackground();
        bgShape.setColor(Color.rgb(red, green, blue));
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

            if (examName.isEmpty() || examName.length() == 0) {
                mTextInputLayout.setError("시험 이름은 필수로 입력해야 합니다.");
                return true;
            }

            Database mData = new Database();
            mData.openOrCreateDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName,
                    ExamDataBaseInfo.examListTableName, ExamDataBaseInfo.examListTableColumn);

            Cursor mCursor = mData.getData(ExamDataBaseInfo.examListTableName, "name");
            for (int i = 0; i < mCursor.getCount(); i++) {
                mCursor.moveToNext();

                if (examName.equals(mCursor.getString(0))) {
                    mTextInputLayout.setError("이미 존재하는 시험 이름입니다.");
                    return true;
                }
            }

            mData.addData("name", examName);

            mData.addData("year", mCalendar.get(Calendar.YEAR));
            mData.addData("month", mCalendar.get(Calendar.MONTH));
            mData.addData("day", mCalendar.get(Calendar.DAY_OF_MONTH));

            mData.addData("red", red);
            mData.addData("green", green);
            mData.addData("blue", blue);

            mData.commit(ExamDataBaseInfo.examListTableName);

            mData.createTable(examName, ExamDataBaseInfo.examDetailedColumn);

            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
