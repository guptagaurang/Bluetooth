package com.android.bluetoothmusic.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserDatabaseHelper extends BluetoothDatabaseHelper {

    public UserDatabaseHelper(Context context) {
        super(context);
    }

    public boolean createRegistration(String username, String email, String password, String phone_number, String address, String status, String created_at, String updated_at) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + BLUETOOTH_MUSIC_TABLE + " WHERE " + EMAIL + " = ?", new String[]{email});
        boolean createSuccessful = false;
        if (!cursor.moveToFirst()) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(USERNAME, username);
            contentValues.put(EMAIL, email);
            contentValues.put(PASSWORD, password);
            contentValues.put(PHONE_NUMBER, phone_number);
            contentValues.put(ADDRESS, address);
            contentValues.put(STATUS, status);
            contentValues.put(CREATED_AT, created_at);
            contentValues.put(UPDATED_AT, updated_at);
            createSuccessful = getWritableDatabase().insert(BLUETOOTH_MUSIC_TABLE, null, contentValues) > 0;
            getWritableDatabase().close();
        }
        return createSuccessful;
    }

    public String registrationCreate(String username, String email, String password, String phone_number, String address, String status, String created_at, String updated_at) throws Exception {
        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + BLUETOOTH_MUSIC_TABLE + " WHERE " + EMAIL + " = ?", new String[]{email})) {
            if (cursor.moveToFirst()) {
                return "Email already exist!";
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME, username);
        contentValues.put(EMAIL, email);
        contentValues.put(PASSWORD, password);
        contentValues.put(PHONE_NUMBER, phone_number);
        contentValues.put(ADDRESS, address);
        contentValues.put(STATUS, status);
        contentValues.put(CREATED_AT, created_at);
        contentValues.put(UPDATED_AT, updated_at);
        boolean createSuccessful = getWritableDatabase().insert(BLUETOOTH_MUSIC_TABLE, null, contentValues) > 0;
        getWritableDatabase().close();
        return createSuccessful ? "Registration Successful" : "Registration Failed";
    }

    public JSONObject registration(String username, String email, String password, String phone_number, String address, String status, String created_at, String updated_at) throws Exception {
        JSONObject registrationObject = new JSONObject();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + BLUETOOTH_MUSIC_TABLE + " WHERE " + EMAIL + " = ?", new String[]{email})) {
            if (cursor.moveToFirst()) {
                registrationObject.put("message", "Email already exist!");
                registrationObject.put("success", false);
                return registrationObject;
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(USERNAME, username);
        contentValues.put(EMAIL, email);
        contentValues.put(PASSWORD, password);
        contentValues.put(PHONE_NUMBER, phone_number);
        contentValues.put(ADDRESS, address);
        contentValues.put(STATUS, status);
        contentValues.put(CREATED_AT, created_at);
        contentValues.put(UPDATED_AT, updated_at);
        boolean createSuccessful = getWritableDatabase().insert(BLUETOOTH_MUSIC_TABLE, null, contentValues) > 0;
        getWritableDatabase().close();

        if (createSuccessful) {
            registrationObject.put("message", "Registration Successful");
            registrationObject.put("success", true);
        } else {
            registrationObject.put("message", "Registration Failed");
            registrationObject.put("success", false);
        }
        return registrationObject;
    }

    public JSONArray loginCreate(String email, String password) throws JSONException {
        String[] selectionArgs = {email, password};

        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + BLUETOOTH_MUSIC_TABLE + " where " + EMAIL + "= ? AND " + PASSWORD + "= ?", selectionArgs);

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    if (cursor.getString(i) != null) {
                        rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                    } else {
                        rowObject.put(cursor.getColumnName(i), "");
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet;
    }

    public JSONObject login(String email, String password) throws JSONException {
        JSONObject loginObject = new JSONObject();
        loginObject.put("message", "Login failed");
        loginObject.put("success", false);

        String[] selectionArgs = {email, password};
        Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + BLUETOOTH_MUSIC_TABLE + " WHERE " + EMAIL + "= ? AND " + PASSWORD + "= ?", selectionArgs);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {

            int totalColumn = cursor.getColumnCount();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null && cursor.getString(i) != null) {
                    loginObject.put(cursor.getColumnName(i), cursor.getString(i));
                    loginObject.put("message", "Login Successful");
                    loginObject.put("success", true);
                }
            }
            cursor.moveToNext();
        }
        cursor.close();
        return loginObject;
    }

    public JSONObject updateProfile(int id, String username, String email, String status, String updated_at) throws Exception {
        JSONObject updateProfileObject = new JSONObject();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + BLUETOOTH_MUSIC_TABLE + " WHERE " + EMAIL + " = ?", new String[]{email})) {
            if (cursor.moveToFirst()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(USERNAME, username);
                contentValues.put(EMAIL, email);
                contentValues.put(STATUS, status);
                contentValues.put(UPDATED_AT, updated_at);

                boolean createSuccessful = getWritableDatabase().update(BLUETOOTH_MUSIC_TABLE, contentValues, USER_ID + " = ?", new String[]{Integer.toString(id)}) > 0;
                getWritableDatabase().close();

                if (createSuccessful) {
                    String[] selectionArgs = {email, String.valueOf(id)};
                    Cursor cursorUpdate = getReadableDatabase().rawQuery("SELECT * FROM " + BLUETOOTH_MUSIC_TABLE + " WHERE " + EMAIL + "= ? AND " + USER_ID + "= ?", selectionArgs);
                    cursorUpdate.moveToFirst();
                    while (!cursorUpdate.isAfterLast()) {

                        int totalColumn = cursorUpdate.getColumnCount();

                        for (int i = 0; i < totalColumn; i++) {
                            if (cursorUpdate.getColumnName(i) != null && cursorUpdate.getString(i) != null) {
                                updateProfileObject.put(cursorUpdate.getColumnName(i), cursorUpdate.getString(i));
                                updateProfileObject.put("message", "Profile Update Successful");
                                updateProfileObject.put("success", true);
                            }
                        }
                        cursorUpdate.moveToNext();
                    }
                    cursorUpdate.close();
                } else {
                    updateProfileObject.put("message", "Profile Update Failed");
                    updateProfileObject.put("success", false);
                }

            } else {
                updateProfileObject.put("message", "Email already exist!");
                updateProfileObject.put("success", false);
            }
            return updateProfileObject;
        }
    }

    public JSONObject changePassword(int id, String oldPassword, String confirmPassword) throws Exception {
        JSONObject updateProfileObject = new JSONObject();

        try (Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + BLUETOOTH_MUSIC_TABLE + " WHERE "+ PASSWORD + "= ? AND " + USER_ID + " = ?", new String[]{oldPassword, String.valueOf(id)})) {
            if (cursor.moveToFirst()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(PASSWORD, confirmPassword);

                boolean createSuccessful = getWritableDatabase().update(BLUETOOTH_MUSIC_TABLE, contentValues, USER_ID + " = ?", new String[]{Integer.toString(id)}) > 0;
                getWritableDatabase().close();

                if (createSuccessful) {
                    String[] selectionArgs = {String.valueOf(id)};
                    Cursor cursorUpdate = getReadableDatabase().rawQuery("SELECT * FROM " + BLUETOOTH_MUSIC_TABLE + " WHERE " + USER_ID + "= ?", selectionArgs);
                    cursorUpdate.moveToFirst();
                    while (!cursorUpdate.isAfterLast()) {

                        int totalColumn = cursorUpdate.getColumnCount();

                        for (int i = 0; i < totalColumn; i++) {
                            if (cursorUpdate.getColumnName(i) != null && cursorUpdate.getString(i) != null) {
                                updateProfileObject.put(cursorUpdate.getColumnName(i), cursorUpdate.getString(i));
                                updateProfileObject.put("message", "Password Update Successful");
                                updateProfileObject.put("success", true);
                            }
                        }
                        cursorUpdate.moveToNext();
                    }
                    cursorUpdate.close();
                } else {
                    updateProfileObject.put("message", "Password Update Failed");
                    updateProfileObject.put("success", false);
                }

            } else {
                updateProfileObject.put("message", "Password not exist!");
                updateProfileObject.put("success", false);
            }
            return updateProfileObject;
        }
    }

}
