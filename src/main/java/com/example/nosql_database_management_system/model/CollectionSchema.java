package com.example.nosql_database_management_system.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class CollectionSchema {
    private Map<String, SchemaField> fields;

    public SchemaField getField(String name) {
        return fields.get(name);
    }
}
