package com.tistory.itmir.whdghks913.reportcard.activity.show.category;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.activity.modify.CategoryActivity;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.util.ArrayList;

public class ShowCategoryActivity extends AppCompatActivity {
    /**
     * type
     */
    private int type;

    private SimpleRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_simple_list);

        Intent mIntent = getIntent();
        type = mIntent.getIntExtra("type", 0);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        if (type == 1) mToolbar.setTitle(getString(R.string.title_activity_subject_category));
        setSupportActionBar(mToolbar);

        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.mFab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(getApplicationContext(), CategoryActivity.class);
                mIntent.putExtra("type", 0);
                mIntent.putExtra("isExam", (type == 0));
                startActivity(mIntent);
            }
        });

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

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        mAdapter = new SimpleRecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);

        if (type == 0)
            getExamCategoryList();
        else
            getSubjectCategoryList();
    }

    private void getExamCategoryList() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        ArrayList<ExamDataBaseInfo.categoryData> mValues = ExamDataBaseInfo.getCategoryList();
        if (mValues == null)
            return;

        for (int i = 0; i < mValues.size(); i++) {
            ExamDataBaseInfo.categoryData mData = mValues.get(i);
            mAdapter.addItem(mData._categoryId, mData.name, mData.color, 0);
        }
    }

    private void getSubjectCategoryList() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        ArrayList<ExamDataBaseInfo.categoryData> mValues = ExamDataBaseInfo.getSubjectCategoryList();
        if (mValues == null)
            return;

        for (int i = 0; i < mValues.size(); i++) {
            ExamDataBaseInfo.categoryData mData = mValues.get(i);
            mAdapter.addItem(mData._categoryId, mData.name, mData.color, 1);
        }
    }

    public class SimpleRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.ViewHolder> {

        private final int mBackground;
        private ArrayList<ExamDataBaseInfo.categoryData> mValues = new ArrayList<>();

        public SimpleRecyclerViewAdapter(Context mContext) {
            TypedValue mTypedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final View mColor;
            public final TextView mCategoryName;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mColor = view.findViewById(R.id.mColor);
                mCategoryName = (TextView) view.findViewById(R.id.mName);
            }
        }

        public void addItem(int _categoryId, String name, int color, int type) {
            ExamDataBaseInfo.categoryData mData = new ExamDataBaseInfo.categoryData();

            mData._categoryId = _categoryId;
            mData.color = color;
            mData.name = name;
            mData.type = type;

            mValues.add(mData);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

//        public ExamData getItemData(int position) {
//            return mValues.get(position);
//        }

        public void clear() {
            mValues.clear();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_simple_list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ExamDataBaseInfo.categoryData mData = mValues.get(position);

            GradientDrawable bgShape = (GradientDrawable) holder.mColor.getBackground();
            bgShape.setColor(mData.color);

            holder.mCategoryName.setText(mData.name);
            holder.mView.setTag(mData);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExamDataBaseInfo.categoryData mData = (ExamDataBaseInfo.categoryData) v.getTag();
                    int _categoryId = mData._categoryId;
                    int color = mData.color;
                    String name = mData.name;
                    int type = mData.type;

                    Intent mIntent = new Intent(v.getContext(), CategoryActivity.class);
                    mIntent.putExtra("type", 1);

                    mIntent.putExtra("_id", _categoryId);
                    mIntent.putExtra("color", color);
                    mIntent.putExtra("name", name);

                    mIntent.putExtra("isExam", (type == 0));

                    v.getContext().startActivity(mIntent);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (type == 0)
            getExamCategoryList();
        else
            getSubjectCategoryList();
    }


}
