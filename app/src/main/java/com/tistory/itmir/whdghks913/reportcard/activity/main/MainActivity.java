package com.tistory.itmir.whdghks913.reportcard.activity.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.activity.create.exam.CreateExamActivity;
import com.tistory.itmir.whdghks913.reportcard.tool.Database;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Database mDatabase;
    SimpleRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.mFab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateExamActivity.class));
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        mAdapter = new SimpleRecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);

        getExamList();
    }

    private void getExamList() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        if (mDatabase == null)
            mDatabase = new Database();
        mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);
        Cursor mCursor = mDatabase.getData(ExamDataBaseInfo.examListTableName);

        if (mCursor == null)
            return;

        for (int i = 0; i < mCursor.getCount(); i++) {
            mCursor.moveToNext();

            int _id = mCursor.getInt(0);

            String name = mCursor.getString(1);

            int year = mCursor.getInt(2);
            int month = mCursor.getInt(3);
            int day = mCursor.getInt(4);

            int red = mCursor.getInt(5);
            int green = mCursor.getInt(6);
            int blue = mCursor.getInt(7);

            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd E요일", Locale.KOREA);
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(year, month, day);

            mAdapter.addItem(_id, Color.rgb(red, green, blue), name, mFormat.format(mCalendar.getTime()));
        }
    }

    public class SimpleRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.ViewHolder> {

        private final int mBackground;
        private ArrayList<ExamData> mValues = new ArrayList<>();

        public SimpleRecyclerViewAdapter(Context mContext) {
            TypedValue mTypedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final View mColor;
            public final TextView mExamName, mExamDate;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mColor = view.findViewById(R.id.mColor);
                mExamName = (TextView) view.findViewById(R.id.mExamName);
                mExamDate = (TextView) view.findViewById(R.id.mExamDate);
            }
        }

        public void addItem(int _id, int color, String name, String date) {
            ExamData mData = new ExamData();

            mData._id = _id;
            mData.color = color;
            mData.name = name;
            mData.date = date;

            mValues.add(mData);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public ExamData getItemData(int position) {
            return mValues.get(position);
        }

        public void clear() {
            mValues.clear();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_exam_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ExamData mData = mValues.get(position);

            holder.mColor.setBackgroundColor(mData.color);
            holder.mExamName.setText(mData.name);
            holder.mExamDate.setText(mData.date);

            holder.mView.setTag(mData._id);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO
                }
            });
            holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mDatabase != null) {
                        mDatabase.remove(ExamDataBaseInfo.examListTableName, "_id", (int) view.getTag());
                        getExamList();
                    }

                    return true;
                }
            });
        }
    }

    public class ExamData {
        public int _id;
        public int color;
        public String name, date;
    }

    @Override
    public void onResume() {
        super.onResume();

        getExamList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
