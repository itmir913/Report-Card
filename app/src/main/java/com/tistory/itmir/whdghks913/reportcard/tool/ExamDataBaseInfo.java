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
     * 시험 리스트를 저장하는 테이블
     */
    public static final String examListTableName = "examNameList";
    public static final String examListTableColumn = "name text, year integer, month integer, day integer, red integer, green integer, blue integer";

    /**
     * 시험 정보를 저장하는 세부 테이블
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
