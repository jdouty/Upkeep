package com.gmail.kludgeworks.upkeep.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Joshua Douty on 12/17/14.
 */
public class LedgerContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.gmail.KludgeWorks.upkeep.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_ACCOUNTS = "accounts";
    public static final String PATH_TRANSACTIONS = "transactions";

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }

    /* Inner class that defines the table contents of the Accounts table */
    public static final class AccountsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ACCOUNTS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNTS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_ACCOUNTS;

        // Table name
        public static final String TABLE_NAME = "accounts";

        // Account Name.
        public static final String COLUMN_ACCT_NM = "acct_nm";
        // Current Account Balance.
        public static final String COLUMN_BAL = "bal";
        // Account Type. (Checking, Savings, Credit, ...)
        public static final String COLUMN_ACCT_TYPE = "acct_type";
        // Activity Date. This is the date for the most recent transaction.
        public static final String COLUMN_ACTIVITY_DT = "activity_dt";
        // Note. Some information about the account.
        public static final String COLUMN_NOTE = "note";

        public static Uri buildAccountsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the table contents of the transaction table */
    public static final class TransactionsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRANSACTIONS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTIONS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_TRANSACTIONS;

        public static final String TABLE_NAME = "transactions";

        // Account Name.
        public static final String COLUMN_ACCT_NM = "acct_nm";
        // Transaction amount.
        public static final String COLUMN_TRANS_AM = "trans_am";
        // Payee. Who's the money going to?
        public static final String COLUMN_PAYEE = "payee";
        // Transaction type. Changes based on account type.
        public static final String COLUMN_TRANS_TYPE = "trans_type";
        // Transaction category. Used for future budgeting feature.
        public static final String COLUMN_CATEGORY = "category";
        // Transaction date.
        public static final String COLUMN_TRANS_DT = "trans_dt";
        // Cleared. 0 FALSE 1 TRUE
        public static final String COLUMN_CLEARED = "cleared";
        // Memo. Details about the transaction
        public static final String COLUMN_MEMO = "memo";


        public static Uri buildTransactionsUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
