package com.tistory.itmir.whdghks913.reportcard.activity.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
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
import com.tistory.itmir.whdghks913.reportcard.activity.create.CreateExamActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.show.exam.ShowExamDetailActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.show.subject.ShowSubjectActivity;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;
import com.tistory.itmir.whdghks913.reportcard.tool.initDatabase;

import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    SimpleRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        setSupportActionBar(mToolbar);

        FloatingActionButton mExamFab = (FloatingActionButton) findViewById(R.id.mFab);
        mExamFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateExamActivity.class));
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        mAdapter = new SimpleRecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);

        if (!ExamDataBaseInfo.isDatabaseExists()) {
            (new initDatabase()).init();
        }

        getExamList();
    }

    private void getExamList() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        ArrayList<ExamDataBaseInfo.examData> mValues = ExamDataBaseInfo.getExamList();
        if (mValues == null)
            return;

        for (int i = 0; i < mValues.size(); i++) {
            ExamDataBaseInfo.examData mData = mValues.get(i);

            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd E요일", Locale.KOREA);
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(mData.year, mData.month, mData.day);

            mAdapter.addItem(mData._id, mData.category, mData.color, mData.name, mFormat.format(mCalendar.getTime()), ExamDataBaseInfo.getCategoryNameById(mData.category));
        }


        mAdapter.sort();
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
            public final TextView mExamName, mExamDate, mExamCategory;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mColor = view.findViewById(R.id.mColor);
                mExamName = (TextView) view.findViewById(R.id.mExamName);
                mExamDate = (TextView) view.findViewById(R.id.mExamDate);
                mExamCategory = (TextView) view.findViewById(R.id.mExamCategory);
            }
        }

        public void addItem(int _id, int category, int color, String name, String date, String categoryName) {
            ExamData mData = new ExamData();

            mData._id = _id;
            mData.category = category;
            mData.color = color;
            mData.name = name;
            mData.date = date;
            mData.categoryName = categoryName;

            mValues.add(mData);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

//        public ExamData getItemData(int position) {
//            return mValues.get(position);
//        }

        public void sort() {
            Collections.sort(mValues, ALPHA_COMPARATOR);
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

            GradientDrawable bgShape = (GradientDrawable) holder.mColor.getBackground();
            bgShape.setColor(mData.color);

            holder.mExamName.setText(mData.name);
            holder.mExamDate.setText(mData.date);
            holder.mExamCategory.setText(mData.categoryName);

            holder.mView.setTag(mData);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExamData mTag = (ExamData) v.getTag();

                    Intent mIntent = new Intent(v.getContext(), ShowExamDetailActivity.class);
                    mIntent.putExtra("_id", mTag._id);
                    mIntent.putExtra("name", mTag.name);
//                    mIntent.putExtra("category", mTag.category);
//                    mIntent.putExtra("color", mTag.color);

                    startActivity(mIntent);
                }
            });
        }
    }

    public class ExamData {
        public int _id;
        public int color, category;
        public String name, categoryName, date;
    }

    /**
     * 알파벳순으로 정렬
     */
    public final Comparator<ExamData> ALPHA_COMPARATOR = new Comparator<ExamData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(ExamData arg1, ExamData arg2) {
            return sCollator.compare(arg1.date, arg2.date);
        }
    };

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
        } else if (id == R.id.action_subject) {
            startActivity(new Intent(getApplicationContext(), ShowSubjectActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
