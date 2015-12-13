package com.tistory.itmir.whdghks913.reportcard.tool;

import java.io.File;

/**
 * Created by whdghks913 on 2015-12-13.
 */
public class ExamDataBaseInfo {
    /**
     * DB의 기본적인 내용
     */
    public static final String dataBasePath = "/data/data/com.tistory.itmir.whdghks913.reportcard/databases/";
    public static final String dataBaseName = "ExaminationData.db";

    /**
     *
     */
    public static final String categoryExamTableName = "categoryExam";
    public static final String categoryExamTableColumn = "name text, color integer";

    /**
     * 시험 리스트를 저장하는 테이블
     */
    public static final String examListTableName = "examNameList";
    public static final String examListTableColumn = "name text, category integer, year integer, month integer, day integer, color integer";

    /**
     * 시험 정보를 저장하는 세부 테이블
     * 테이블 이름은 시험 리스트에 저장된 _id값
     * name : 과목 이름
     * score : 받은 점수
     * rank : 전교 석차
     * applicants : 응시자 수
     * class : 등급
     */
    public static final String examDetailedColumn = "name text, score integer, rank integer, applicants integer, class integer";

    public static boolean isDatabaseExists() {
        return new File(dataBasePath + dataBaseName).exists();
    }
}
