package com.vijay.castle.zomatosearch.views.activities;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.vijay.castle.zomatosearch.MyApplication;
import com.vijay.castle.zomatosearch.R;
import com.vijay.castle.zomatosearch.adapters.RestaurantFragmentPagerAdapter;
import com.vijay.castle.zomatosearch.adapters.RestaurantRecyclerViewAdapter;
import com.vijay.castle.zomatosearch.api.APIs;
import com.vijay.castle.zomatosearch.database.DatabaseHelper;
import com.vijay.castle.zomatosearch.models.CuisinesModel;
import com.vijay.castle.zomatosearch.models.CuisinesResponseModel;
import com.vijay.castle.zomatosearch.models.RestaurantInfoModel;
import com.vijay.castle.zomatosearch.models.RestaurantModel;
import com.vijay.castle.zomatosearch.models.RestaurantSearchResponseModel;
import com.vijay.castle.zomatosearch.providers.RestaurantColumns;
import com.vijay.castle.zomatosearch.views.fragments.FilterResultFragment;
import com.vijay.castle.zomatosearch.views.fragments.RestaurantFragment;
import com.vijay.castle.zomatosearch.utilities.SendCustomEvent;
import com.vijay.castle.zomatosearch.utilities.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.vijay.castle.zomatosearch.utilities.Utils.getCityId;
import static com.vijay.castle.zomatosearch.utilities.Utils.getOrderBy;
import static com.vijay.castle.zomatosearch.utilities.Utils.getRestaurantName;
import static com.vijay.castle.zomatosearch.utilities.Utils.getSortBy;
import static com.vijay.castle.zomatosearch.utilities.Utils.setCuisineIdMapping;
import static com.vijay.castle.zomatosearch.utilities.Utils.setRestaurantName;
import static com.vijay.castle.zomatosearch.utilities.Utils.showShortToast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        RestaurantFragment.IRestaurantFragmentCallBacks, FilterResultFragment.IFilterCallBacks,
        RestaurantRecyclerViewAdapter.IlikeButtonCallBacks, LoaderManager.LoaderCallbacks<Cursor> {

    Unbinder unbinder;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.no_result_image_view)
    ImageView noResultImageView;
    @BindView(R.id.content_loading_progress)
    ProgressBar contentLoadingProgressBar;
    private SearchView searchView;
    private boolean isFavouritePage, isFavouritePageLoaded;
    private TreeMap<String, Set<RestaurantInfoModel>> restaurantCuisineMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        initializeDrawer(mToolbar);
        showContentProgressBar();
        getCuisinesList(getCityId());
    }

    private void initializeDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void showContentProgressBar() {
        hideViewPager();
        noResultImageView.setVisibility(View.GONE);
        contentLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideContentProgressBar() {
        contentLoadingProgressBar.setVisibility(View.GONE);
    }

    private void hideViewPager() {
        viewPager.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);
    }

    private void showViewPager() {
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
        noResultImageView.setVisibility(View.GONE);
    }

    private void getCuisinesList(int cityId) {

        new APIs(new APIs.Callbacks() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(Object response) {
                processCuisineResponse((CuisinesResponseModel) response);
            }

            @Override
            public void onFailure(Throwable t) {
                showShortToast("Some error occurred");
            }
        }).getCuisines(cityId);
    }

    private void processCuisineResponse(CuisinesResponseModel cuisinesResponseModel) {
        HashMap<String, Integer> cuisineIdMapping = new HashMap<>();
        for (CuisinesModel model : cuisinesResponseModel.getCuisines()) {
            cuisineIdMapping.put(model.getCuisine_name().toLowerCase(), model.getCuisine_id());
        }
        setCuisineIdMapping(cuisineIdMapping);
        getRestaurantsByQuery(getRestaurantName(), 0);
    }

    private void getRestaurantsByQuery(String query, int offset) {
        new APIs(new APIs.Callbacks() {
            @Override
            public void onStart() {
                showContentProgressBar();
            }

            @Override
            public void onSuccess(Object response) {
                initializeData((RestaurantSearchResponseModel) response);
                getSupportLoaderManager().initLoader(1, null, MainActivity.this);
                hideContentProgressBar();
            }

            @Override
            public void onFailure(Throwable t) {
                showContentProgressBar();
                showShortToast("Some error occurred");
            }
        }).getRestaurantsByQuery(query, getSortBy(), getOrderBy(), offset);
    }

    private void initializeData(RestaurantSearchResponseModel responseModel) {
        restaurantCuisineMap = new TreeMap<>();
        updateData(responseModel, restaurantCuisineMap);
        if (responseModel.getRestaurants().size() == 0) {
            showNoResultFoundImage();
        } else {
            setupViewPager(viewPager, restaurantCuisineMap);
        }
    }

    private void showNoResultFoundImage() {
        noResultImageView.setVisibility(View.VISIBLE);
        hideViewPager();
    }

    private TreeMap<String, Set<RestaurantInfoModel>> updateData(RestaurantSearchResponseModel responseModel,
                                                                 TreeMap<String, Set<RestaurantInfoModel>> restaurantCuisineMap) {
        for (RestaurantModel model : responseModel.getRestaurants()) {
            String cuisines = model.getRestaurant().getCuisines();
            for (String cuisine : cuisines.split(",")) {
                cuisine = cuisine.trim();
                Set<RestaurantInfoModel> currentList = new LinkedHashSet<>();
                if (restaurantCuisineMap.containsKey(cuisine)) {
                    currentList = restaurantCuisineMap.get(cuisine);
                }
                currentList.add(model.getRestaurant());
                restaurantCuisineMap.put(cuisine, currentList);
            }
        }
        return restaurantCuisineMap;
    }

    private void setupViewPager(ViewPager viewPager, TreeMap<String, Set<RestaurantInfoModel>> restaurantCuisineMap) {
        EventBus.getDefault().postSticky(restaurantCuisineMap);
        showViewPager();
        if (restaurantCuisineMap.keySet().size() <= 3) {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        } else {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }
        addTabs(viewPager, restaurantCuisineMap.keySet());
        tabLayout.setupWithViewPager(viewPager);
    }

    private void addTabs(ViewPager viewPager, Set<String> cuisines) {
        RestaurantFragmentPagerAdapter viewPagerAdapter = new RestaurantFragmentPagerAdapter(getSupportFragmentManager());
        for (String cuisine : cuisines) {
            Fragment fragment = RestaurantFragment.newInstance();
            Bundle bundle = new Bundle();
            bundle.putString("cuisine", cuisine);
            bundle.putBoolean("isFavouritePage", isFavouritePage);
            fragment.setArguments(bundle);
            viewPagerAdapter.addFrag(fragment, cuisine);
        }
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public TreeMap<String, Set<RestaurantInfoModel>> updateResponseData(RestaurantSearchResponseModel model,
                                                                        TreeMap<String, Set<RestaurantInfoModel>> mapping) {
        return updateData(model, mapping);
    }

    @Override
    public void onTabRemove(String cuisine) {
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        searchView = (SearchView) menu.findItem(R.id.m_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSupportActionBar().setTitle(query);
                setRestaurantName(query);
                getRestaurantsByQuery(query, 0);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                menu.findItem(R.id.m_search).collapseActionView();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.m_filter:
                openFilterDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        isFavouritePage = false;
        isFavouritePageLoaded = false;

        switch (item.getItemId()) {
            case R.id.nav_favourite:
                isFavouritePage = true;
                getSupportLoaderManager().initLoader(1, null, MainActivity.this);
                break;

            case R.id.nav_home:
                getRestaurantsByQuery(getRestaurantName(), 0);
                break;

            case R.id.nav_filter:
                openFilterDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openFilterDialog() {
        FragmentManager fm = getSupportFragmentManager();
        FilterResultFragment fragment = new FilterResultFragment();
        fragment.show(fm, "Show Fragment");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (!searchView.isIconified()) {
            searchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onApplyFilter() {
        getRestaurantsByQuery(getRestaurantName(), 0);
        Utils.showSnackBarMessage(getWindow().getDecorView().findViewById(android.R.id.content), "Filter applied :)");
    }

    @Override
    public void onDismissFilter() {

    }

    @Override
    public void onLike(String cuisine, RestaurantInfoModel model) {
        DatabaseHelper.getInstance(this).insertRestaurantInfo(cuisine, model);
    }

    @Override
    public void onDislike(String cuisine, RestaurantInfoModel model) {
        DatabaseHelper.getInstance(this).deleteRestaurantInfo(cuisine, model);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[]{
                RestaurantColumns.Restaurants.CUISINE,
                RestaurantColumns.Restaurants.RESTAURANT_DATA,
        };
        return new CursorLoader(this,
                RestaurantColumns.Restaurants.CONTENT_URI,
                projection,
                null,
                null,
                "_id DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        TreeMap<String, Set<RestaurantInfoModel>> currentMapping = new TreeMap<>();
        int count = cursor.getCount();
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                int cuisineIndex = cursor.getColumnIndex(RestaurantColumns.Restaurants.CUISINE);
                int dataIndex = cursor.getColumnIndex(RestaurantColumns.Restaurants.RESTAURANT_DATA);
                String cuisine = cursor.getString(cuisineIndex);
                RestaurantInfoModel model = MyApplication.getGsonInstance().fromJson(cursor.getString(dataIndex), RestaurantInfoModel.class);
                setBookmarkToCuisineRestMapping(cuisine, model); // update bookmark to original mapping
                Set<RestaurantInfoModel> currentList = new LinkedHashSet<>();
                if (currentMapping.containsKey(cuisine)) {
                    currentList = currentMapping.get(cuisine);
                }
                currentList.add(model);
                currentMapping.put(cuisine, currentList);
                cursor.moveToNext();
            }
        }
        afterRestaurantDBChange(count, currentMapping);
    }

    private void afterRestaurantDBChange(int count, TreeMap<String, Set<RestaurantInfoModel>> currentMapping) {
        if (isFavouritePage) {
            if (count == 0) { // If nor results then show noResultFound
                showNoResultFoundImage();
                return;
            }
            if (isFavouritePageLoaded) {
                // IF favourite page create the just update data
                EventBus.getDefault().postSticky(new SendCustomEvent(SendCustomEvent.EVENT_DB_CHANGE, currentMapping));
            } else { // If favourite not created yet then create
                setupViewPager(viewPager, currentMapping);
                isFavouritePageLoaded = true;
            }
        } else { // Send update changes to fragments
            EventBus.getDefault().postSticky(new SendCustomEvent(SendCustomEvent.EVENT_DB_CHANGE, restaurantCuisineMap));
        }
    }

    private void setBookmarkToCuisineRestMapping(String cuisine, RestaurantInfoModel model) {
        Set<RestaurantInfoModel> restaurantList = restaurantCuisineMap.get(cuisine);
        for (RestaurantInfoModel infoMode : restaurantList) {
            if (infoMode.equals(model)) {
                infoMode.setBookMarked(true);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}