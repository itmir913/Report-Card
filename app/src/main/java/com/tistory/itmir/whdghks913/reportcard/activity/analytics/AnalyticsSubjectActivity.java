package com.tistory.itmir.whdghks913.reportcard.activity.analytics;

import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.LineChartView;
import com.db.chart.view.Tooltip;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BounceEase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tistory.itmir.whdghks913.reportcard.R;
import com.tistory.itmir.whdghks913.reportcard.tool.ExamDataBaseInfo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class AnalyticsSubjectActivity extends AppCompatActivity {
    private int _subjectId;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics_subject);

        Intent mIntent = getIntent();
        _subjectId = mIntent.getIntExtra("_id", 0);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.mToolbar);
        mToolbar.setTitle(String.format(getString(R.string.title_activity_analytics_subject_format), mIntent.getStringExtra("name")));
        setSupportActionBar(mToolbar);

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

        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = com.tistory.itmir.whdghks913.reportcard.tool.Tools.MD5(android_id).toUpperCase();

        mAdView = (AdView) findViewById(R.id.adView);
        mAdView.loadAd(new AdRequest.Builder()
                .addTestDevice(deviceId)
                .build());

        showChart();
    }

    private void showChart() {
        ArrayList<ExamDataBaseInfo.examData> mData = ExamDataBaseInfo.getNewExamList(10);

        LineChartView mScoreChart = (LineChartView) findViewById(R.id.mScoreChart);
        LineChartView mClassChart = (LineChartView) findViewById(R.id.mClassChart);
        LineChartView mRankChart = (LineChartView) findViewById(R.id.mRankChart);
        LineChartView mPercentageChart = (LineChartView) findViewById(R.id.mPercentageChart);

        LineSet scoreListSet = new LineSet();
        LineSet classListSet = new LineSet();
        LineSet rankListSet = new LineSet();
        LineSet percentageListSet = new LineSet();

        int count = 0;

        float maxScore = 0;
        int maxClass = 0, maxRank = 0;

        int size = mData.size();
        for (int i = size - 1; i >= 0; i--) {
            ExamDataBaseInfo.examData mExamData = mData.get(i);
            ExamDataBaseInfo.subjectInExamData mSubjectData = ExamDataBaseInfo.getSubjectDataByExamId(mExamData._id, _subjectId);

            if (mSubjectData != null) {
                Point scorePoint = new Point(mExamData.name, mSubjectData.score);
                scorePoint.setColor(Color.parseColor("#eef1f6"));
                scorePoint.setStrokeColor(mExamData.color);
                scorePoint.setStrokeThickness(Tools.fromDpToPx(4));
                scoreListSet.addPoint(scorePoint);

                Point classPoint = new Point(mExamData.name, mSubjectData.mClass);
                classPoint.setColor(Color.parseColor("#eef1f6"));
                classPoint.setStrokeColor(mExamData.color);
                classPoint.setStrokeThickness(Tools.fromDpToPx(4));
                classListSet.addPoint(classPoint);

                Point rankPoint = new Point(mExamData.name, mSubjectData.rank);
                rankPoint.setColor(Color.parseColor("#eef1f6"));
                rankPoint.setStrokeColor(mExamData.color);
                rankPoint.setStrokeThickness(Tools.fromDpToPx(4));
                rankListSet.addPoint(rankPoint);

                boolean isZero = ((mSubjectData.rank == 0) || (mSubjectData.applicants == 0));
                float percentage = mSubjectData.percentage;
                if (percentage == 0.0f) {
                    percentage = isZero ? 0 : 100 - (new BigDecimal(mSubjectData.rank))
                            .divide(new BigDecimal(mSubjectData.applicants), 2, BigDecimal.ROUND_UP)
                            .multiply(new BigDecimal("100"))
                            .floatValue();
                }

                Point percentagePoint = new Point(mExamData.name, percentage);
                percentagePoint.setColor(Color.parseColor("#eef1f6"));
                percentagePoint.setStrokeColor(mExamData.color);
                percentagePoint.setStrokeThickness(Tools.fromDpToPx(4));
                percentageListSet.addPoint(percentagePoint);

                if (maxScore < mSubjectData.score)
                    maxScore = mSubjectData.score;
                if (maxClass < mSubjectData.mClass)
                    maxClass = mSubjectData.mClass;
                if (maxRank < mSubjectData.rank)
                    maxRank = mSubjectData.rank;

                count++;
            }
        }

        if (count == 0)
            return;

        int score = (maxScore == 0) ? 0 : (new BigDecimal(maxScore))
                .divide(new BigDecimal(10), 0, BigDecimal.ROUND_UP)
                .multiply(new BigDecimal(10))
                .intValue();
        int mClass = (maxClass == 0) ? 0 : (new BigDecimal(maxClass))
                .divide(new BigDecimal(1), 0, BigDecimal.ROUND_UP)
                .multiply(new BigDecimal(1))
                .intValue();
        int rank = (maxRank == 0) ? 0 : (new BigDecimal(maxRank))
                .divide(new BigDecimal(10), 0, BigDecimal.ROUND_UP)
                .multiply(new BigDecimal(10))
                .intValue();

        Tooltip[] mTooltipList = new Tooltip[3];
        for (int i = 0; i < 3; i++) {
            mTooltipList[i] = new Tooltip(this, R.layout.tooltip_linechart, R.id.value);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                mTooltipList[i].setEnterAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 1),
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f));

                mTooltipList[i].setExitAnimation(PropertyValuesHolder.ofFloat(View.ALPHA, 0),
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 0f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f));
            }
        }
        mScoreChart.setTooltips(mTooltipList[0]);
        mRankChart.setTooltips(mTooltipList[1]);
        mPercentageChart.setTooltips(mTooltipList[2]);

        scoreListSet.setColor(Color.parseColor("#FF8E8A84")).setThickness(Tools.fromDpToPx(2));
        classListSet.setColor(Color.parseColor("#FF8E8A84")).setThickness(Tools.fromDpToPx(2));
        rankListSet.setColor(Color.parseColor("#FF8E8A84")).setThickness(Tools.fromDpToPx(2));
        percentageListSet.setColor(Color.parseColor("#FF8E8A84")).setThickness(Tools.fromDpToPx(2));

        mScoreChart.addData(scoreListSet);
        mClassChart.addData(classListSet);
        mRankChart.addData(rankListSet);
        mPercentageChart.addData(percentageListSet);

        mScoreChart.setAxisBorderValues(0, score, (score / 5))
                .setLabelsColor(Color.parseColor("#FF8E8A84"))
                .setAxisColor(Color.parseColor("#FF8E8A84"))
                .setLabelsFormat(new DecimalFormat("#점"));

        mClassChart.setAxisBorderValues(0, mClass, 1)
                .setLabelsColor(Color.parseColor("#FF8E8A84"))
                .setAxisColor(Color.parseColor("#FF8E8A84"))
                .setLabelsFormat(new DecimalFormat("#등급"));

        mRankChart.setAxisBorderValues(0, rank, 10)
                .setLabelsColor(Color.parseColor("#FF8E8A84"))
                .setAxisColor(Color.parseColor("#FF8E8A84"));

        mPercentageChart.setAxisBorderValues(0, 100, 20)
                .setLabelsColor(Color.parseColor("#FF8E8A84"))
                .setAxisColor(Color.parseColor("#FF8E8A84"));

        mScoreChart.show(new Animation(1500).setEasing(new BounceEase()).setAlpha(2));
        mClassChart.show(new Animation(1500).setEasing(new BounceEase()).setAlpha(2));
        mRankChart.show(new Animation(1500).setEasing(new BounceEase()).setAlpha(2));
        mPercentageChart.show(new Animation(1500).setEasing(new BounceEase()).setAlpha(2));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mAdView != null)
            mAdView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mAdView != null)
            mAdView.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mAdView != null)
            mAdView.destroy();
    }

}
