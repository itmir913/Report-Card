package com.tistory.itmir.whdghks913.reportcard.activity.show.exam;

import android.content.Context;
import android.content.Intent;
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
import com.tistory.itmir.whdghks913.reportcard.activity.edit.EditSubjectScoreActivity;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by whdghks913 on 2015-12-13.
 */
public class FragmentSubjectList extends Fragment {
    public static Fragment getInstance(int _id) {
        FragmentSubjectList mFragment = new FragmentSubjectList();

        Bundle args = new Bundle();
        args.putInt("_id", _id);
//        args.putString("title", title);
        mFragment.setArguments(args);

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_detail_subject, container, false);
        RecyclerView recyclerView = (RecyclerView) mView.findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        Bundle args = getArguments();
        int _id = args.getInt("_id");
//        String title = args.getString("title");

        AdapterSubject mAdapter = new AdapterSubject(getActivity());
        recyclerView.setAdapter(mAdapter);

        ArrayList<ExamDataBaseInfo.subjectInExamData> mValues = ExamDataBaseInfo.getSubjectDataByExamId(_id);
        if (mValues == null)
            return mView;


        for (int i = 0; i < mValues.size(); i++) {
            ExamDataBaseInfo.subjectInExamData mData = mValues.get(i);

            mAdapter.addItem(_id, mData._subjectId, mData.name, mData.score, mData.rank, mData.applicants, mData.mClass);
        }

        mAdapter.sort();

        return mView;
    }
}

class AdapterSubject extends RecyclerView.Adapter<AdapterSubject.SubjectViewHolder> {
    private final int mBackground;
    private ArrayList<SubjectListInfo> mValues = new ArrayList<>();

    public AdapterSubject(Context mContext) {
        TypedValue mTypedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
    }

    public class SubjectViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName, mScore, mRank, mClass;

        public SubjectViewHolder(View mView) {
            super(mView);
            this.mView = mView;
            mName = (TextView) mView.findViewById(R.id.mName);
            mScore = (TextView) mView.findViewById(R.id.mScore);
            mRank = (TextView) mView.findViewById(R.id.mRank);
            mClass = (TextView) mView.findViewById(R.id.mClass);
        }
    }

    public void addItem(int _id, int subjectId, String name, float score, int rank, int applicants, int mClass) {
        SubjectListInfo addInfo = new SubjectListInfo();

        addInfo._id = _id;
        addInfo.subjectId = subjectId;
        addInfo.name = name;
        addInfo.score = score;
        addInfo.rank = rank;
        addInfo.applicants = applicants;
        addInfo.mClass = mClass;

        mValues.add(addInfo);
    }

    @Override
    public AdapterSubject.SubjectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_detail_subject_item, parent, false);
        mView.setBackgroundResource(mBackground);

        return new SubjectViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(final SubjectViewHolder holder, int position) {
        SubjectListInfo mInfo = getItemData(position);

        holder.mName.setText(mInfo.name);
        holder.mScore.setText(String.format(holder.mRank.getContext().getString(R.string.score_format), mInfo.score));
        holder.mRank.setText(String.format(holder.mRank.getContext().getString(R.string.rank_format), mInfo.rank, mInfo.applicants));
        holder.mClass.setText(String.format(holder.mRank.getContext().getString(R.string.class_format), mInfo.mClass));

        holder.mView.setTag(mInfo);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SubjectListInfo mInfo = (SubjectListInfo) view.getTag();
                Intent mIntent = new Intent(view.getContext(), EditSubjectScoreActivity.class);

                mIntent.putExtra("_id", mInfo._id);
                mIntent.putExtra("subjectId", mInfo.subjectId);
                mIntent.putExtra("name", mInfo.name);

                mIntent.putExtra("score", mInfo.score);
                mIntent.putExtra("rank", mInfo.rank);
                mIntent.putExtra("applicants", mInfo.applicants);
                mIntent.putExtra("mClass", mInfo.mClass);

                view.getContext().startActivity(mIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void sort() {
        Collections.sort(mValues, ALPHA_COMPARATOR);
    }

    public SubjectListInfo getItemData(int position) {
        return mValues.get(position);
    }

    public class SubjectListInfo {
        public int _id;
        public int subjectId;
        public float score;
        public int rank, applicants, mClass;
        public String name;
    }

    /**
     * 알파벳순으로 정렬
     */
    public final Comparator<SubjectListInfo> ALPHA_COMPARATOR = new Comparator<SubjectListInfo>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(SubjectListInfo arg1, SubjectListInfo arg2) {
            return sCollator.compare(arg1.name, arg2.name);
        }
    };
}

