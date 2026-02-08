package com.example.restfulsql.service;

import com.example.restfulsql.api.Condition;
import com.example.restfulsql.api.CountRequest;
import com.example.restfulsql.api.Operator;
import com.example.restfulsql.web.InvalidQueryException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CountService {

    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TableMetadataService tableMetadataService;

    public CountService(NamedParameterJdbcTemplate jdbcTemplate, TableMetadataService tableMetadataService) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableMetadataService = tableMetadataService;
    }

    public long countRows(CountRequest request) {
        String table = normalizeIdentifier(request.getTable(), "table");
        if (!tableMetadataService.tableOrViewExists(table)) {
            throw new InvalidQueryException("Unknown table or view: " + table);
        }

        Set<String> columns = tableMetadataService.loadColumnNames(table);
        StringBuilder sql = new StringBuilder("select count(*) from ").append(table);
        Map<String, Object> params = new HashMap<>();
        List<String> clauses = new ArrayList<>();

        int index = 0;
        for (Condition condition : request.getConditions()) {
            String column = normalizeIdentifier(condition.getColumn(), "column");
            if (!columns.contains(column.toLowerCase(Locale.ROOT))) {
                throw new InvalidQueryException("Unknown column: " + column);
            }

            Operator operator = condition.getOperator();
            String paramName = "p" + index++;
            JsonNode value = condition.getValue();
            if (operator.expectsCollection()) {
                if (!value.isArray()) {
                    throw new InvalidQueryException("Operator " + operator.name() + " expects a list value");
                }
                List<Object> values = new ArrayList<>();
                value.forEach(node -> values.add(readValue(node)));
                clauses.add(column + " " + operator.getSql() + " (:" + paramName + ")");
                params.put(paramName, values);
            } else {
                clauses.add(column + " " + operator.getSql() + " :" + paramName);
                params.put(paramName, readValue(value));
            }
        }

        if (!clauses.isEmpty()) {
            sql.append(" where ").append(String.join(" and ", clauses));
        }

        Long result = jdbcTemplate.queryForObject(sql.toString(), params, Long.class);
        return result == null ? 0L : result;
    }

    private String normalizeIdentifier(String identifier, String label) {
        if (identifier == null || !IDENTIFIER_PATTERN.matcher(identifier).matches()) {
            throw new InvalidQueryException("Invalid " + label + " name");
        }
        return identifier;
    }

    private Object readValue(JsonNode node) {
        if (node.isNumber()) {
            return node.numberValue();
        }
        if (node.isBoolean()) {
            return node.booleanValue();
        }
        if (node.isNull()) {
            return null;
        }
        return node.asText();
    }
}
