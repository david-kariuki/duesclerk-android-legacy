package com.duesclerk.custom.storage_adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.duesclerk.custom.custom_utilities.AccountUtils;
import com.duesclerk.custom.java_beans.JB_ClientAccountInfo;

import java.util.ArrayList;
import java.util.Objects;

public class SQLiteDB extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "db_duesclerk";

    // client Table Name
    private static final String TABLE_CLIENT = "Client";

    private final Context mContext;

    /**
     * Constructor
     *
     * @param mContext - Calling Activity context
     */
    public SQLiteDB(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = mContext;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_CLIENT;
        CREATE_TABLE_CLIENT = "CREATE TABLE " + TABLE_CLIENT
                + "("
                + AccountUtils.KEY_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + AccountUtils.FIELD_CLIENT_ID + " TEXT UNIQUE,"
                + AccountUtils.FIELD_EMAIL_ADDRESS + " TEXT UNIQUE,"
                + AccountUtils.FIELD_PASSWORD + " TEXT" + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE_CLIENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // Drop old table if it exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENT);

        // Recreates Table
        onCreate(sqLiteDatabase);
    }

    /**
     * Function to load data into the client account information Java Bean
     *
     * @param clientAccountInfo - ArrayList with client account information Java Bean
     */
    private JB_ClientAccountInfo loadClientAccountInfoDataJavaBean(ArrayList<JB_ClientAccountInfo> clientAccountInfo) {
        JB_ClientAccountInfo jbClientDetails = new JB_ClientAccountInfo();

        for (int i = 0; i < clientAccountInfo.size(); i++) {
            jbClientDetails = clientAccountInfo.get(i);
        }

        return jbClientDetails;
    }

    /**
     * Function to store client account information
     *
     * @param clientId     - primary key
     * @param emailAddress - unique key
     * @param password     - text
     */
    public boolean storeClientAccountInformation(Context context, String clientId,
                                                 String emailAddress,
                                                 String password) {
        //try {
        boolean recordExist = false;

        // Check if record being inserted already exists
        if (!isEmpty()) {
            // Loop through database array
            for (int i = 0; i < this.getClientAccountInfo().size(); i++) {
                if (this.getClientAccountInfo().get(i).getClientId().equals(clientId)) {
                    // Record exists

                    // Delete record and set exists to false if deleted otherwise true
                    recordExist = !this.deleteClientAccountInfoByClientId(clientId);
                }
            }
        }

        if (!recordExist) {

            // Create SQLiteDatabase object
            SQLiteDatabase storeToDatabase = this.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(AccountUtils.FIELD_CLIENT_ID, clientId); // ClientId
            contentValues.put(AccountUtils.FIELD_EMAIL_ADDRESS, emailAddress); // emailAddress
            contentValues.put(AccountUtils.FIELD_PASSWORD, password); // password

            // Inserting Data
            storeToDatabase.insert(TABLE_CLIENT, null, contentValues);
            storeToDatabase.close(); // Closing Connection to the database

            ArrayList<JB_ClientAccountInfo> clientDetails = this.getClientAccountInfo();

            // Load java bean with client data
            JB_ClientAccountInfo jbClientAccountInfo =
                    this.loadClientAccountInfoDataJavaBean(clientDetails);

            String storedClientId, storedEmailAddress, storedPassword;
            storedClientId = jbClientAccountInfo.getClientId();
            storedEmailAddress = jbClientAccountInfo.getEmailAddress();
            storedPassword = jbClientAccountInfo.getPassword();

            // Check For Change
            return (storedClientId.equalsIgnoreCase(clientId)
                    && storedEmailAddress.equalsIgnoreCase(emailAddress)
                    && storedPassword.equalsIgnoreCase(password));
        }
        //} catch (Exception ignored) {
        //}

        return false;
    }

    /**
     * Function to get client data from database
     */
    public ArrayList<JB_ClientAccountInfo> getClientAccountInfo() {
        try {
            ArrayList<JB_ClientAccountInfo> client = new ArrayList<>();
            JB_ClientAccountInfo jbClient = new JB_ClientAccountInfo();

            String selectQuery = "SELECT * FROM " + TABLE_CLIENT;
            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

            // Loop through cursor
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                jbClient.setClientId(cursor.getString(cursor.getColumnIndex(AccountUtils.FIELD_CLIENT_ID)));
                jbClient.setEmailAddress(cursor.getString(cursor.getColumnIndex(AccountUtils.FIELD_EMAIL_ADDRESS)));
                jbClient.setPassword(cursor.getString(cursor.getColumnIndex(AccountUtils.FIELD_PASSWORD)));
                client.add(jbClient); // Add java bean to ArrayList
            }

            cursor.close(); // Close access to cursor
            sqLiteDatabase.close(); // Closing Connection to the database

            return client; // Return the client details
        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * Function to delete and recreate all client table
     *
     * @param clientId - primary key
     */
    public boolean deleteClientAccountInfoByClientId(String clientId) {
        try {
            // Create SQLiteDatabase object
            SQLiteDatabase database = this.getWritableDatabase();

            database.delete(TABLE_CLIENT, AccountUtils.FIELD_CLIENT_ID + "= ?",
                    new String[]{clientId}); // Delete All Rows
            database.close(); // Closing Connection to the database

            return this.isEmpty();
        } catch (Exception ignored) {
        }

        return false;
    }

    /**
     * Function to update client account information
     *
     * @param clientId    - primary key
     * @param newValue    - new value to be set
     * @param updateField - field in database to be updated
     */
    public boolean updateClientAccountInformation(Context context, String clientId,
                                                  String newValue, String updateField) {
        try {
            String oldValue = null, updatedFieldValue = null;

            if (updateField.equals(AccountUtils.FIELD_EMAIL_ADDRESS)
                    || updateField.equals(AccountUtils.FIELD_PASSWORD)) {

                // Create database object
                SQLiteDatabase updateDatabase = this.getWritableDatabase();
                ContentValues contentValues = new ContentValues();

                // Create class object
                SQLiteDB sqLiteDB = new SQLiteDB(context);

                // Get client account information
                ArrayList<JB_ClientAccountInfo> clientAccountInfo;
                JB_ClientAccountInfo jbClientAccountInfo;
                clientAccountInfo = sqLiteDB.getClientAccountInfo();
                jbClientAccountInfo = this.loadClientAccountInfoDataJavaBean(clientAccountInfo);

                switch (updateField) {
                    case AccountUtils.FIELD_EMAIL_ADDRESS:
                        oldValue = jbClientAccountInfo.getEmailAddress();
                        contentValues.put(AccountUtils.FIELD_EMAIL_ADDRESS, newValue);

                        // update fields
                        updateDatabase.update(TABLE_CLIENT, contentValues,
                                AccountUtils.FIELD_CLIENT_ID + "='" + clientId + "'", null);
                        break;

                    case AccountUtils.FIELD_PASSWORD:
                        oldValue = jbClientAccountInfo.getPassword();
                        contentValues.put(AccountUtils.FIELD_PASSWORD, newValue);

                        // update fields
                        updateDatabase.update(TABLE_CLIENT, contentValues,
                                AccountUtils.FIELD_CLIENT_ID + "='" + clientId + "'", null);
                        break;

                    default:
                        break;
                }

                // Close database connection
                updateDatabase.close();

                // Get updated account info
                clientAccountInfo = sqLiteDB.getClientAccountInfo();
                jbClientAccountInfo = this.loadClientAccountInfoDataJavaBean(clientAccountInfo);

                switch (updateField) {

                    case AccountUtils.FIELD_EMAIL_ADDRESS:
                        updatedFieldValue = jbClientAccountInfo.getEmailAddress();
                        break;
                    case AccountUtils.FIELD_PASSWORD:
                        updatedFieldValue = jbClientAccountInfo.getPassword();
                        break;
                    default:
                        break;
                }
            }
            return (!(Objects.requireNonNull(updatedFieldValue).equals(oldValue)));
        } catch (Exception ignored) {

        }

        return false;
    }

    /**
     * Function to check if database is empty
     */
    public boolean isEmpty() {
        return this.getClientAccountInfo().size() == 0;
    }
}
