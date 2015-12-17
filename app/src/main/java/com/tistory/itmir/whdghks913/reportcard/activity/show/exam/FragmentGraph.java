package com.tistory.itmir.whdghks913.reportcard.activity.show.exam;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.Tools;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.CubicEase;
import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by whdghks913 on 2015-12-13.
 */
public class FragmentGraph extends Fragment {
    public static Fragment getInstance(int _id) {
        FragmentGraph mFragment = new FragmentGraph();

        Bundle args = new Bundle();
        args.putInt("_id", _id);
        mFragment.setArguments(args);

        return mFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_detail_graph, container, false);

        Bundle args = getArguments();
        int _id = args.getInt("_id");

        ArrayList<barSubjectData> mBarSetData = new ArrayList<>();
        ArrayList<ExamDataBaseInfo.subjectInExamData> mValues = ExamDataBaseInfo.getSubjectDataByExamId(_id);
        ArrayList<ExamDataBaseInfo.subjectData> subjectList = ExamDataBaseInfo.getSubjectList();
        ArrayList<Integer> mSubjectId = ExamDataBaseInfo.getSubjectIdList();
        ArrayList<Integer> mSubjectColor = ExamDataBaseInfo.getSubjectColorList();

        if (mValues == null || subjectList == null || mSubjectId == null || mSubjectColor == null)
            return mView;

        HorizontalBarChartView mBarChartView = (HorizontalBarChartView) mView.findViewById(R.id.mBarChartView);
        mBarChartView.setAxisBorderValues(0, 100, 20);
        mBarChartView.setBarSpacing(Tools.fromDpToPx(50));

        BarSet barSet = new BarSet();

        for (int i = 0; i < mValues.size(); i++) {
            ExamDataBaseInfo.subjectInExamData mData = mValues.get(i);

            int _subjectId = mData._subjectId;
            String name = mData.name;
            int color = mSubjectColor.get(mSubjectId.indexOf(_subjectId));
            float score = mData.score;

            barSubjectData mBarData = new barSubjectData();
            mBarData.name = name;
            mBarData.color = color;
            mBarData.score = score;
            mBarSetData.add(mBarData);
        }

        Collections.sort(mBarSetData, ALPHA_COMPARATOR);

        for (int i = 0; i < mBarSetData.size(); i++) {
            barSubjectData mData = mBarSetData.get(i);
            Bar bar = new Bar(mData.name, mData.score);
            bar.setColor(mData.color);
            barSet.addBar(bar);
        }

        mBarChartView.addData(barSet);

        Animation anim = new Animation(500);
        anim.setEasing(new CubicEase());
        mBarChartView.show(anim);

        return mView;
    }

    /**
     * 알파벳순으로 정렬
     */
    public static final Comparator<barSubjectData> ALPHA_COMPARATOR = new Comparator<barSubjectData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(barSubjectData arg1, barSubjectData arg2) {
            return sCollator.compare(arg2.name, arg1.name);
        }
    };

    class barSubjectData {
        public String name;
        public int color;
        public float score;
    }

}

