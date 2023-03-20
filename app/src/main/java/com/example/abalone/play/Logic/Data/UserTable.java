package com.example.abalone.play.Logic.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class UserTable extends SQLiteOpenHelper {

    public static final String DATABASENAME = "user.db";
    public static final String TABLE_PRODUCT = "User";
    public static final int DATABASEVERSION = 1;

    public static final String COLUMN_ID = "userID";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_SURNAME = "Surname";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_IMAGE = "Image";
    public static final String COLUMN_WINS = "Wins";

    SQLiteDatabase database;

    private final String CREATE_TABLE_PRODUCT = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " VARCHAR," + COLUMN_SURNAME + " VARCHAR," + COLUMN_EMAIL + " VARCHAR," + COLUMN_IMAGE + " BLOB," + COLUMN_WINS + " INTEGER" + ");";

    String[] allColumns = {UserTable.COLUMN_ID, UserTable.COLUMN_NAME, UserTable.COLUMN_SURNAME, UserTable.COLUMN_EMAIL, UserTable.COLUMN_IMAGE, UserTable.COLUMN_WINS};

    public UserTable(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCT);
        Log.i("data1", "Table customer created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(db);
    }

    public void open() {
        database = this.getWritableDatabase();
        Log.i("data", "Database connection open");
    }

    public ArrayList<User> getAllStudents() {
        this.open();
        ArrayList<User> l = new ArrayList<>();
        Cursor cursor = database.query(UserTable.TABLE_PRODUCT, allColumns, null, null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(cursor.getColumnIndex(UserTable.COLUMN_ID));
                String fname = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_NAME));
                String lname = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_SURNAME));
                String address = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_EMAIL));
                byte[] byteArray = cursor.getBlob(cursor.getColumnIndex(UserTable.COLUMN_IMAGE));
                Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                int wins = cursor.getInt(cursor.getColumnIndex(UserTable.COLUMN_WINS));
                User s = new User(fname, lname, address, bitmap, wins, id);
                l.add(s);
            }
        }
        this.close();
        return l;
    }

    public long deleteUserByID(long rowID) {
        this.open();
        long fina = database.delete(UserTable.TABLE_PRODUCT, UserTable.COLUMN_ID + "=" + rowID, null);
        this.close();
        return fina;
    }

    public long updateByRow(User s) {
        if (s != null) {
            this.open();
            ContentValues values = new ContentValues();
            values.put(UserTable.COLUMN_ID, s.getId());
            values.put(UserTable.COLUMN_NAME, s.getName());
            values.put(UserTable.COLUMN_SURNAME, s.getSurname());
            values.put(UserTable.COLUMN_EMAIL, s.getEmail());
            values.put(UserTable.COLUMN_WINS, s.getWins());
            Bitmap temp = s.getImg();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            temp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            values.put(UserTable.COLUMN_IMAGE, byteArray);

            return database.update(UserTable.TABLE_PRODUCT, values, UserTable.COLUMN_ID + "=" + s.getId(), null);
        }
        return -1;
    }

    public User getStudentByID(long rowID) {
        this.open();
        Cursor cursor = database.query(UserTable.TABLE_PRODUCT, allColumns, UserTable.COLUMN_ID + "=" + rowID, null, null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            long id = cursor.getLong(cursor.getColumnIndex(UserTable.COLUMN_ID));
            String fname = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_NAME));
            String lname = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_SURNAME));
            String address = cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_EMAIL));
            byte[] byteArray = cursor.getBlob(cursor.getColumnIndex(UserTable.COLUMN_IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            int wins = cursor.getInt(cursor.getColumnIndex(UserTable.COLUMN_WINS));
            User newUser = new User(fname, lname, address, bitmap, wins, id);
            this.close();
            return newUser;
        }
        return null;
    }

    public User createUser(User user) {
        ContentValues values = new ContentValues();
        values.put(UserTable.COLUMN_NAME, user.getName());
        values.put(UserTable.COLUMN_SURNAME, user.getSurname());
        values.put(UserTable.COLUMN_EMAIL, user.getEmail());
        Bitmap temp = user.getImg();
        Bitmap copy = Bitmap.createBitmap(temp.getWidth(), temp.getHeight(), temp.getConfig());
        Canvas canvas = new Canvas(copy);
        canvas.drawBitmap(temp, 0, 0, null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        copy.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteArray = stream.toByteArray();
        System.out.println("Bitmap (UserTable Temp): " + temp.getHeight() + ", " + temp.getWidth());
        System.out.println("Bitmap (UserTable Copy): " + copy.getHeight() + ", " + copy.getWidth());
        values.put(UserTable.COLUMN_IMAGE, byteArray);
        System.out.println("database: " + database);
        this.open();
        long insertid = database.insert(UserTable.TABLE_PRODUCT, null, values);
        Log.i("data", "User " + insertid + " insert to database");
        this.close();
        return user;
    }
}