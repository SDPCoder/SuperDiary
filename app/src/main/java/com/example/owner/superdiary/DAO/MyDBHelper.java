package com.example.owner.superdiary.DAO;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class MyDBHelper extends SQLiteOpenHelper {
    private String TABLE_NAME;
    private Map<String, String>  table_head;
    private int columnNum;
    private String[] colNameArray;

    public MyDBHelper(Context context, String name, String table_name, Map<String, String> table_head, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.TABLE_NAME = table_name;
        this.table_head = table_head;
        this.columnNum = table_head.size();

        table_head.put("year", "INTEGER");
        table_head.put("month", "INTEGER");
        table_head.put("day", "INTEGER");
        table_head.put("time", "TEXT");
        this.columnNum += 4;

        Object [] table_head_keyset = table_head.keySet().toArray();
        colNameArray = new String[columnNum];
        for (int i = 0; i < columnNum; i++) {
            colNameArray[i] = (String) table_head_keyset[i];
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

        String CREATE_TABLE = "CREATE TABLE if not exists " + TABLE_NAME
                + " (";
        int i = 0;
        for (String key : table_head.keySet()) {
            String name = key;
            String type = table_head.get(name);
            CREATE_TABLE += name + " " + type;

            if (i < table_head.size() - 1) {
                CREATE_TABLE += ", ";
            }
            i++;
        }
        CREATE_TABLE += ")";

        Log.i("MyDBHelper", "Initializing table with SQL " + String.valueOf(CREATE_TABLE));
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insert(Map<String, Object> record) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < columnNum; i++) {
            String colName = colNameArray[i];
            String type = table_head.get(colName);
            Log.i("insert", colName + " " + type);
            if (type.equals("TEXT")) {
//                Log.i("insertValue", (String)record.get(colName));
                cv.put(colName, (String)record.get(colName));
            } else if (type.equals("INTEGER")) {
//                Log.i("insertValue", String.valueOf((int)record.get(colName)));
                cv.put(colName, (int)record.get(colName));
            } else if (type.equals("REAL")) {
//                Log.i("insertValue", String.valueOf((double)record.get(colName)));
                cv.put(colName, (double)record.get(colName));
            }
        }
        db.insert(TABLE_NAME, null, cv);
        db.close();
        return true;
    }

    public boolean insertNow(Map<String, Object> record) {
        Calendar date = Calendar.getInstance();
        int day = date.get(Calendar.DAY_OF_MONTH);
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        record.put("month", month);
        record.put("year", year);
        record.put("day", day);
        record.put("time", String.valueOf(System.currentTimeMillis()));
        return insert(record);
    }

    public int deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, null, null);
    }

    public List queryAll() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor results = db.query(TABLE_NAME, colNameArray, null, null, null, null, null);
        return converToEntry(results);
    }

    public List query(Calendar date) {
        SQLiteDatabase db = getWritableDatabase();
        int day, year, month;
        day = date.get(Calendar.DAY_OF_MONTH);
        year = date.get(Calendar.YEAR);
        month = date.get(Calendar.MONTH) + 1;
        Cursor cursor = db.query(TABLE_NAME, colNameArray, "year=? and month=? and day=?",
                new String[]{String.valueOf(year), String.valueOf(month), String.valueOf(day)}, null, null, null);
        return converToEntry(cursor);
    }

    public List queryToday() {
        return query(Calendar.getInstance());
    }

    private List converToEntry(Cursor cursor) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> record = new HashMap<>();
                for (int i = 0; i < columnNum; i++) {
                    String colName = colNameArray[i];
                    switch (table_head.get(colName)) {
                        case "TEXT":
                            record.put(colName, cursor.getString(cursor.getColumnIndex(colName)));
                            break;
                        case "INTEGER":
                            record.put(colName, cursor.getInt(cursor.getColumnIndex(colName)));
                            break;
                        case "REAL":
                            record.put(colName, cursor.getDouble(cursor.getColumnIndex(colName)));
                            break;
                    }
                }

                list.add(record);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public int getSize() {
        return (int) DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE_NAME);
    }
}