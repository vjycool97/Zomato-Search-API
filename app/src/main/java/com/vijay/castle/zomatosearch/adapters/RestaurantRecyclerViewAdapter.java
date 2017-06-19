package com.vijay.castle.zomatosearch.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.vijay.castle.zomatosearch.R;
import com.vijay.castle.zomatosearch.models.RestaurantInfoModel;
import com.squareup.picasso.Picasso;

import java.util.Set;

import butterknife.ButterKnife;

/**
 * Created by ijay on 6/10/17.
 */

public class RestaurantRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private String cuisine;
    private Set<RestaurantInfoModel> restaurantInfoModelList;
    private final int FOOTER_LAYOUT = 100;
    private final int ITEM_LAYOUT = 200;
    private final static int FADE_DURATION = 800;
    private boolean isAllItemLoaded;
    private boolean isFavouritePage;
    private IlikeButtonCallBacks ilikeButtonCallBacks;

    public interface IlikeButtonCallBacks {
        void onLike(String cuisine, RestaurantInfoModel model);
        void onDislike(String cuisine, RestaurantInfoModel model);
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView avgCostTextView;
        TextView addressTextView;
        TextView agrRatingTextView;
        LikeButton bookmarkButton;
        ImageView thumbImageView;
        ImageView deleteImageView;
        View rowView;

        ItemViewHolder(View rowView) {
            super(rowView);
            this.rowView = rowView;
            nameTextView = ButterKnife.findById(rowView, R.id.name_TV);
            avgCostTextView = ButterKnife.findById(rowView, R.id.avg_cost_TV);
            addressTextView = ButterKnife.findById(rowView, R.id.address_text_TV);
            agrRatingTextView = ButterKnife.findById(rowView, R.id.aggregate_rating_TV);
            thumbImageView = ButterKnife.findById(rowView, R.id.thumb_IV);
            deleteImageView = ButterKnife.findById(rowView, R.id.delete_item);
            bookmarkButton = ButterKnife.findById(rowView, R.id.bookmark_item);
        }

        void configureViews(final RestaurantInfoModel model) {
            nameTextView.setText(model.getName());
            addressTextView.setText(model.getLocality());
            agrRatingTextView.setText(model.getAggregate_rating());
            agrRatingTextView.setBackgroundColor(Color.parseColor(model.getRating_color()));
            avgCostTextView.setText("₹" + model.getAverage_cost_for_two() + " (Approx for 2)");
            bookmarkButton.setLiked(model.isBookMarked());
            if (isFavouritePage) {
                bookmarkButton.setVisibility(View.GONE);
                deleteImageView.setVisibility(View.VISIBLE);
            } else {
                bookmarkButton.setVisibility(View.VISIBLE);
                deleteImageView.setVisibility(View.GONE);
            }
            bookmarkButton.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    ilikeButtonCallBacks.onLike(cuisine, model);
                    model.setBookMarked(true);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    ilikeButtonCallBacks.onDislike(cuisine, model);
                    model.setBookMarked(false);
                }
            });

            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ilikeButtonCallBacks.onDislike(cuisine, model);
                }
            });

            try {
                Picasso.with(mContext).load(model.getThumb()).placeholder(R.drawable.placeholder_food).into(thumbImageView);
            } catch (Exception ignored) {}
        }
    }

    private class FooterViewHolder extends RecyclerView.ViewHolder {
        View rowView;
        FooterViewHolder(View rowView) {
            super(rowView);
            this.rowView = rowView;
        }

        void configureViews() {
            if (isFavouritePage || isAllItemLoaded) {
                itemView.setVisibility(View.GONE);
            }
        }
    }

    public RestaurantRecyclerViewAdapter(Context mContext, boolean isFavouritePage, String cuisine, Set<RestaurantInfoModel> restaurantInfoModelList) {
        this.mContext = mContext;
        this.cuisine = cuisine;
        this.isFavouritePage = isFavouritePage;
        this.restaurantInfoModelList = restaurantInfoModelList;
        ilikeButtonCallBacks = (IlikeButtonCallBacks) mContext;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case FOOTER_LAYOUT:
                v = LayoutInflater.from(mContext).inflate(R.layout.restaurant_loading_footer, parent, false);
                return new FooterViewHolder(v);

            default:
                v = LayoutInflater.from(mContext).inflate(R.layout.restaurant_list, parent, false);
                return new ItemViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_LAYOUT:
                ((ItemViewHolder)holder).configureViews((RestaurantInfoModel) restaurantInfoModelList.toArray()[position]);
                break;

            case FOOTER_LAYOUT:
                ((FooterViewHolder)holder).configureViews();
                break;
        }
//        setFadeAnimation(holder.itemView);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    public void hideFooter() {
        isAllItemLoaded = true;
    }

    @Override
    public int getItemCount() {
        return restaurantInfoModelList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == restaurantInfoModelList.size() ? FOOTER_LAYOUT : ITEM_LAYOUT;
    }
}
