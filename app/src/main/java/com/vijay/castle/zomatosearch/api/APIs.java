package com.vijay.castle.zomatosearch.api;

import com.vijay.castle.zomatosearch.models.CuisinesResponseModel;
import com.vijay.castle.zomatosearch.models.RestaurantSearchResponseModel;
import com.vijay.castle.zomatosearch.retrofit.RetrofitClent;
import com.vijay.castle.zomatosearch.retrofit.ZomatoAPI;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;

import static com.vijay.castle.zomatosearch.utilities.Utils.getOrderBy;
import static com.vijay.castle.zomatosearch.utilities.Utils.getSortBy;

/**
 * Created by ijay on 6/9/17.
 */

public class APIs {
    private Callbacks callbacks;
    private ZomatoAPI zomatoAPI;

    public interface Callbacks {
        void onStart();
        void onSuccess(Object response);
        void onFailure(Throwable t);
    }

    public APIs(Callbacks callbacks) {
        this.callbacks = callbacks;
        zomatoAPI = RetrofitClent.getInstance().create(ZomatoAPI.class);
    }

    public void getCuisines(int cityId) {
        Observable<CuisinesResponseModel> response = zomatoAPI.getCuisines(cityId);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<CuisinesResponseModel>() {
                    @Override
                    public void onNext(CuisinesResponseModel cuisinesResponseModel) {
                        callbacks.onSuccess(cuisinesResponseModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callbacks.onFailure(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getRestaurantsByQuery(String query, String sortBy, String orderBy, int offset) {
        Observable<RestaurantSearchResponseModel> response = zomatoAPI.getRestaurants(query, sortBy, orderBy, offset);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<RestaurantSearchResponseModel>() {
                    @Override
                    public void onNext(RestaurantSearchResponseModel restaurantSearchResponseModel) {
                        callbacks.onSuccess(restaurantSearchResponseModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callbacks.onFailure(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void getMoreRestaurantsByQueryAndCuisine(String query, int cuisine_id, int offset) {
        ZomatoAPI zomatoAPI = RetrofitClent.getInstance().create(ZomatoAPI.class);
        Observable<RestaurantSearchResponseModel> response = zomatoAPI.getRestaurantsFilterByCuisine(query, cuisine_id, getSortBy(), getOrderBy(), offset);
        response.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DefaultObserver<RestaurantSearchResponseModel>() {
                    @Override
                    public void onNext(RestaurantSearchResponseModel restaurantSearchResponseModel) {
                        callbacks.onSuccess(restaurantSearchResponseModel);
                    }

                    @Override
                    public void onError(Throwable e) {
                        callbacks.onFailure(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
