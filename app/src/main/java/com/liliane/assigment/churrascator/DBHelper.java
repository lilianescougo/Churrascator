package com.liliane.assigment.churrascator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private String TAG = "DBHelper";

    //Database info
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database";

    //Tables names
    private static final String TABLE_PEOPLE = "people";
    private static final String TABLE_GROCERIES = "groceries";

    // People Table Columns
    private static final String KEY_PEOPLE_ID = "id";
    private static final String KEY_PEOPLE_NAME = "name";
    private static final String KEY_PEOPLE_QUANTITY = "quantity";
    private static final String KEY_PEOPLE_EATS = "eats";
    private static final String KEY_PEOPLE_DRINKS = "drinks";

    // Groceries Table Columns
    private static final String KEY_GROCERIES_ID = "id";
    private static final String KEY_GROCERIES_NAME = "name";
    private static final String KEY_GROCERIES_IS_UNITARY = "isUnitary";
    private static final String KEY_GROCERIES_VOLUME = "volume";
    private static final String KEY_GROCERIES_PRICE = "price";
    private static final String KEY_GROCERIES_IS_CHECKED = "isChecked";
    private static final String KEY_GROCERIES_SESSION = "session";

    private static DBHelper sInstance;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Insert each person returning its ID
    public void insertPeople(List<People> peopleList) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (People person: peopleList) {
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
                values.put(KEY_PEOPLE_NAME, person.getName());
                values.put(KEY_PEOPLE_QUANTITY, person.getQuantity());
                values.put(KEY_PEOPLE_QUANTITY, person.getQuantity());
                values.put((KEY_PEOPLE_EATS), person.getEats());
                values.put(KEY_PEOPLE_DRINKS, person.getDrinks());
                // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
                db.insertOrThrow(TABLE_PEOPLE, null, values);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.d(TAG, "Error while trying to add groceries to database");
            } finally {
                db.endTransaction();
            }
        }

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PEOPLE_TABLE = "CREATE TABLE " + TABLE_PEOPLE +
                "(" +
                KEY_PEOPLE_ID + " INTEGER PRIMARY KEY, " +
                KEY_PEOPLE_NAME + " TEXT, " +
                KEY_PEOPLE_QUANTITY + " INTEGER, " +
                KEY_PEOPLE_EATS + " REAL, " +
                KEY_PEOPLE_DRINKS + " REAL" +
                ")";

        String CREATE_GROCERIES_TABLE = "CREATE TABLE " + TABLE_GROCERIES +
                "(" +
                KEY_GROCERIES_ID + " INTEGER PRIMARY KEY, " +
                KEY_GROCERIES_NAME + " TEXT, " +
                KEY_GROCERIES_IS_UNITARY + " INTEGER, " +
                KEY_GROCERIES_VOLUME + " REAL, " +
                KEY_GROCERIES_PRICE + " REAL, " +
                KEY_GROCERIES_IS_CHECKED + " INTEGER, " +
                KEY_GROCERIES_SESSION + " INTEGER" +
                ")";

        db.execSQL(CREATE_PEOPLE_TABLE);
        db.execSQL(CREATE_GROCERIES_TABLE);
    }

    public static synchronized DBHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DBHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROCERIES);
            onCreate(db);
        }
    }

    public void addGroceryList (List<GroceryItem> groceryItemList) {
        for (GroceryItem groceryit: groceryItemList) {
            addGrocery(groceryit);
        }
    }

    // Insert people into the database
    public void addGrocery(GroceryItem groceryItem) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_GROCERIES_NAME, groceryItem.getName());
            values.put(KEY_GROCERIES_IS_CHECKED, 0);
            int isUnitary = groceryItem.isUnitary() ? 1 : 0;
            values.put(KEY_GROCERIES_IS_UNITARY, isUnitary);
            if(isUnitary == 1) values.put(KEY_GROCERIES_VOLUME, groceryItem.getVolume());
            values.put(KEY_GROCERIES_PRICE, groceryItem.getPrice());
            values.put(KEY_GROCERIES_SESSION, groceryItem.getSession());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_GROCERIES, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add groceries to database");
        } finally {
            db.endTransaction();
        }
    }

    // Get all posts in the database
    public List<GroceryItem> getAllGroceriesFromSession(int session) {
        List<GroceryItem> groceryItemList = new ArrayList<>();

        // SELECT * FROM groceries
        // WHERE session is session
        String GROCERIES_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s = %d",
                        TABLE_GROCERIES,
                        KEY_GROCERIES_SESSION,
                        session);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(GROCERIES_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(KEY_GROCERIES_NAME));
                    boolean isUnitary = cursor.getInt(cursor.getColumnIndex(KEY_GROCERIES_IS_UNITARY)) == 1;
                    double price = cursor.getDouble(cursor.getColumnIndex(KEY_GROCERIES_PRICE));
                    GroceryItem groceryItem = new GroceryItem(name, isUnitary, price, session);
                    groceryItem.setId(cursor.getInt(cursor.getColumnIndex(KEY_GROCERIES_ID)));
                    if(isUnitary) groceryItem.setVolume(cursor.getDouble(cursor.getColumnIndex(KEY_GROCERIES_VOLUME)));
                    groceryItem.setChecked(cursor.getInt(cursor.getColumnIndex(KEY_GROCERIES_IS_CHECKED)) == 1);
                    groceryItemList.add(groceryItem);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get groceryItemList from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return groceryItemList;
    }

    // Delete all posts and users in the database
    public void deleteAllGroceriesAndPeople() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PEOPLE);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROCERIES);
//            db.delete(TABLE_PEOPLE, null, null);
//            db.delete(TABLE_GROCERIES, null, null);
            onCreate(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete all tables");
        } finally {
            db.endTransaction();
        }
    }

    public List<People> getPeople() {
        List<People> peopleList = new ArrayList<>();

        // SELECT * FROM groceries
        // WHERE session is session
        String PEOPLE_SELECT_QUERY =
                String.format("SELECT * FROM %s", TABLE_PEOPLE);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(PEOPLE_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(KEY_PEOPLE_ID));
                    String name = cursor.getString(cursor.getColumnIndex(KEY_PEOPLE_NAME));
                    double eats = cursor.getDouble(cursor.getColumnIndex(KEY_PEOPLE_EATS));
                    double drinks = cursor.getDouble(cursor.getColumnIndex(KEY_PEOPLE_DRINKS));
                    int quantity = cursor.getInt(cursor.getColumnIndex(KEY_PEOPLE_QUANTITY));
                    People person = new People(id, name, quantity, eats, drinks);
                    peopleList.add(person);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get People from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return peopleList;
    }

    public void updatePeopleQuantity(int id, int qtd) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PEOPLE_QUANTITY, qtd);
        String selection = KEY_PEOPLE_ID + " = ?";
        String[] selectionArgs = { "" + id };
        try {
            db.update(TABLE_PEOPLE, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.i(TAG, "updatePeopleQuantity erro: " + e.getMessage());
        }
    }

    public void updateGroceryItemIsChecked(int id, boolean isChecked) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_GROCERIES_IS_CHECKED, isChecked ? 1 : 0);
        String selection = KEY_GROCERIES_ID + " = ?";
        String[] selectionArgs = { "" + id };
        try {
            db.update(TABLE_GROCERIES, values, selection, selectionArgs);
        } catch (Exception e) {
            Log.i(TAG, "updateGroceryItemIsChecked erro: " + e.getMessage());
        }
    }


}








