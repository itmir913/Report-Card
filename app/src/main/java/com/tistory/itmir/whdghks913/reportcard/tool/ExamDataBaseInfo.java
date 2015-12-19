package com.tistory.itmir.whdghks913.reportcard.tool;

import android.database.Cursor;

import java.io.File;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by whdghks913 on 2015-12-13.
 */
public class ExamDataBaseInfo {
    /**
     * Database
     */
    public static Database mDatabase;

    public static void initDatabase() {
        if (mDatabase == null) {
            mDatabase = new Database();
            mDatabase.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);
        }
    }

    /**
     * DB의 기본적인 내용
     */
    public static final String dataBasePath = "/data/data/com.tistory.itmir.whdghks913.reportcard/databases/";
    public static final String dataBaseName = "ExaminationData.db";

    /**
     * 시험 카테고리 (내신, 모의고사, etc..)
     */
    public static final String categoryExamTableName = "categoryExam";
    public static final String categoryExamTableColumn = "name text, color integer";

    /**
     * 시험 리스트를 저장하는 테이블
     */
    public static final String examListTableName = "examNameList";
    public static final String examListTableColumn = "name text, category integer, year integer, month integer, day integer, color integer";

    /**
     * 과목을 저장하는 세부 테이블
     * name 과목 이름
     * color 과목 색상
     */
    public static final String subjectTableName = "subjectList";
    public static final String subjectColumn = "name text, color integer";

    /**
     * 시험 정보를 저장하는 세부 테이블
     * 테이블 이름은 시험 리스트에 저장된 _id값
     * name : 과목 이름
     * score : 받은 점수
     * rank : 전교 석차
     * applicants : 응시자 수
     * class : 등급
     * average : 과목 평군
     * standardDeviation : 과목 표준 편차
     */
    public static final String examDetailedColumn = "name integer, score integer, rank integer, applicants integer, class integer, average integer, standardDeviation integer";

    public static String getExamTable(int _id) {
        return "exam_" + _id;
    }

    public static boolean isDatabaseExists() {
        return new File(dataBasePath + dataBaseName).exists();
    }

    public static ArrayList<categoryData> getCategoryList() {
        initDatabase();

        Cursor mCategoryCursor = mDatabase.getFirstData(ExamDataBaseInfo.categoryExamTableName);

        ArrayList<categoryData> mValues = new ArrayList<>();

        for (int i = 0; i < mCategoryCursor.getCount(); i++) {
            categoryData mData = new categoryData();

            mData._categoryId = mCategoryCursor.getInt(0);
            mData.name = mCategoryCursor.getString(1);
            mData.color = mCategoryCursor.getInt(2);

            mValues.add(mData);

            mCategoryCursor.moveToNext();
        }

        Collections.sort(mValues, CATEGORY_LIST);

        return mValues;
    }

    public static class categoryData {
        public String name;
        public int _categoryId, color;
    }

    public static final Comparator<categoryData> CATEGORY_LIST = new Comparator<categoryData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(categoryData arg1, categoryData arg2) {
            return sCollator.compare(arg1.name, arg2.name);
        }
    };

    /**
     * 시험 리스트를 가져오는 메소드
     */
    public static ArrayList<examData> getExamList() {
        initDatabase();

        Cursor mExamListCursor = mDatabase.getData(ExamDataBaseInfo.examListTableName);

        if (mExamListCursor == null)
            return null;

        ArrayList<examData> mValues = new ArrayList<>();

        for (int i = 0; i < mExamListCursor.getCount(); i++) {
            mExamListCursor.moveToNext();

            examData mData = new examData();

            int _id = mExamListCursor.getInt(0);
            String name = mExamListCursor.getString(1);
            int category = mExamListCursor.getInt(2);
            int year = mExamListCursor.getInt(3);
            int month = mExamListCursor.getInt(4);
            int day = mExamListCursor.getInt(5);
            int color = mExamListCursor.getInt(6);

            mData._id = _id;
            mData.name = name;
            mData.category = category;
            mData.year = year;
            mData.month = month;
            mData.day = day;
            mData.color = color;

            SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd E요일", Locale.KOREA);
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(year, month, day);

            mData.dateName = mFormat.format(mCalendar.getTime());

            mValues.add(mData);
        }

        Collections.sort(mValues, EXAM_LIST);

        return mValues;
    }

    public static class examData {
        public int _id, category, color;
        public int year, month, day;
        public String name, dateName;
    }

    public static final Comparator<examData> EXAM_LIST = new Comparator<examData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(examData arg1, examData arg2) {
            if (arg1.dateName.equals(arg2.dateName))
                return sCollator.compare(arg1.name, arg2.name);
            else
                return sCollator.compare(arg1.dateName, arg2.dateName);
        }
    };

    /**
     * 과목 리스트를 가져오는 메소드
     */
    public static ArrayList<subjectData> getSubjectList() {
        initDatabase();

        Cursor mSubjectListCursor = mDatabase.getData(ExamDataBaseInfo.subjectTableName);

        if (mSubjectListCursor == null)
            return null;

        ArrayList<subjectData> mValues = new ArrayList<>();

        for (int i = 0; i < mSubjectListCursor.getCount(); i++) {
            mSubjectListCursor.moveToNext();

            int _subjectId = mSubjectListCursor.getInt(0);
            String name = mSubjectListCursor.getString(1);
            int color = mSubjectListCursor.getInt(2);

            subjectData mData = new subjectData();
            mData._subjectId = _subjectId;
            mData.name = name;
            mData.color = color;

            mValues.add(mData);
        }

        return mValues;
    }

    public static class subjectData {
        public int _subjectId, color;
        public String name;
    }

    /**
     * 시험 고유 _id를 이용하여 저장된 과목의 데이터를 가져오는 메소드
     */
    public static ArrayList<subjectInExamData> getSubjectDataByExamId(int _id) {
        initDatabase();

        Cursor mCursor = mDatabase.getFirstData(ExamDataBaseInfo.getExamTable(_id));

        if (mCursor == null)
            return null;

        ArrayList<subjectInExamData> mValues = new ArrayList<>();

        for (int i = 0; i < mCursor.getCount(); i++) {
            int _subjectId = mCursor.getInt(1);
            Cursor mSubjectNameCursor = mDatabase.getData(ExamDataBaseInfo.subjectTableName, "name", "_id", _subjectId);
            mSubjectNameCursor.moveToNext();

            String name = mSubjectNameCursor.getString(0);
            float score = mCursor.getFloat(2);
            int rank = mCursor.getInt(3);
            int applicants = mCursor.getInt(4);
            int mClass = mCursor.getInt(5);
            float average = mCursor.getFloat(6);
            float standardDeviation = mCursor.getFloat(7);

            subjectInExamData mData = new subjectInExamData();
            mData._id = _id;
            mData._subjectId = _subjectId;
            mData.score = score;
            mData.rank = rank;
            mData.applicants = applicants;
            mData.mClass = mClass;
            mData.average = average;
            mData.standardDeviation = standardDeviation;
            mData.name = name;

            mValues.add(mData);

            mCursor.moveToNext();
        }

        return mValues;
    }

    public static class subjectInExamData {
        public int _id, _subjectId, rank, applicants, mClass;
        public float score, average, standardDeviation;
        public String name;
    }

    /**
     * 과목의 subjectId만 가져오는 메소드
     */
    public static ArrayList<Integer> getSubjectIdList() {
        initDatabase();

        Cursor mSubjectListCursor = mDatabase.getData(ExamDataBaseInfo.subjectTableName);

        if (mSubjectListCursor == null)
            return null;

        ArrayList<Integer> mValues = new ArrayList<>();

        for (int i = 0; i < mSubjectListCursor.getCount(); i++) {
            mSubjectListCursor.moveToNext();

            int _subjectId = mSubjectListCursor.getInt(0);

            mValues.add(_subjectId);
        }

        return mValues;
    }

    /**
     * 과목의 color 가져오는 메소드
     */
    public static ArrayList<Integer> getSubjectColorList() {
        initDatabase();

        Cursor mSubjectListCursor = mDatabase.getData(ExamDataBaseInfo.subjectTableName);

        if (mSubjectListCursor == null)
            return null;

        ArrayList<Integer> mValues = new ArrayList<>();

        for (int i = 0; i < mSubjectListCursor.getCount(); i++) {
            mSubjectListCursor.moveToNext();

            int color = mSubjectListCursor.getInt(2);

            mValues.add(color);
        }

        return mValues;
    }


}
