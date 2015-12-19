package com.tistory.itmir.whdghks913.reportcard.activity.show.exam;

import android.animation.PropertyValuesHolder;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.db.chart.Tools;
import com.db.chart.model.Bar;
import com.db.chart.model.BarSet;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.HorizontalBarChartView;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.XController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.CubicEase;
import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.math.BigDecimal;
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

        ArrayList<ExamDataBaseInfo.subjectInExamData> mValues = ExamDataBaseInfo.getSubjectDataByExamId(_id);
        ArrayList<ExamDataBaseInfo.subjectData> subjectList = ExamDataBaseInfo.getSubjectList();
        ArrayList<Integer> mSubjectId = ExamDataBaseInfo.getSubjectIdList();
        ArrayList<Integer> mSubjectColor = ExamDataBaseInfo.getSubjectColorList();

        if (mValues == null || subjectList == null || mValues.size() == 0 || subjectList.size() == 0 || mSubjectId == null || mSubjectColor == null)
            return mView;

        ArrayList<chartData> mChartData = new ArrayList<>();
        for (int i = 0; i < mValues.size(); i++) {
            ExamDataBaseInfo.subjectInExamData mData = mValues.get(i);

            chartData mBarData = new chartData();
            mBarData.name = mData.name;
            mBarData.color = mSubjectColor.get(mSubjectId.indexOf(mData._subjectId));
            mBarData.score = mData.score;
            mBarData.applicants = mData.applicants;
            mBarData.rank = mData.rank;
            mChartData.add(mBarData);
        }

        Collections.sort(mChartData, ALPHA_COMPARATOR);

        showScoreGraph(mView, mChartData);
        showPercentageGraph(mView, mChartData);

        return mView;
    }

    private void showScoreGraph(View mView, ArrayList<chartData> mChartData) {
        HorizontalBarChartView mBarChartView = (HorizontalBarChartView) mView.findViewById(R.id.mBarChartView);

        BarSet barSet = new BarSet();

        int size = mChartData.size();
        for (int i = 0; i < size; i++) {
            chartData mData = mChartData.get(i);
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
        mParams.height = (size == 1) ? 150 : 220 + (size * 40);
        mBarChartView.setLayoutParams(mParams);

        Animation anim = new Animation(3000);
        anim.setAlpha(2);
        mBarChartView.show(anim);
    }

    private void showPercentageGraph(View mView, ArrayList<chartData> mChartData) {
        LineChartView mLineChartView = (LineChartView) mView.findViewById(R.id.mPercentageLineChartView);

        LineSet listSet = new LineSet();

        int count = 0;

        int size = mChartData.size();
        for (int i = size - 1; i >= 0; i--) {
            chartData mData = mChartData.get(i);

            int rank = mData.rank;
            int applicants = mData.applicants;

            boolean isZero = ((rank == 0) || (applicants == 0));
            if (isZero)
                continue;

            float percentage = 100 - (new BigDecimal(rank))
                    .divide(new BigDecimal(applicants), 2, BigDecimal.ROUND_UP)
                    .multiply(new BigDecimal("100"))
                    .floatValue();

            Point point = new Point(mData.name, percentage);
            point.setColor(Color.parseColor("#eef1f6"));
            point.setStrokeColor(mData.color);
            point.setStrokeThickness(Tools.fromDpToPx(4));

            listSet.addPoint(point);
            count++;
        }

        if (count == 0)
            return;

        Tooltip mTooltip = new Tooltip(getActivity(), R.layout.tooltip_linechart, R.id.value);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mTooltip.setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));

            mTooltip.setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                    PropertyValuesHolder.ofFloat(View.SCALE_X, 0f),
                    PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f));
        }
        mLineChartView.setTooltips(mTooltip);

        listSet.setColor(Color.parseColor("#FF8E8A84"));
        listSet.setThickness(Tools.fromDpToPx(2));

        mLineChartView.addData(listSet);

        mLineChartView.setAxisBorderValues(0, 100, 20)
                .setLabelsColor(Color.parseColor("#FF8E8A84"))
                .setAxisColor(Color.parseColor("#FF8E8A84"));

        // Animation customization
        mLineChartView.show(new Animation(1500).setEasing(new CubicEase()).setAlpha(2));
    }

    /**
     * 알파벳순으로 정렬
     */
    public final Comparator<chartData> ALPHA_COMPARATOR = new Comparator<chartData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(chartData arg1, chartData arg2) {
            return sCollator.compare(arg2.name, arg1.name);
        }
    };

    class chartData {
        public String name;
        public int color;
        public float score;
        public int rank, applicants;
    }

}

