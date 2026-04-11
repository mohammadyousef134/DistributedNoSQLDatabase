package com.example.nosql_database_management_system.DAO.indexing;

import lombok.AllArgsConstructor;

import java.util.Objects;


@AllArgsConstructor
public class PropertyIndex {

    private final String dbName;
    private final String colName;
    private final String field;
    private final String value;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PropertyIndex)) return false;
        PropertyIndex that = (PropertyIndex) o;
        return Objects.equals(dbName, that.dbName) &&
                Objects.equals(colName, that.colName) &&
                Objects.equals(field, that.field) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dbName, colName, field, value);
    }
}