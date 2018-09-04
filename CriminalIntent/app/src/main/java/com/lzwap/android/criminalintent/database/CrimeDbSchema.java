package com.lzwap.android.criminalintent.database;

public class CrimeDbSchema {

    //该内部类的唯一用途就是定义描述数据表元素
    public static final class CrimeTable {
        //定义数据库表名
        public static final String NAME = "crimes";

        //定义数据表字段
        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SUSPECT = "suspect";
            public static final String PHONE = "phone";
        }
    }



}
