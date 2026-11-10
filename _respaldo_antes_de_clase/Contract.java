package com.example.myapplication;

public class Contract {
}
public static class FeedEntry implements BaseColums{
    public static final String nameTable = "persona";
    public static final String column1 = "nombre";

    public static final String column2 = "apellido";

}

public static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE" + FeedEntry.nameTable + " "