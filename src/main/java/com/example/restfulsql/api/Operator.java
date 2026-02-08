package com.example.restfulsql.api;

import java.util.Arrays;

public enum Operator {
    EQ("="),
    NE("<>"),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    LIKE("LIKE"),
    IN("IN", true);

    private final String sql;
    private final boolean expectsCollection;

    Operator(String sql) {
        this(sql, false);
    }

    Operator(String sql, boolean expectsCollection) {
        this.sql = sql;
        this.expectsCollection = expectsCollection;
    }

    public String getSql() {
        return sql;
    }

    public boolean expectsCollection() {
        return expectsCollection;
    }

    public static Operator fromName(String name) {
        return Arrays.stream(values())
            .filter(operator -> operator.name().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow();
    }
}
