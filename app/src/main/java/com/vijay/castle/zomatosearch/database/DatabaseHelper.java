package com.vijay.castle.zomatosearch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vijay.castle.zomatosearch.MyApplication;
import com.vijay.castle.zomatosearch.models.RestaurantInfoModel;
import com.vijay.castle.zomatosearch.providers.RestaurantColumns;

/**
 * Created by vijay on 6/9/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "restaurant.db";

    private static final int DATABASE_VERSION = 1;

    public static final String RESTAURANT_TABLE_NAME = "restaurant";

    private static DatabaseHelper databaseHelper;

    private Context mContext;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    public static DatabaseHelper getInstance(Context context) {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper(context);
        }
        return databaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + RESTAURANT_TABLE_NAME + " (" + RestaurantColumns.Restaurants.ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + RestaurantColumns.Restaurants.CUISINE + " VARCHAR(100),"
                + RestaurantColumns.Restaurants.RESTAURANT_ID + " INTEGER,"
                + RestaurantColumns.Restaurants.NAME + " VARCHAR(100),"
                + RestaurantColumns.Restaurants.RESTAURANT_DATA + " LONGTEXT" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RESTAURANT_TABLE_NAME);
        onCreate(db);
    }

    public void insertRestaurantInfo(String cuisine, RestaurantInfoModel model) {
        ContentValues values = new ContentValues();
        values.put(RestaurantColumns.Restaurants.NAME, model.getName());
        values.put(RestaurantColumns.Restaurants.CUISINE, cuisine);
        values.put(RestaurantColumns.Restaurants.RESTAURANT_ID, model.getId());
        values.put(RestaurantColumns.Restaurants.RESTAURANT_DATA, MyApplication.getGsonInstance().toJson(model));
        mContext.getContentResolver().insert(RestaurantColumns.Restaurants.CONTENT_URI, values);
    }

    public void deleteRestaurantInfo(String cuisine, RestaurantInfoModel model) {
        String where = RestaurantColumns.Restaurants.CUISINE + "=? AND "
                + RestaurantColumns.Restaurants.RESTAURANT_ID + "=?";
        String[] args = {cuisine, String.valueOf(model.getId())};
        mContext.getContentResolver().delete(RestaurantColumns.Restaurants.CONTENT_URI, where, args);
    }
}