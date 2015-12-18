package com.tistory.itmir.whdghks913.reportcard.activity.show.exam;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.Tools;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.XController;
import com.db.chart.view.animation.Animation;
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

        showScoreGraph(mView, _id);

        return mView;
    }

    private void showScoreGraph(View mView, int _id) {
        ArrayList<ExamDataBaseInfo.subjectInExamData> mValues = ExamDataBaseInfo.getSubjectDataByExamId(_id);
        ArrayList<ExamDataBaseInfo.subjectData> subjectList = ExamDataBaseInfo.getSubjectList();

        if (mValues == null || subjectList == null)
            return;
        if (mValues.size() == 0 || subjectList.size() == 0)
            return;

        ArrayList<barSubjectData> mBarSetData = new ArrayList<>();
        ArrayList<Integer> mSubjectId = ExamDataBaseInfo.getSubjectIdList();
        ArrayList<Integer> mSubjectColor = ExamDataBaseInfo.getSubjectColorList();

        HorizontalBarChartView mBarChartView = (HorizontalBarChartView) mView.findViewById(R.id.mBarChartView);

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

        int size = mBarSetData.size();
        for (int i = 0; i < size; i++) {
            barSubjectData mData = mBarSetData.get(i);
            Bar bar = new Bar(mData.name, mData.score);
            bar.setColor(mData.color);
            barSet.addBar(bar);
        }
        mBarChartView.addData(barSet);

        mBarChartView.setBorderSpacing(Tools.fromDpToPx(5))
                .setAxisBorderValues(0, 100, 20)
                .setXAxis(false)
                .setYAxis(false)
                .setLabelsColor(Color.parseColor("#FF8E8A84"))
                .setXLabels(XController.LabelPosition.OUTSIDE);

        ViewGroup.LayoutParams mParams = mBarChartView.getLayoutParams();
        mParams.height = (size == 1) ? 200 : 235 + (size * 15);
        mBarChartView.setLayoutParams(mParams);

        Animation anim = new Animation(500);
        mBarChartView.show(anim);
    }

    /**
     * 알파벳순으로 정렬
     */
    public final Comparator<barSubjectData> ALPHA_COMPARATOR = new Comparator<barSubjectData>() {
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

