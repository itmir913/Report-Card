package com.tistory.itmir.whdghks913.reportcard.activity.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.activity.modify.CategoryActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.modify.ExamActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.show.exam.ShowExamDetailActivity;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.util.ArrayList;

public class ExamListFragment extends Fragment {
    private SimpleRecyclerViewAdapter mAdapter;
    private TextView mEmpty;

    public static ExamListFragment newInstance() {
        return new ExamListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.recyclerview, container, false);

        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new SimpleRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);

        mEmpty = (TextView) mView.findViewById(R.id.mEmpty);

        getExamList();

        return mView;
    }

    private void getExamList() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        ArrayList<ExamDataBaseInfo.examData> mExamValues = ExamDataBaseInfo.getExamList(false);
        ArrayList<ExamDataBaseInfo.categoryData> mCategoryValues = ExamDataBaseInfo.getCategoryList();
        if (mExamValues == null || mExamValues.size() == 0) {
            mEmpty.setVisibility(View.VISIBLE);
            return;
        } else {
            if (mEmpty.getVisibility() == View.VISIBLE)
                mEmpty.setVisibility(View.GONE);
        }

        ArrayList<Integer> mCategoryIdInExam = new ArrayList<>();
        for (int i = 0; i < mExamValues.size(); i++) {
            mCategoryIdInExam.add(mExamValues.get(i).category);
        }

        for (int i = 0; i < mCategoryValues.size(); i++) {
            ExamDataBaseInfo.categoryData mCategoryData = mCategoryValues.get(i);
            int _categoryId = mCategoryData._categoryId;
            if (mCategoryIdInExam.contains(_categoryId)) {
                mAdapter.addItem(_categoryId, mCategoryData.name, mCategoryData.color);

                for (int jj = 0; jj < mExamValues.size(); jj++) {
                    ExamDataBaseInfo.examData mExamData = mExamValues.get(jj);

                    int examCategoryId = mExamData.category;

                    if (examCategoryId == _categoryId)
                        mAdapter.addItem(mExamData._id, examCategoryId, mExamData.color, mExamData.name, mExamData.dateName);
                }

            }

        }

        if (mAdapter.getItemCount() == 0) {
            mEmpty.setVisibility(View.VISIBLE);
        }

    }

    public class SimpleRecyclerViewAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        private final int mBackground;
        private ArrayList<listData> mValues = new ArrayList<>();

        public SimpleRecyclerViewAdapter(Context mContext) {
            TypedValue mTypedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final View headerDivider;
            public final TextView mHeader;

            public HeaderViewHolder(View view) {
                super(view);
                mView = view;
                headerDivider = view.findViewById(R.id.headerDivider);
                mHeader = (TextView) view.findViewById(R.id.mHeader);
            }
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final View mColor;
            public final TextView mExamName, mExamDate;

            public ItemViewHolder(View view) {
                super(view);
                mView = view;
                mColor = view.findViewById(R.id.mColor);
                mExamName = (TextView) view.findViewById(R.id.mExamName);
                mExamDate = (TextView) view.findViewById(R.id.mExamDate);
            }
        }

        public void addItem(int _categoryId, String name, int color) {
            categoryData mData = new categoryData(_categoryId, name, color);
            mValues.add(mData);
        }

        public void addItem(int _id, int category, int color, String name, String dateName) {
            examData mData = new examData(_id, category, color, name, dateName);
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
        public int getItemViewType(int position) {
            if (mValues.get(position).isHeader())
                return TYPE_HEADER;
            return TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_header, parent, false);
                view.setBackgroundResource(mBackground);
                return new HeaderViewHolder(view);
            } else if (viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_exam_list_item, parent, false);
                view.setBackgroundResource(mBackground);
                return new ItemViewHolder(view);
            }
            throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            listData mData = mValues.get(position);

            if (mData.isHeader()) {
                HeaderViewHolder mHolder = (HeaderViewHolder) holder;
                categoryData mCategoryData = (categoryData) mData;

                mHolder.mHeader.setText(mCategoryData.name);
                mHolder.headerDivider.setBackgroundColor(mCategoryData.color);
                mHolder.mView.setTag(mCategoryData);
                mHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        categoryData mTag = (categoryData) v.getTag();

                        int _categoryId = mTag._categoryId;
                        int color = mTag.color;
                        String name = mTag.name;

                        Intent mIntent = new Intent(v.getContext(), CategoryActivity.class);
                        mIntent.putExtra("type", 1);
                        mIntent.putExtra("_id", _categoryId);
                        mIntent.putExtra("color", color);
                        mIntent.putExtra("name", name);

                        v.getContext().startActivity(mIntent);

                    }
                });

            } else {
                ItemViewHolder mHolder = (ItemViewHolder) holder;
                examData mItemData = (examData) mData;

                GradientDrawable bgShape = (GradientDrawable) mHolder.mColor.getBackground();
                bgShape.setColor(mItemData.color);

                mHolder.mExamName.setText(mItemData.name);
                mHolder.mExamDate.setText(mItemData.dateName);

                mHolder.mView.setTag(mItemData);

                mHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        examData mTag = (examData) v.getTag();

                        Intent mIntent = new Intent(v.getContext(), ShowExamDetailActivity.class);
                        mIntent.putExtra("_id", mTag._id);
                        mIntent.putExtra("name", mTag.name);

                        startActivity(mIntent);
                    }
                });
                mHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        examData mTag = (examData) v.getTag();

                        Intent mIntent = new Intent(v.getContext(), ExamActivity.class);
                        mIntent.putExtra("type", 1);
                        mIntent.putExtra("_id", mTag._id);
                        mIntent.putExtra("name", mTag.name);

                        startActivity(mIntent);

                        return true;
                    }
                });
            }
        }
    }

    public interface listData {
        boolean isHeader();
    }

    public class categoryData implements listData {
        public String name;
        public int _categoryId, color;

        public categoryData(int _categoryId, String name, int color) {
            this.name = name;
            this._categoryId = _categoryId;
            this.color = color;
        }

        @Override
        public boolean isHeader() {
            return true;
        }
    }

    public class examData implements listData {
        public int _id, category, color;
        public String name, dateName;

        public examData(int _id, int category, int color, String name, String dateName) {
            this._id = _id;
            this.category = category;
            this.color = color;
            this.name = name;
            this.dateName = dateName;
        }

        @Override
        public boolean isHeader() {
            return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getExamList();
    }
}
