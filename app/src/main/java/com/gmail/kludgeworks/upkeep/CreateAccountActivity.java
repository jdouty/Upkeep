package com.gmail.kludgeworks.upkeep;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.kludgeworks.upkeep.data.LedgerContract.AccountsEntry;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Joshua Douty on 1/24/15.
 */
public class CreateAccountActivity  extends ActionBarActivity {

    public static final String DATE_KEY = "forecast_date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_account, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.save_account) {
            saveAccount();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method to handle insertion of a new account in the accounts database.
     *
     * @return the row ID of the added account.
     */
    private long saveAccount() {
        final TextView nameView = (TextView) findViewById(R.id.create_account_name_edittext);
        final TextView balanceView = (TextView) findViewById(R.id.create_account_balance_edittext);
        final Spinner typeView = (Spinner) findViewById(R.id.create_account_type_spinner);
        final TextView noteView = (TextView) findViewById(R.id.create_account_notes_edittext);

        String accountName = nameView.getText().toString();
        String balance = balanceView.getText().toString();
        String accountType = typeView.getSelectedItem().toString();
        String note = noteView.getText().toString();

        // pick current system date
        Date dt = new Date();
        // set format for date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        // parse it like
        String date = dateFormat.format(dt);

        // First, check if the account with this account name exists in the db
        Cursor cursor = getContentResolver().query(
                AccountsEntry.CONTENT_URI,
                new String[]{AccountsEntry.COLUMN_ACCT_NM},
                AccountsEntry.COLUMN_ACCT_NM + " = ?",
                new String[]{accountName},
                null);

        if (cursor.moveToFirst()) {
            int locationIdIndex = cursor.getColumnIndex(AccountsEntry.COLUMN_ACCT_NM);
            Toast.makeText(this, "Account with this name already exists.", Toast.LENGTH_SHORT).show();
            return cursor.getLong(locationIdIndex);
        } else {
            ContentValues accountValues = new ContentValues();
            accountValues.put(AccountsEntry.COLUMN_ACCT_NM, accountName);
            accountValues.put(AccountsEntry.COLUMN_BAL, balance);
            accountValues.put(AccountsEntry.COLUMN_ACCT_TYPE, accountType);
            accountValues.put(AccountsEntry.COLUMN_ACTIVITY_DT, date);
            accountValues.put(AccountsEntry.COLUMN_NOTE, note);

            Uri locationInsertUri = getContentResolver()
                    .insert(AccountsEntry.CONTENT_URI, accountValues);

            finish();
            return ContentUris.parseId(locationInsertUri);
        }
    }
}
