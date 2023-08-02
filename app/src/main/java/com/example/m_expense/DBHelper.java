package com.example.m_expense;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static final String USER_TABLE_NAME = "user_tbl";

    public static final String USER_ID_COLUMN = "person_id";
    public static final String USER_NAME_COLUMN = "name";
    public static final String USER_EMAIL_COLUMN = "email";
    public static final String USER_PASSWORD_COLUMN = "password";

    private static final String TRIP_TABLE_NAME = "trip_tbl";

    public static final String TRIP_ID_COLUMN = "trip_id";
    public static final String TRIP_NAME_COLUMN = "trip_name";
    public static final String TRIP_DESTINATION_COLUMN = "destination";
    public static final String TRIP_DATE_COLUMN = "date";
    public static final String TRIP_RISK_COLUMN = "require_risk_assessment";
    public static final String TRIP_DESCRIPTION_COLUMN = "description";
    public static final String TRIP_FLAG_COLUMN = "flag";
    public static final String TRIP_BUDGET_COLUMN = "budget";

    private static final String EXPENSE_TABLE_NAME = "expense_tbl";

    public static final String EXPENSE_ID_COLUMN = "expense_id";
    public static final String EXPENSE_TYPE_COLUMN = "expense_type";
    public static final String EXPENSE_AMOUNT_COLUMN = "expense_amount";
    public static final String EXPENSE_TIME_COLUMN = "expense_time";
    public static final String EXPENSE_COMMENTS_COLUMN = "additional_comments";
    public static final String EXPENSE_ICON_COLUMN = "expense_icon";

    private final SQLiteDatabase database;

    private static final String USER_TABLE_CREATE = String.format(
            "CREATE TABLE %s(" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT) ",
            USER_TABLE_NAME, USER_ID_COLUMN, USER_NAME_COLUMN, USER_EMAIL_COLUMN, USER_PASSWORD_COLUMN);

    private static final String TRIP_TABLE_CREATE = String.format(
            "CREATE TABLE %s(" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s INTEGER NOT NULL, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s INTEGER, " +
                    "%s INTEGER, " +
                    "%s INTEGER, " +
                    "%s TEXT, " +
                    "FOREIGN KEY(%s) REFERENCES %s (%s) ON DELETE CASCADE) ",
            TRIP_TABLE_NAME, TRIP_ID_COLUMN, USER_ID_COLUMN, TRIP_NAME_COLUMN, TRIP_DESTINATION_COLUMN, TRIP_DATE_COLUMN,
            TRIP_RISK_COLUMN, TRIP_BUDGET_COLUMN, TRIP_FLAG_COLUMN, TRIP_DESCRIPTION_COLUMN, USER_ID_COLUMN, USER_TABLE_NAME, USER_ID_COLUMN);

    private static final String EXPENSE_TABLE_CREATE = String.format(
            "CREATE TABLE %s(" +
                    "%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "%s INTEGER NOT NULL, " +
                    "%s INTEGER NOT NULL, " +
                    "%s TEXT, " +
                    "%s INTEGER, " +
                    "%s TEXT, " +
                    "%s TEXT, " +
                    "%s INTEGER, " +
                    "FOREIGN KEY(%s) REFERENCES %s (%s) ON DELETE CASCADE, " +
                    "FOREIGN KEY(%s) REFERENCES %s (%s)) ",
            EXPENSE_TABLE_NAME, EXPENSE_ID_COLUMN, TRIP_ID_COLUMN, USER_ID_COLUMN, EXPENSE_TYPE_COLUMN, EXPENSE_AMOUNT_COLUMN,
            EXPENSE_TIME_COLUMN, EXPENSE_COMMENTS_COLUMN, EXPENSE_ICON_COLUMN, TRIP_ID_COLUMN, TRIP_TABLE_NAME, TRIP_ID_COLUMN, USER_ID_COLUMN, USER_TABLE_NAME, USER_ID_COLUMN);

    //Referential integrity is set to cascade - If a Trip is deleted, the expenses are deleted too, If a User is
    //deleted, related Trips and Expenses also get deleted.
    public DBHelper(Context context) {
        super(context, USER_TABLE_NAME, null, 12);
        database = getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USER_TABLE_CREATE);
        db.execSQL(TRIP_TABLE_CREATE);
        db.execSQL(EXPENSE_TABLE_CREATE);
    }

    //enable foreign keys
    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EXPENSE_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TRIP_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);


        Log.v(this.getClass().getName(), "The following tables have been dropped: "
                + USER_TABLE_NAME
                + TRIP_TABLE_NAME
                + EXPENSE_TABLE_NAME
                + " Database version" + oldVersion + " has been upgraded to " + newVersion);
        onCreate(db);
    }

    //get password associated with an email address
    public String getPasswordByEmail(String email) {
        Cursor cursorRes = database.query(USER_TABLE_NAME, new String[]{
                        USER_EMAIL_COLUMN, USER_PASSWORD_COLUMN},
                USER_EMAIL_COLUMN + " = ?", new String[]{email}, null, null, null);
        cursorRes.moveToNext();
        String res = cursorRes.getString(1);
        cursorRes.close();
        return res;
    }

    //Get a row of Trip data where the search input matches using LIKE
    public Cursor getTripNameSearchQuery(String search, String filter, int user_id) {

        String searchPlaceHolder = "";

        if (filter.equals("Trip Name")) {
            searchPlaceHolder = TRIP_NAME_COLUMN;
        }

        if (filter.equals("Destination")) {
            searchPlaceHolder = TRIP_DESTINATION_COLUMN;
        }

        if (filter.equals("Date")) {
            searchPlaceHolder = TRIP_DATE_COLUMN;
        }

        return database.query(true, TRIP_TABLE_NAME, new String[]{TRIP_ID_COLUMN, TRIP_NAME_COLUMN,
                        TRIP_DESTINATION_COLUMN, TRIP_DATE_COLUMN, TRIP_RISK_COLUMN, TRIP_DESCRIPTION_COLUMN, TRIP_FLAG_COLUMN, TRIP_BUDGET_COLUMN, USER_ID_COLUMN},
                searchPlaceHolder + " LIKE ?" + " AND " + USER_ID_COLUMN + "= ?",
                new String[]{"%" + search + "%", String.valueOf(user_id)}, null, null, null,
                null);
    }


    //Get the User Id associated with an Email address
    public int getIdByEmail(String email) {
        Cursor cursorRes = database.query(USER_TABLE_NAME, new String[]{USER_ID_COLUMN,
                        USER_EMAIL_COLUMN},
                USER_EMAIL_COLUMN + " = ?", new String[]{email}, null, null, null);
        cursorRes.moveToNext();
        int res = cursorRes.getInt(0);
        cursorRes.close();
        return res;
    }

    //Validation - Checking if an email exists already
    public boolean checkIfEmailExists(String email) {
        String res = null;
        try {
            Cursor cursorRes = database.query(USER_TABLE_NAME, new String[]{USER_ID_COLUMN,
                            USER_EMAIL_COLUMN},
                    USER_EMAIL_COLUMN + " = ?", new String[]{email}, null, null, null);
            cursorRes.moveToNext();
            res = cursorRes.getString(1);
            cursorRes.close();
        } catch (CursorIndexOutOfBoundsException ignored) {

        }
        return email.equals(res);
    }


    //Preload Cursor with all trips/expenses using ID
    public Cursor getAllTripsById(int id) {
        return database.query(TRIP_TABLE_NAME, new String[]{TRIP_ID_COLUMN, TRIP_NAME_COLUMN,
                        TRIP_DESTINATION_COLUMN, TRIP_DATE_COLUMN, TRIP_RISK_COLUMN, TRIP_DESCRIPTION_COLUMN, TRIP_FLAG_COLUMN, TRIP_BUDGET_COLUMN},
                USER_ID_COLUMN + "= ?", new String[]{String.valueOf(id)}, null, null, TRIP_ID_COLUMN);
    }


    //ADD ICON COLUMN HERE
    public Cursor getAllExpensesById(int id) {
        return database.query(EXPENSE_TABLE_NAME, new String[]{EXPENSE_ID_COLUMN, EXPENSE_COMMENTS_COLUMN, EXPENSE_ICON_COLUMN, EXPENSE_TIME_COLUMN,
                        EXPENSE_AMOUNT_COLUMN, EXPENSE_TYPE_COLUMN},
                TRIP_ID_COLUMN + "= ?", new String[]{String.valueOf(id)}, null, null, TRIP_ID_COLUMN);
    }


    //Count queries
    public long countExpensesByTripId(long tripId) {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, EXPENSE_TABLE_NAME,
                TRIP_ID_COLUMN + " = ?", new String[]{String.valueOf(tripId)});
    }

    public long countExpensesByUserId(long tripId) {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, EXPENSE_TABLE_NAME,
                USER_ID_COLUMN + " = ?", new String[]{String.valueOf(tripId)});
    }

    public long countTripsByUserId(int userId) {
        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TRIP_TABLE_NAME,
                USER_ID_COLUMN + " = ?", new String[]{String.valueOf(userId)});
    }


    //Insert queries
    public void insertTrip(Trip t) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(TRIP_NAME_COLUMN, t.getName());
        rowValues.put(TRIP_DESTINATION_COLUMN, t.getDestination());
        rowValues.put(TRIP_DATE_COLUMN, t.getDate());
        rowValues.put(TRIP_RISK_COLUMN, t.isRequire_risk());
        rowValues.put(TRIP_DESCRIPTION_COLUMN, t.getDescription());
        rowValues.put(USER_ID_COLUMN, t.getPersonId());
        rowValues.put(TRIP_FLAG_COLUMN, t.getFlag());
        rowValues.put(TRIP_BUDGET_COLUMN, t.getBudget());


        database.insertOrThrow(TRIP_TABLE_NAME, null, rowValues);
    }


    public void insertExpense(Expense e, int userId) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(EXPENSE_TYPE_COLUMN, e.getExpense_type());
        rowValues.put(EXPENSE_AMOUNT_COLUMN, e.getExpense_amount());
        rowValues.put(EXPENSE_TIME_COLUMN, e.getExpense_time());
        rowValues.put(EXPENSE_COMMENTS_COLUMN, e.getComments());
        rowValues.put(EXPENSE_ICON_COLUMN, e.getExpense_icon());
        rowValues.put(TRIP_ID_COLUMN, e.getId());
        rowValues.put(USER_ID_COLUMN, userId);


        database.insertOrThrow(EXPENSE_TABLE_NAME, null, rowValues);
    }

    public void insertUser(Person p) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(USER_NAME_COLUMN, p.getName());
        rowValues.put(USER_EMAIL_COLUMN, p.getEmail());
        rowValues.put(USER_PASSWORD_COLUMN, p.getPassword());


        database.insertOrThrow(USER_TABLE_NAME, null, rowValues);
    }


    //Delete queries
    public void deleteTripById(long id) {
        database.delete(TRIP_TABLE_NAME, TRIP_ID_COLUMN + "=" + id, null);
    }

    public void deleteExpenseById(long id) {
        database.delete(EXPENSE_TABLE_NAME, EXPENSE_ID_COLUMN + "=" + id, null);
    }

    public void deleteUserById(long id) {
        database.delete(USER_TABLE_NAME, USER_ID_COLUMN + "=" + id, null);
    }


    //Update queries
    public void updateTripDetails(Trip t, int trip_id) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(TRIP_NAME_COLUMN, t.getName());
        rowValues.put(TRIP_DESTINATION_COLUMN, t.getDestination());
        rowValues.put(TRIP_DATE_COLUMN, t.getDate());
        rowValues.put(TRIP_RISK_COLUMN, t.isRequire_risk());
        rowValues.put(TRIP_DESCRIPTION_COLUMN, t.getDescription());
        rowValues.put(TRIP_FLAG_COLUMN, t.getFlag());
        rowValues.put(TRIP_BUDGET_COLUMN, t.getBudget());

        database.update(TRIP_TABLE_NAME, rowValues, TRIP_ID_COLUMN + " =?", new String[]{String.valueOf(trip_id)});
    }


    public void updateExpenseDetails(Expense e, long ex_id) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(EXPENSE_TIME_COLUMN, e.getExpense_time());
        rowValues.put(EXPENSE_TYPE_COLUMN, e.getExpense_type());
        rowValues.put(EXPENSE_AMOUNT_COLUMN, e.getExpense_amount());
        rowValues.put(EXPENSE_COMMENTS_COLUMN, e.getComments());
        rowValues.put(EXPENSE_ICON_COLUMN, e.getExpense_icon());

        database.update(EXPENSE_TABLE_NAME, rowValues, EXPENSE_ID_COLUMN + " =?", new String[]{String.valueOf(ex_id)});
    }

    public void updateUserDetails(Person p, int userId) {
        ContentValues rowValues = new ContentValues();

        rowValues.put(USER_NAME_COLUMN, p.getName());
        rowValues.put(USER_EMAIL_COLUMN, p.getEmail());
        rowValues.put(USER_PASSWORD_COLUMN, p.getPassword());

        database.update(USER_TABLE_NAME, rowValues, USER_ID_COLUMN + " =?", new String[]{String.valueOf(userId)});
    }


    //Get sum of all expenses added by user
    public int sumOfExpensesByUserId(int userId) {

        Cursor cursor = database.rawQuery("SELECT SUM(" + DBHelper.EXPENSE_AMOUNT_COLUMN + " ) " +
                "as Total FROM " + DBHelper.EXPENSE_TABLE_NAME + " WHERE " + USER_ID_COLUMN + "=?", new String[]{String.valueOf(userId)});
        cursor.moveToPosition(0);
        @SuppressLint("Range") int total = cursor.getInt(cursor.getColumnIndex("Total"));
        cursor.close();
        return total;

    }


    //Get all data associated with one ID
    public Cursor getOneUserById(int userId) {
        return database.query(USER_TABLE_NAME, new String[]{USER_NAME_COLUMN,
                        USER_EMAIL_COLUMN, USER_PASSWORD_COLUMN},
                USER_ID_COLUMN + "= ?", new String[]{String.valueOf(userId)}, null, null, USER_ID_COLUMN);
    }

    public Cursor getOneTripById(int id) {
        return database.query(TRIP_TABLE_NAME, new String[]{TRIP_ID_COLUMN, TRIP_NAME_COLUMN,
                        TRIP_DESTINATION_COLUMN, TRIP_DATE_COLUMN, TRIP_RISK_COLUMN, TRIP_DESCRIPTION_COLUMN, TRIP_FLAG_COLUMN, TRIP_BUDGET_COLUMN},
                TRIP_ID_COLUMN + "= ?", new String[]{String.valueOf(id)}, null, null, TRIP_ID_COLUMN);
    }


    public Cursor getOneExpenseById(long id) {
        return database.query(EXPENSE_TABLE_NAME, new String[]{EXPENSE_ID_COLUMN, EXPENSE_TYPE_COLUMN,
                        EXPENSE_AMOUNT_COLUMN, EXPENSE_TIME_COLUMN, EXPENSE_COMMENTS_COLUMN, EXPENSE_ICON_COLUMN},
                EXPENSE_ID_COLUMN + "= ?", new String[]{String.valueOf(id)}, null, null, null);
    }

    public int getExpenseCountByTripId(long tripId) {
        Cursor cursor = database.rawQuery("SELECT SUM(" + DBHelper.EXPENSE_AMOUNT_COLUMN + " ) " +
                "as Total FROM " + DBHelper.EXPENSE_TABLE_NAME + " WHERE " + TRIP_ID_COLUMN + "=?", new String[]{String.valueOf(tripId)});
        cursor.moveToPosition(0);
        @SuppressLint("Range") int total = cursor.getInt(cursor.getColumnIndex("Total"));
        cursor.close();
        return total;

    }

    public void updateBudget(long trip_id, int remainingBudget) {
        ContentValues rowValues = new ContentValues();


        rowValues.put(TRIP_BUDGET_COLUMN, remainingBudget);

        database.update(TRIP_TABLE_NAME, rowValues, TRIP_ID_COLUMN + " =?", new String[]{String.valueOf(trip_id)});

    }

    public int getBudgetByTripId(int trip_id){
        Cursor cursorRes = database.query(TRIP_TABLE_NAME, new String[]{TRIP_BUDGET_COLUMN},
                TRIP_ID_COLUMN + " = ?", new String[]{String.valueOf(trip_id)}, null, null, null);
        cursorRes.moveToNext();
        int res = cursorRes.getInt(0);
        cursorRes.close();
        return res;
    }
}
