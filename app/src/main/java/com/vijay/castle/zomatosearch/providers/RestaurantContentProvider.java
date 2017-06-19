package com.vijay.castle.zomatosearch.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.vijay.castle.zomatosearch.database.DatabaseHelper;

import java.util.HashMap;

/**
 * Created by vijay on 6/9/17.
 */

public class RestaurantContentProvider extends ContentProvider {

    private static final String TAG = "RestaurantContentProvider";

    public static final String AUTHORITY = "com.vijay.castle.zomatosearch.providers.RestaurantContentProvider";

    private static final int RESTAURANTS = 1;

    private static final int RESTAURANT_ID = 0;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "restaurants", RESTAURANTS);
        uriMatcher.addURI(AUTHORITY, "restaurants/#", RESTAURANT_ID);
    }

    private static HashMap<String, String> RESTAURANT_PROJECTION_MAP;

    private DatabaseHelper dbHelper;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case RESTAURANTS:
                break;

            case RESTAURANT_ID:
                where = where + RestaurantColumns.Restaurants.ID + " = " + uri.getLastPathSegment();
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = db.delete(DatabaseHelper.RESTAURANT_TABLE_NAME, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case RESTAURANTS:
                return RestaurantColumns.Restaurants.CONTENT_TYPE;

            case RESTAURANT_ID:
                return RestaurantColumns.Restaurants.PARTICULAR_CONTENT_TYPE;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (uriMatcher.match(uri) != RESTAURANTS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(DatabaseHelper.RESTAURANT_TABLE_NAME, "", values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(RestaurantColumns.Restaurants.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        dbHelper = DatabaseHelper.getInstance(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(DatabaseHelper.RESTAURANT_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case RESTAURANTS:
                qb.setProjectionMap(RESTAURANT_PROJECTION_MAP);
                break;

            case RESTAURANT_ID:
                selection = selection + RestaurantColumns.Restaurants.ID + " = " + uri.getLastPathSegment();
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (sortOrder == null || sortOrder.equals("")){
            /**
             * By default sort on restaurant names
             */
            sortOrder = RestaurantColumns.Restaurants.NAME;
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (uriMatcher.match(uri)) {
            case RESTAURANTS:
                count = db.update(DatabaseHelper.RESTAURANT_TABLE_NAME, values, where, whereArgs);
                break;

            case RESTAURANT_ID:
            count = db.update(DatabaseHelper.RESTAURANT_TABLE_NAME, values,
                    RestaurantColumns.Restaurants.ID + " = " + uri.getPathSegments().get(1) +
                            (TextUtils.isEmpty(where) ? "" : " AND (" +where + ')'), whereArgs);
            break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}