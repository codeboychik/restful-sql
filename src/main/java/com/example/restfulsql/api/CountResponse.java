package com.example.restfulsql.api;

public class CountResponse {

    private final long count;

    public CountResponse(long count) {
        this.count = count;
    }

    public long getCount() {
        return count;
    }
}
