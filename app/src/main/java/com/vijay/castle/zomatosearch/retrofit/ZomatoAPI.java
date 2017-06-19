package com.vijay.castle.zomatosearch.retrofit;

import com.vijay.castle.zomatosearch.models.CuisinesResponseModel;
import com.vijay.castle.zomatosearch.models.RestaurantSearchResponseModel;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by vijay on 6/9/17.
 */

public interface ZomatoAPI {

    @GET("api/v2.1/search")
    Observable<RestaurantSearchResponseModel> getRestaurants(@Query("q") String query,
                                                             @Query("sort") String sort,
                                                             @Query("order") String order,
                                                             @Query("start") int offset);

    @GET("api/v2.1/search")
    Observable<RestaurantSearchResponseModel> getRestaurantsFilterByCuisine(@Query("q") String query,
                                                                            @Query("cuisines") int cuisine_id,
                                                                            @Query("sort") String sort,
                                                                            @Query("order") String order,
                                                                            @Query("start") int offset);

    @GET("api/v2.1/cuisines")
    Observable<CuisinesResponseModel> getCuisines(@Query("city_id") int cityId);
}
