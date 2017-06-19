package com.vijay.castle.zomatosearch.utilities;

/**
 * Created by vijay on 6/9/17.
 */

public class SendCustomEvent {
    public static final int EVENT_DB_CHANGE = 1;

    private int typeOfEvent;
    private Object data;

    public SendCustomEvent(int typeOfEvent, Object data) {
        this.typeOfEvent = typeOfEvent;
        this.data = data;
    }

    public int getTypeOfEvent() {
        return typeOfEvent;
    }

    public Object getData() {
        return data;
    }
}
