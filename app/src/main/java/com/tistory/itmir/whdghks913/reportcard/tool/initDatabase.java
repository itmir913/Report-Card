package com.tistory.itmir.whdghks913.reportcard.tool;

/**
 * Created by whdghks913 on 2015-12-13.
 */
public class initDatabase {
    public void init() {
        Database mData = new Database();
        mData.openDatabase(ExamDataBaseInfo.dataBasePath, ExamDataBaseInfo.dataBaseName);
        mData.createTable(ExamDataBaseInfo.categoryExamTableName, ExamDataBaseInfo.categoryExamTableColumn);
        mData.createTable(ExamDataBaseInfo.categorySubjectTableName, ExamDataBaseInfo.categorySubjectTableColumn);
        mData.createTable(ExamDataBaseInfo.examListTableName, ExamDataBaseInfo.examListTableColumn);
        mData.createTable(ExamDataBaseInfo.subjectTableName, ExamDataBaseInfo.subjectColumn);

        mData.addData("name", "기본");
        mData.addData("color", -16738680);
        mData.commit(ExamDataBaseInfo.categoryExamTableName);
        mData.addData("name", "내신");
        mData.addData("color", -16537100);
        mData.commit(ExamDataBaseInfo.categoryExamTableName);
        mData.addData("name", "모의고사");
        mData.addData("color", -7617718);
        mData.commit(ExamDataBaseInfo.categoryExamTableName);

        mData.addData("name", "기본 과목");
        mData.addData("color", -16738680);
        mData.commit(ExamDataBaseInfo.categorySubjectTableName);
        mData.addData("name", "내신 과목");
        mData.addData("color", -9920712);
        mData.commit(ExamDataBaseInfo.categorySubjectTableName);
        mData.addData("name", "모의고사 과목");
        mData.addData("color", -12627531);
        mData.commit(ExamDataBaseInfo.categorySubjectTableName);

        mData.release();
    }
}
