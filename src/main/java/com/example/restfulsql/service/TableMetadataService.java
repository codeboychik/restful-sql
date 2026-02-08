package com.example.restfulsql.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TableMetadataService {

    private final DataSource dataSource;
    private final String configuredSchema;

    public TableMetadataService(DataSource dataSource, @Value("${app.db.schema:}") String configuredSchema) {
        this.dataSource = dataSource;
        this.configuredSchema = configuredSchema;
    }

    public boolean tableOrViewExists(String name) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String schema = resolveSchema(connection);
            try (ResultSet tables = metaData.getTables(connection.getCatalog(), schema, "%", new String[] {"TABLE", "VIEW"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    if (tableName != null && tableName.equalsIgnoreCase(name)) {
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to read table metadata", ex);
        }
    }

    public Set<String> loadColumnNames(String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String schema = resolveSchema(connection);
            Set<String> columns = new HashSet<>();
            try (ResultSet resultSet = metaData.getColumns(connection.getCatalog(), schema, "%", null)) {
                while (resultSet.next()) {
                    String currentTable = resultSet.getString("TABLE_NAME");
                    if (currentTable == null || !currentTable.equalsIgnoreCase(tableName)) {
                        continue;
                    }
                    columns.add(resultSet.getString("COLUMN_NAME").toLowerCase(Locale.ROOT));
                }
            }
            return columns;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to read column metadata", ex);
        }
    }

    private String resolveSchema(Connection connection) throws SQLException {
        if (configuredSchema != null && !configuredSchema.isBlank()) {
            return configuredSchema;
        }
        String schema = connection.getSchema();
        if (schema == null || schema.isBlank()) {
            return null;
        }
        return schema;
    }
}
