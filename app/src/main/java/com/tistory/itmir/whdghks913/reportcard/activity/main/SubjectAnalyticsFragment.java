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

        getSubjectList();

        return mView;
    }

    private void getSubjectList() {
        mAdapter.clear();
        mAdapter.notifyDataSetChanged();

        ArrayList<ExamDataBaseInfo.subjectData> mValues = ExamDataBaseInfo.getSubjectList();
        if (mValues == null)
            return;

        for (int i = 0; i < mValues.size(); i++) {
            ExamDataBaseInfo.subjectData mData = mValues.get(i);
            mAdapter.addItem(mData._subjectId, mData.name, mData.color);
        }
    }

    public class SimpleRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleRecyclerViewAdapter.ViewHolder> {

        private final int mBackground;
        private ArrayList<ExamDataBaseInfo.subjectData> mValues = new ArrayList<>();

        public SimpleRecyclerViewAdapter(Context mContext) {
            TypedValue mTypedValue = new TypedValue();
            mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final View mColor;
            public final TextView mSubjectName;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mColor = view.findViewById(R.id.mColor);
                mSubjectName = (TextView) view.findViewById(R.id.mName);
            }
        }

        public void addItem(int _subjectId, String name, int color) {
            ExamDataBaseInfo.subjectData mData = new ExamDataBaseInfo.subjectData();

            mData._subjectId = _subjectId;
            mData.color = color;
            mData.name = name;

            mValues.add(mData);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

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
            ExamDataBaseInfo.subjectData mData = mValues.get(position);

            GradientDrawable bgShape = (GradientDrawable) holder.mColor.getBackground();
            bgShape.setColor(mData.color);

            holder.mSubjectName.setText(mData.name);
            holder.mView.setTag(mData);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExamDataBaseInfo.subjectData mData = (ExamDataBaseInfo.subjectData) v.getTag();
                    int _subjectId = mData._subjectId;
//                    int color = mData.color;
                    String name = mData.name;

                    Intent mIntent = new Intent(v.getContext(), AnalyticsSubjectActivity.class);
                    mIntent.putExtra("_id", _subjectId);
//                    mIntent.putExtra("color", color);
                    mIntent.putExtra("name", name);

                    v.getContext().startActivity(mIntent);
                }
            });
        }
    }

}
