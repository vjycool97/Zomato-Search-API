package com.vijay.castle.zomatosearch.providers;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by vijay on 6/9/17.
 */

public class RestaurantColumns {

    public RestaurantColumns() {
    }

    public static final class Restaurants implements BaseColumns {

        public Restaurants() {
        }
        public static final Uri CONTENT_URI = Uri.parse("content://"
                + RestaurantContentProvider.AUTHORITY + "/restaurants");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.castle.zomatorestaurant";

        public static final String PARTICULAR_CONTENT_TYPE = "vnd.android.cursor.item/vnd.castle.zomatorestaurant";

        public static final String ID = "_id";

        public static final String CUISINE = "cuisine";

        public static final String NAME = "name";

        public static final String RESTAURANT_DATA = "data";

        public static final String RESTAURANT_ID = "restaurant_id";
    }
}
