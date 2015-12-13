package com.tistory.itmir.whdghks913.reportcard.activity.show.exam;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tistory.itmir.whdghks913.reportcard.R;

import java.util.ArrayList;

/**
 * Created by whdghks913 on 2015-12-13.
 */
public class AdapterSubject extends RecyclerView.Adapter<AdapterSubject.SubjectViewHolder> {
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

    public void addItem(String name, int score, int rank, int applicants, int mClass) {
        SubjectListInfo addInfo = new SubjectListInfo();

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

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public SubjectListInfo getItemData(int position) {
        return mValues.get(position);
    }

    public class SubjectListInfo {
        public int score, rank, applicants, mClass;
        public String name;
    }
}
