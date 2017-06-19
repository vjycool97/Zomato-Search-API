package com.vijay.castle.zomatosearch.models;

/**
 * Created by vijay on 6/9/17.
 */

public class CuisinesModel {
    private CuisineInfoModel cuisine;

    private CuisineInfoModel getCuisineInfo() {
        return cuisine;
    }

    public int getCuisine_id() {
        return getCuisineInfo().cuisine_id;
    }

    public String getCuisine_name() {
        return getCuisineInfo().cuisine_name;
    }

    private class CuisineInfoModel {
        private int cuisine_id;
        private String cuisine_name;
    }
}
