package com.vijay.castle.zomatosearch.models;

/**
 * Created by vijay on 6/9/17.
 */

public class RestaurantInfoModel {
    private String name;
    private Integer id;
    private int average_cost_for_two;
    private String thumb;
    private String featured_image;
    private String photos_url;
    private String cuisines;
    private UserRating user_rating;
    private Location location;
    private boolean isBookMarked;

    private class UserRating {
        private String aggregate_rating;
        private String rating_text;
        private String rating_color;
        private String votes;

        String getAggregate_rating() {
            return aggregate_rating;
        }

        String getRating_text() {
            return rating_text;
        }

        String getRating_color() {
            return rating_color;
        }

        String getVotes() {
            return votes;
        }
    }

    private class Location {
        private String city;
        private String locality_verbose;

        public String getCity() {
            return city;
        }

        public String getLocality_verbose() {
            return locality_verbose;
        }
    }

    public String getName() {
        return name;
    }

    public int getAverage_cost_for_two() {
        return average_cost_for_two;
    }

    public String getThumb() {
        return thumb;
    }

    String getFeatured_image() {
        return featured_image;
    }

    public String getPhotos_url() {
        return photos_url;
    }

    public String getCuisines() {
        return cuisines;
    }

    private UserRating getUser_rating() {
        return user_rating;
    }

    private Location getLocation() {
        return location;
    }

    public String getLocality() {
        return location.getLocality_verbose();
    }

    public String getCity() {
        return location.getCity();
    }

    public String getAggregate_rating() {
        return getUser_rating().aggregate_rating;
    }

    public String getRating_text() {
        return getUser_rating().rating_text;
    }

    public String getRating_color() {
        return "#"+getUser_rating().rating_color;
    }

    public String getVotes() {
        return getUser_rating().votes;
    }

    public Integer getId() {
        return id;
    }

    public boolean isBookMarked() {
        return isBookMarked;
    }

    public void setBookMarked(boolean bookMarked) {
        isBookMarked = bookMarked;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RestaurantInfoModel) {
            RestaurantInfoModel x = (RestaurantInfoModel) obj;
            return x.id.equals(id);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }
}
