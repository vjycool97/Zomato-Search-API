package com.vijay.castle.zomatosearch.views.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vijay.castle.zomatosearch.api.APIs;
import com.vijay.castle.zomatosearch.utilities.PaginationScrollListener;
import com.vijay.castle.zomatosearch.R;
import com.vijay.castle.zomatosearch.adapters.RestaurantRecyclerViewAdapter;
import com.vijay.castle.zomatosearch.models.RestaurantInfoModel;
import com.vijay.castle.zomatosearch.models.RestaurantSearchResponseModel;
import com.vijay.castle.zomatosearch.utilities.SendCustomEvent;
import com.vijay.castle.zomatosearch.utilities.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.vijay.castle.zomatosearch.utilities.Utils.getCuisineIdMapping;
import static com.vijay.castle.zomatosearch.utilities.Utils.getRestaurantName;
import static com.vijay.castle.zomatosearch.utilities.Utils.showShortToast;

/**
 * Created by vijay on 6/10/17.
 */

public class RestaurantFragment extends Fragment {

    private String cuisine;
    private boolean isLoading, isFavouritePage;
    private Set<RestaurantInfoModel> restaurantList;
    @BindView(R.id.no_result_image_view) ImageView noResultFound;
    @BindView(R.id.recycler_view) RecyclerView recyclerView;
    private TreeMap<String, Set<RestaurantInfoModel>> cuisineRestaurantMapping;
    private IRestaurantFragmentCallBacks iCallBacks;
    private RestaurantRecyclerViewAdapter recyclerViewAdapter;
    private int TOTAL_RESULT = 0;
    private int CURRENT_RESULT = 0;
    EventBus bus = EventBus.getDefault();

    public interface IRestaurantFragmentCallBacks {
        TreeMap<String, Set<RestaurantInfoModel>> updateResponseData(RestaurantSearchResponseModel model,
                                                                     TreeMap<String, Set<RestaurantInfoModel>> mapping);
        void onTabRemove(String cuisine);
    }

    public RestaurantFragment() {
    }

    public static RestaurantFragment newInstance() {
        return new RestaurantFragment();
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
        iCallBacks = (IRestaurantFragmentCallBacks) getContext();
        cuisine = getArguments().getString("cuisine");
        isFavouritePage = getArguments().getBoolean("isFavouritePage");
        cuisineRestaurantMapping = EventBus.getDefault().getStickyEvent(TreeMap.class);
        restaurantList = cuisineRestaurantMapping.get(cuisine);
        if (restaurantList == null) {
            iCallBacks.onTabRemove(cuisine);
            restaurantList = new HashSet<>();
        }
        CURRENT_RESULT = restaurantList.size();
    }

    @Override
    public void onStop() {
        bus.unregister(this);
        super.onStop();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerViewAdapter = new RestaurantRecyclerViewAdapter(getContext(), isFavouritePage, cuisine, restaurantList);
        recyclerView.setAdapter(recyclerViewAdapter);
        checkForLastPage();
        recyclerView.addOnScrollListener(new PaginationScrollListener() {
            @Override
            public void loadMoreItems() {
                tryMoreResults();
            }

            @Override
            public boolean isLastPage() {
                return checkForLastPage();
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean isFavouritePage() {
                return isFavouritePage;
            }
        });

        if (!Utils.isRecyclerScrollable(recyclerView) && TOTAL_RESULT != CURRENT_RESULT && !isFavouritePage) {
            tryMoreResults();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    private boolean checkForLastPage() {
        if (TOTAL_RESULT == CURRENT_RESULT) {
            recyclerViewAdapter.hideFooter();
            return true;
        }
        return false;
    }

    private void tryMoreResults() {
        isLoading = true;
        getMoreRestaurantsByQueryAndCuisine(getRestaurantName(),
                getCuisineIdMapping().get(cuisine.toLowerCase()), restaurantList.size());
    }

    private void getMoreRestaurantsByQueryAndCuisine(String query, int cuisine_id, int offset) {
        new APIs(new APIs.Callbacks() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object response) {
                RestaurantSearchResponseModel restaurantSearchResponseModel = (RestaurantSearchResponseModel) response;
                TOTAL_RESULT = restaurantSearchResponseModel.getResultsFound();
                CURRENT_RESULT = restaurantSearchResponseModel.getResultsStart()
                        + restaurantSearchResponseModel.getResultsShown();
                notifyDataChange(iCallBacks.updateResponseData(restaurantSearchResponseModel, cuisineRestaurantMapping));
                if (TOTAL_RESULT == CURRENT_RESULT) { // check if this is the last result then hide footer
                    recyclerViewAdapter.hideFooter();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Throwable t) {
                isLoading = false;
                showShortToast("Some error occurred");
            }
        }).getMoreRestaurantsByQueryAndCuisine(query, cuisine_id, offset);
    }

    @Subscribe
    public void onEvent(final SendCustomEvent event) {
        if (event.getTypeOfEvent() == SendCustomEvent.EVENT_DB_CHANGE) { // DBChanges event
            cuisineRestaurantMapping = (TreeMap<String, Set<RestaurantInfoModel>>) event.getData();
            if (isFavouritePage) {
                restaurantList.clear();
                if (cuisineRestaurantMapping.get(cuisine) != null) {
                    restaurantList.addAll(cuisineRestaurantMapping.get(cuisine));
                } else {
                    recyclerView.setVisibility(View.GONE);
                    noResultFound.setVisibility(View.VISIBLE);
                }
                recyclerViewAdapter.notifyDataSetChanged();
            } else {
                notifyDataChange(cuisineRestaurantMapping);
            }
        }
    }

    public void notifyDataChange(TreeMap<String, Set<RestaurantInfoModel>> restaurantCuisineMap) {
        restaurantList = restaurantCuisineMap.get(cuisine);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
