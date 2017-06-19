package com.vijay.castle.zomatosearch.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.castle.zomatosearch.MyApplication;

import java.util.HashMap;

/**
 * Created by vijay on 6/9/17.
 */

public class Utils {
    private static HashMap<String, Integer> cuisineIdMapping;
    private static String CURRENT_ORDER_BY = null;
    private static String CURRENT_SORT_BY = null;
    private static String CURRENT_RESTAURANT = null;
    private static int CURRENT_CITY_ID = -1;

    public static boolean isNetworkAvailable() {
        ConnectivityManager
                cm = (ConnectivityManager) MyApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public static int dpToPx(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    public static void showSnackBarMessage(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public static void changeTextColorOfSearchView(View searchView, int color) {
        int id = searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchView.findViewById(id);
        textView.setTextColor(color);
    }

    public static String getSortBy() {
        if (CURRENT_SORT_BY == null) {
            CURRENT_SORT_BY = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString(Constants.SORT_BY_KEY, Constants.DEFAULT_SORT_BY);
        }
        return CURRENT_SORT_BY;
    }

    public static void setSortBy(String sortBy) {
        CURRENT_SORT_BY = sortBy;
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putString(Constants.SORT_BY_KEY, sortBy).apply();
    }

    public static String getOrderBy() {
        if (CURRENT_ORDER_BY == null) {
            CURRENT_ORDER_BY = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString(Constants.ORDER_BY_KEY, Constants.DEFAULT_ORDER_BY);
        }
        return CURRENT_ORDER_BY;
    }

    public static void setOrderBy(String orderBy) {
        CURRENT_ORDER_BY = orderBy;
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putString(Constants.ORDER_BY_KEY, orderBy).apply();
    }

    public static int getCityId() {
        if (CURRENT_CITY_ID == -1) {
            CURRENT_CITY_ID = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getInt(Constants.CITY_ID, Constants.DEFAULT_CITY_ID);
        }
        return CURRENT_CITY_ID;
    }

    public static void setCityId(int id) {
        CURRENT_CITY_ID = id;
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putInt(Constants.CITY_ID, id).apply();
    }

    public static String getRestaurantName() {
        if (CURRENT_RESTAURANT == null) {
            CURRENT_RESTAURANT = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).getString(Constants.RESTAURANT_NAME, Constants.DEFAULT_RESTAURANT_NAME);
        }
        return CURRENT_RESTAURANT;
    }

    public static void setRestaurantName(String restaurantName) {
        CURRENT_RESTAURANT = restaurantName;
        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit().putString(Constants.RESTAURANT_NAME, restaurantName).apply();
    }

    public static HashMap<String, Integer> getCuisineIdMapping() {
        return cuisineIdMapping;
    }

    public static void setCuisineIdMapping(HashMap<String, Integer> map) {
        cuisineIdMapping = map;
    }

    public static boolean isRecyclerScrollable(RecyclerView recyclerView) {
        return recyclerView.computeHorizontalScrollRange() > recyclerView.getWidth() || recyclerView.computeVerticalScrollRange() > recyclerView.getHeight();
    }

    public static void showShortToast(String message) {
        Toast.makeText(MyApplication.getContext(), "Some error occurred", Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(String message) {
        Toast.makeText(MyApplication.getContext(), "Some error occurred", Toast.LENGTH_LONG).show();
    }

//    public static void storeBookmarkedRestaurant(List<RestaurantInfoModel> bookmarkedList) {
//        PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext()).edit()
//                .putString("bookmarked", MyApplication.getGsonInstance().toJson(bookmarkedList)).apply();
//    }

//    public static List<RestaurantInfoModel> getBookmarkedRestaurant() {
//        return MyApplication.getGsonInstance().fromJson(PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext())
//                .getString("bookmarked", "[]"), List.class);
//    }
}
