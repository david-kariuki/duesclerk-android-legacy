package com.duesclerk.custom.storage_adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.duesclerk.custom.custom_utilities.DataUtils;
import com.duesclerk.custom.custom_utilities.UserAccountUtils;
import com.duesclerk.custom.java_beans.JB_UserAccountInfo;

import java.util.ArrayList;
import java.util.Objects;

public class UserDatabase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "db_duesclerk";

    // user Table Name
    private static final String TABLE_USER = "User";

    /**
     * Constructor
     *
     * @param context - Calling Activity context
     */
    public UserDatabase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_TABLE_USER;
        CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER
                + "("
                + UserAccountUtils.KEY_RECORD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + UserAccountUtils.FIELD_USER_ID + " TEXT UNIQUE,"
                + UserAccountUtils.FIELD_EMAIL_ADDRESS + " TEXT UNIQUE,"
                + UserAccountUtils.FIELD_PASSWORD + " TEXT,"
                + UserAccountUtils.FIELD_ACCOUNT_TYPE + " TEXT"
                + ")";
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // Drop old table if it exists
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);

        onCreate(sqLiteDatabase); // Recreates Table
    }

    /**
     * Function to load data into the user account information Java Bean
     *
     * @param userAccountInfo - ArrayList with user account information Java Bean
     *
     * @return JB_UserAccountInfo - User details java bean
     */
    private JB_UserAccountInfo loadUserAccountInfoDataJavaBean(
            ArrayList<JB_UserAccountInfo> userAccountInfo) {

        JB_UserAccountInfo jbUserDetails = new JB_UserAccountInfo();

        // Loop through user details array
        for (int i = 0; i < userAccountInfo.size(); i++) {

            jbUserDetails = userAccountInfo.get(i); // Load user details java bean
        }

        return jbUserDetails; // Return user details java bean
    }

    /**
     * Function to store user account information
     *
     * @param userId     - primary key
     * @param emailAddress - unique key
     * @param password     - text
     *
     * @return boolean - true/false - (User stored/not stored)
     */
    public boolean storeUserAccountInformation(String userId,
                                                 String emailAddress,
                                                 String password,
                                                 String accountType) {

        try {
            boolean recordExist = false;

            // Check if record being inserted already exists
            if (!isEmpty()) {
                // Loop through database array
                for (int i = 0; i < this.getUserAccountInfo(null).size(); i++) {
                    if (this.getUserAccountInfo(null).get(i).getUserId().equals(userId)) {
                        // Record exists

                        // Delete record and set exists to false if deleted otherwise true
                        recordExist = !this.deleteUserAccountInfoByUserId(userId);
                    }
                }
            }

            if (!recordExist) {

                // Create SQLiteDatabase object
                SQLiteDatabase storeToDatabase = this.getWritableDatabase();

                ContentValues contentValues = new ContentValues();
                contentValues.put(UserAccountUtils.FIELD_USER_ID, userId); // UserId
                contentValues.put(UserAccountUtils.FIELD_EMAIL_ADDRESS, emailAddress); // EmailAddress
                contentValues.put(UserAccountUtils.FIELD_PASSWORD, password); // Password
                contentValues.put(UserAccountUtils.FIELD_ACCOUNT_TYPE, accountType); // Account type

                // Inserting Data
                storeToDatabase.insert(TABLE_USER, null, contentValues);
                storeToDatabase.close(); // Closing Connection to the database

                ArrayList<JB_UserAccountInfo> userDetails = this.getUserAccountInfo(
                        null);

                // Load java bean with user data
                JB_UserAccountInfo jbUserAccountInfo = this.loadUserAccountInfoDataJavaBean(
                        userDetails);

                String storedUserId, storedEmailAddress, storedPassword, storedAccountType;
                storedUserId = jbUserAccountInfo.getUserId();
                storedEmailAddress = jbUserAccountInfo.getEmailAddress();
                storedPassword = jbUserAccountInfo.getPassword();
                storedAccountType = jbUserAccountInfo.getAccountType();

                // Check For Change
                return (storedUserId.equalsIgnoreCase(userId)
                        && storedEmailAddress.equalsIgnoreCase(emailAddress)
                        && storedPassword.equalsIgnoreCase(password)
                        && storedAccountType.equalsIgnoreCase(accountType)
                );
            }
        } catch (Exception ignored) {
        }

        return false;
    }

    /**
     * Function to get user data from database
     *
     * @param userId - Users id
     *
     * @return ArrayList<JB_UserAccountInfo> - User details array
     */
    public ArrayList<JB_UserAccountInfo> getUserAccountInfo(String userId) {

        try {
            ArrayList<JB_UserAccountInfo> userDetailsArray = new ArrayList<>();
            JB_UserAccountInfo jbUser = new JB_UserAccountInfo();

            String selectQuery;
            if (userId == null) {

                // Select all records
                selectQuery = "SELECT * FROM " + TABLE_USER;

            } else {

                // Select all records with specified UserId
                selectQuery = "SELECT * FROM " + TABLE_USER + " WHERE "
                        + UserAccountUtils.FIELD_USER_ID + " = '" + userId + "'";
            }

            SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
            Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

            // Loop through cursor
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                jbUser.setUserId(cursor.getString(cursor.getColumnIndex(
                        UserAccountUtils.FIELD_USER_ID)));
                jbUser.setEmailAddress(cursor.getString(cursor.getColumnIndex(
                        UserAccountUtils.FIELD_EMAIL_ADDRESS)));
                jbUser.setPassword(cursor.getString(cursor.getColumnIndex(
                        UserAccountUtils.FIELD_PASSWORD)));
                jbUser.setAccountType(cursor.getString(cursor.getColumnIndex(
                        UserAccountUtils.FIELD_ACCOUNT_TYPE)));

                userDetailsArray.add(jbUser); // Add java bean to ArrayList
            }

            cursor.close(); // Close access to cursor
            sqLiteDatabase.close(); // Closing Connection to the database

            return userDetailsArray; // Return the user details

        } catch (Exception ignored) {
        }

        return null;
    }

    /**
     * Function to delete and recreate all user table
     *
     * @param userId - primary key
     *
     * @return boolean - true/false - (If info deleted / not deleted)
     */
    public boolean deleteUserAccountInfoByUserId(String userId) {

        try {
            // Create SQLiteDatabase object
            SQLiteDatabase database = this.getWritableDatabase();

            database.delete(TABLE_USER, UserAccountUtils.FIELD_USER_ID + "= ?",
                    new String[]{userId}); // Delete All Rows
            database.close(); // Closing Connection to the database

            return DataUtils.isEmptyArrayList(this.getUserAccountInfo(userId));

        } catch (Exception ignored) {
        }

        return false;
    }

    /**
     * Function to update user account information
     *
     * @param userId    - primary key
     * @param newValue    - new value to be set
     * @param updateField - field in database to be updated
     *
     * @return boolean - true/false - (If updated / not updated)
     */
    public boolean updateUserAccountInformation(Context context, String userId,
                                                  String newValue, String updateField) {

        try {
            String oldValue = null, updatedFieldValue = null;

            // Create database object
            SQLiteDatabase updateDatabase = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            // Create class object
            UserDatabase userDatabase = new UserDatabase(context);

            // Get user account information
            ArrayList<JB_UserAccountInfo> userAccountInfo;
            JB_UserAccountInfo jbUserAccountInfo;
            userAccountInfo = userDatabase.getUserAccountInfo(null);
            jbUserAccountInfo = this.loadUserAccountInfoDataJavaBean(userAccountInfo);

            switch (updateField) {

                case UserAccountUtils.FIELD_EMAIL_ADDRESS:
                    // Email address

                    // Get EmailAddress old value
                    oldValue = jbUserAccountInfo.getEmailAddress();

                    // Put EmailAddress new value to content values
                    contentValues.put(UserAccountUtils.FIELD_EMAIL_ADDRESS, newValue);

                    // Update fields
                    updateDatabase.update(TABLE_USER, contentValues,
                            UserAccountUtils.FIELD_USER_ID
                                    + "='" + userId + "'", null);
                    break;

                case UserAccountUtils.FIELD_PASSWORD:
                    // Password

                    // Get password old value
                    oldValue = jbUserAccountInfo.getPassword();
                    contentValues.put(UserAccountUtils.FIELD_PASSWORD, newValue);

                    // Update fields
                    updateDatabase.update(TABLE_USER, contentValues,
                            UserAccountUtils.FIELD_USER_ID
                                    + "='" + userId + "'", null);
                    break;

                case UserAccountUtils.FIELD_ACCOUNT_TYPE:
                    // Account type

                    // Get account type old value
                    oldValue = jbUserAccountInfo.getAccountType();
                    contentValues.put(UserAccountUtils.FIELD_ACCOUNT_TYPE, newValue);

                    // Update fields
                    updateDatabase.update(TABLE_USER, contentValues,
                            UserAccountUtils.FIELD_USER_ID
                                    + "='" + userId + "'", null);
                    break;

                default:
                    break;
            }

            updateDatabase.close(); // Close database connection

            // Get updated account info
            userAccountInfo = userDatabase.getUserAccountInfo(null);
            jbUserAccountInfo = this.loadUserAccountInfoDataJavaBean(userAccountInfo);

            switch (updateField) {

                case UserAccountUtils.FIELD_EMAIL_ADDRESS:
                    updatedFieldValue = jbUserAccountInfo.getEmailAddress();
                    break;

                case UserAccountUtils.FIELD_PASSWORD:
                    updatedFieldValue = jbUserAccountInfo.getPassword();
                    break;

                case UserAccountUtils.FIELD_ACCOUNT_TYPE:
                    updatedFieldValue = jbUserAccountInfo.getAccountType();
                    break;

                default:
                    break;
            }

            return (!(Objects.requireNonNull(updatedFieldValue).equals(oldValue)));

        } catch (Exception ignored) {
        }

        return false;
    }

    /**
     * Function to check if database is empty
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isEmpty() {
        return this.getUserAccountInfo(null).size() == 0;
    }
}
