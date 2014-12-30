package com.gmail.kludgeworks.upkeep.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by Joshua Douty on 12/29/14.
 */
public class LedgerProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private LedgerDbHelper mOpenHelper;

    private static final int ACCOUNT = 100;
    private static final int WEATHER_WITH_LOCATION = 101;
    private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    private static final int TRANSACTION = 300;
    private static final int LOCATION_ID = 301;

    private static final SQLiteQueryBuilder sAccountQueryBuilder;

    static{
        sAccountQueryBuilder = new SQLiteQueryBuilder();
        sAccountQueryBuilder.setTables(
                LedgerContract.AccountsEntry.TABLE_NAME + " INNER JOIN " +
                        LedgerContract.TransactionsEntry.TABLE_NAME +
                        " ON " + LedgerContract.AccountsEntry.TABLE_NAME +
                        "." + LedgerContract.AccountsEntry.COLUMN_ACCT_NM +
                        " = " + LedgerContract.TransactionsEntry.TABLE_NAME +
                        "." + LedgerContract.TransactionsEntry.COLUMN_ACCT_NM);
    }

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = LedgerContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, LedgerContract.PATH_ACCOUNTS, ACCOUNT);

        matcher.addURI(authority, LedgerContract.PATH_TRANSACTIONS, TRANSACTION);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new LedgerDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "account"
            case ACCOUNT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LedgerContract.AccountsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "transaction"
            case TRANSACTION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LedgerContract.TransactionsEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ACCOUNT:
                return LedgerContract.AccountsEntry.CONTENT_TYPE;
            case TRANSACTION:
                return LedgerContract.TransactionsEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ACCOUNT: {
                long _id = db.insert(LedgerContract.AccountsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = LedgerContract.AccountsEntry.buildAccountsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRANSACTION: {
                long _id = db.insert(LedgerContract.TransactionsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = LedgerContract.TransactionsEntry.buildTransactionsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ACCOUNT:
                rowsDeleted = db.delete(
                        LedgerContract.AccountsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRANSACTION:
                rowsDeleted = db.delete(
                        LedgerContract.TransactionsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ACCOUNT:
                rowsUpdated = db.update(LedgerContract.AccountsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case TRANSACTION:
                rowsUpdated = db.update(LedgerContract.TransactionsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ACCOUNT:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(LedgerContract.AccountsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
