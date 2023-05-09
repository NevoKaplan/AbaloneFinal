// Package and imports
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

// UserTable class definition
public class UserTable extends SQLiteOpenHelper {

    // Database information
    public static final String DATABASENAME = "user.db";
    public static final String TABLE_PRODUCT = "User";
    public static final int DATABASEVERSION = 1;

    // Columns of the table
    public static final String COLUMN_ID = "userID";
    public static final String COLUMN_NAME = "Name";
    public static final String COLUMN_SURNAME = "Surname";
    public static final String COLUMN_EMAIL = "Email";
    public static final String COLUMN_IMAGE = "Image";
    public static final String COLUMN_WINS = "Wins";

    // Database object
    SQLiteDatabase database;

    // SQL command to create the table
    private final String CREATE_TABLE_PRODUCT = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " VARCHAR," + COLUMN_SURNAME + " VARCHAR," + COLUMN_EMAIL + " VARCHAR," + COLUMN_IMAGE + " BLOB," + COLUMN_WINS + " INTEGER" + ");";

    // Array with all the table columns
    String[] allColumns = {UserTable.COLUMN_ID, UserTable.COLUMN_NAME, UserTable.COLUMN_SURNAME, UserTable.COLUMN_EMAIL, UserTable.COLUMN_IMAGE, UserTable.COLUMN_WINS};

    // Constructor
    public UserTable(Context context) {
        super(context, DATABASENAME, null, DATABASEVERSION);
    }

    // Method to create the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCT);
        Log.i("data1", "Table customer created");
    }

    // Method to upgrade the table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        onCreate(db);
    }

    // Method to open the database connection
    public void open() {
        database = this.getWritableDatabase();
        Log.i("data", "Database connection open");
    }

    // Method to get all students from the database
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

    // Method to delete a student by ID
    public long deleteUserByID(long rowID) {
        this.open();
        long fina = database.delete(UserTable.TABLE_PRODUCT, UserTable.COLUMN_ID + "=" + rowID, null);
        this.close();
        return fina;
    }

    // This method updates a row in the user table based on the user object passed as parameter.
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
            // Update the row in the user table and return the number of rows affected.
            return database.update(UserTable.TABLE_PRODUCT, values, UserTable.COLUMN_ID + "=" + s.getId(), null);
        }
        // If the user object passed as parameter is null, return -1 to indicate an error.
        return -1;
    }

    // This method retrieves a user object from the user table based on the rowID passed as parameter.
    public User getStudentByID(long rowID) {
        this.open();
        // Query the user table based on the rowID and get the cursor.
        Cursor cursor = database.query(UserTable.TABLE_PRODUCT, allColumns, UserTable.COLUMN_ID + "=" + rowID, null, null, null, null);
        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            // Get the values from the cursor and create a new user object.
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
        // If the cursor is empty, return null to indicate that no user was found.
        return null;
    }

    // This method creates a new user in the user table based on the user object passed as parameter.
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
        values.put(UserTable.COLUMN_IMAGE, byteArray);
        this.open();
        // Insert the new user into the user table and get the insert ID.
        long insertid = database.insert(UserTable.TABLE_PRODUCT, null, values);
        // Log the insert operation.
        Log.i("data", "User " + insertid + " insert to database");
        this.close();
        //
        return user;
    }
}