package com.gmail.kludgeworks.upkeep.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gmail.kludgeworks.upkeep.data.LedgerContract.*;

/**
 * Created by Joshua Douty on 12/29/14.
 */
public class LedgerDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "ledger.db";

    public LedgerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_ACCOUNTS_TABLE = "CREATE TABLE " + AccountsEntry.TABLE_NAME + " (" +
                AccountsEntry.COLUMN_ACCT_NM + " TEXT PRIMARY KEY," +
                AccountsEntry.COLUMN_BAL + " REAL NOT NULL, " +
                AccountsEntry.COLUMN_ACCT_TYPE + " TEXT NOT NULL, " +
                AccountsEntry.COLUMN_ACTIVITY_DT + " TEXT NOT NULL, " +
                AccountsEntry.COLUMN_NOTE + " TEXT NOT NULL" +
                " );";

        final String SQL_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE " + TransactionsEntry.TABLE_NAME + " (" +
                // the name of the Account entry associated with this Transaction data
                TransactionsEntry.COLUMN_ACCT_NM + " TEXT NOT NULL, " +
                TransactionsEntry.COLUMN_TRANS_AM + " REAL NOT NULL, " +
                TransactionsEntry.COLUMN_PAYEE + " TEXT NOT NULL, " +
                TransactionsEntry.COLUMN_TRANS_TYPE + " TEXT NOT NULL," +
                TransactionsEntry.COLUMN_CATEGORY + " TEXT NOT NULL, " +
                TransactionsEntry.COLUMN_TRANS_DT + " TEXT NOT NULL, " +
                TransactionsEntry.COLUMN_CLEARED + " INTEGER NOT NULL, " +
                TransactionsEntry.COLUMN_MEMO + " TEXT NOT NULL, " +

                // Set up the Account Name column as a foreign key to Accounts table.
                " FOREIGN KEY (" + TransactionsEntry.COLUMN_ACCT_NM + ") REFERENCES " +
                AccountsEntry.TABLE_NAME + " (" + AccountsEntry.COLUMN_ACCT_NM + ")" +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_ACCOUNTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccountsEntry.TABLE_NAME);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TransactionsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
