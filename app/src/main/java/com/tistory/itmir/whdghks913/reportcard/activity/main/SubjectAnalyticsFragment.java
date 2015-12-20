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
import com.tistory.itmir.whdghks913.reportcard.activity.analytics.AnalyticsSubjectActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.modify.CategoryActivity;
import com.tistory.itmir.whdghks913.reportcard.activity.modify.SubjectActivity;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.util.ArrayList;

public class SubjectAnalyticsFragment extends Fragment {
    private SimpleRecyclerViewAdapter mAdapter;

    public static SubjectAnalyticsFragment newInstance() {
        return new SubjectAnalyticsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.recyclerview, container, false);

        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new SimpleRecyclerViewAdapter(getActivity());
        recyclerView.setAdapter(mAdapter);

        getSubjectList(mView);

        return mView;
    }

    private void getSubjectList(View mView) {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        ArrayList<ExamDataBaseInfo.subjectData> mValues = ExamDataBaseInfo.getSubjectList();
        ArrayList<ExamDataBaseInfo.categoryData> mCategoryValues = ExamDataBaseInfo.getSubjectCategoryList();
        if (mValues == null || mValues.size() == 0) {
            mView.findViewById(R.id.mEmpty).setVisibility(View.VISIBLE);
            return;
        }

        ArrayList<Integer> mCategoryIdInExam = new ArrayList<>();
        for (int i = 0; i < mValues.size(); i++) {
            mCategoryIdInExam.add(mValues.get(i).category);
        }

        for (int i = 0; i < mCategoryValues.size(); i++) {
            ExamDataBaseInfo.categoryData mCategoryData = mCategoryValues.get(i);
            int _categoryId = mCategoryData._categoryId;
            if (mCategoryIdInExam.contains(_categoryId)) {
                mAdapter.addItem(0, _categoryId, mCategoryData.name, mCategoryData.color, true);

                for (int jj = 0; jj < mValues.size(); jj++) {
                    ExamDataBaseInfo.subjectData mData = mValues.get(jj);

                    int examCategoryId = mData.category;

                    if (examCategoryId == _categoryId)
                        mAdapter.addItem(mData._subjectId, mData.category, mData.name, mData.color, false);
                }

            }

        }

        if (mAdapter.getItemCount() == 0) {
            mView.findViewById(R.id.mEmpty).setVisibility(View.VISIBLE);
        }

    }

    public class SimpleRecyclerViewAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        private final int mBackground;
        private ArrayList<listData> mValues = new ArrayList<>();
//        private ArrayList<ExamDataBaseInfo.subjectData> mValues = new ArrayList<>();

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
            public final TextView mName;

            public ItemViewHolder(View view) {
                super(view);
                mView = view;
                mColor = view.findViewById(R.id.mColor);
                mName = (TextView) view.findViewById(R.id.mName);
            }
        }

        public void addItem(int _subjectId, int _categoryId, String name, int color, boolean isHeader) {
            subjectData mData = new subjectData(_subjectId, _categoryId, name, color, isHeader);
            mValues.add(mData);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (mValues.get(position).isHeader())
                return TYPE_HEADER;
            return TYPE_ITEM;
        }

        public void clear() {
            mValues.clear();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEADER) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_list_header, parent, false);
                view.setBackgroundResource(mBackground);
                return new HeaderViewHolder(view);
            } else if (viewType == TYPE_ITEM) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_simple_list_item, parent, false);
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
                subjectData mCategoryData = (subjectData) mData;

                mHolder.mHeader.setText(mCategoryData.name);
                mHolder.headerDivider.setBackgroundColor(mCategoryData.color);
                mHolder.mView.setTag(mCategoryData);
                mHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subjectData mTag = (subjectData) v.getTag();

                        int _categoryId = mTag._categoryId;
                        int color = mTag.color;
                        String name = mTag.name;

                        Intent mIntent = new Intent(v.getContext(), CategoryActivity.class);
                        mIntent.putExtra("type", 1);

                        mIntent.putExtra("_id", _categoryId);
                        mIntent.putExtra("color", color);
                        mIntent.putExtra("name", name);

                        mIntent.putExtra("isExam", false);

                        v.getContext().startActivity(mIntent);

                    }
                });

            } else {
                ItemViewHolder mHolder = (ItemViewHolder) holder;
                subjectData mItemData = (subjectData) mData;

                GradientDrawable bgShape = (GradientDrawable) mHolder.mColor.getBackground();
                bgShape.setColor(mItemData.color);

                mHolder.mName.setText(mItemData.name);
                mHolder.mView.setTag(mItemData);

                mHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        subjectData mData = (subjectData) v.getTag();

                        Intent mIntent = new Intent(v.getContext(), AnalyticsSubjectActivity.class);
                        mIntent.putExtra("_id", mData._subjectId);
                        mIntent.putExtra("name", mData.name);

                        startActivity(mIntent);
                    }
                });
                mHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        subjectData mData = (subjectData) v.getTag();

                        Intent mIntent = new Intent(v.getContext(), SubjectActivity.class);
                        mIntent.putExtra("type", 1);
                        mIntent.putExtra("_id", mData._subjectId);
                        mIntent.putExtra("color", mData.color);
                        mIntent.putExtra("name", mData.name);
                        mIntent.putExtra("category", mData._categoryId);

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

    public class subjectData implements listData {
        public String name;
        public int _subjectId, _categoryId, color;
        public boolean isHeader;

        public subjectData(int _subjectId, int _categoryId, String name, int color, boolean isHeader) {
            this.name = name;
            this._subjectId = _subjectId;
            this._categoryId = _categoryId;
            this.color = color;
            this.isHeader = isHeader;
        }

        @Override
        public boolean isHeader() {
            return isHeader;
        }
    }

}
