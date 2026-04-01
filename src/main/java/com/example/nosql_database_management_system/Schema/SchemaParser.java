package com.example.nosql_database_management_system.Schema;

import com.example.nosql_database_management_system.Enum.SchemaFieldType;
import com.example.nosql_database_management_system.model.CollectionSchema;
import com.example.nosql_database_management_system.model.SchemaField;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SchemaParser {
    public static CollectionSchema parse(JSONObject jsonSchema) {
        Map<String, SchemaField> fields = new HashMap<>();

        for(String key : jsonSchema.keySet()) {
            JSONObject fieldObj = jsonSchema.getJSONObject(key);
            // type
            String typeStr = fieldObj.getString("type");

            SchemaFieldType type = SchemaFieldType.valueOf(typeStr.toUpperCase());

            // nullable
            boolean nullable = fieldObj.getBoolean("nullable");

            // new field
            SchemaField field = new SchemaField(type, nullable);
            fields.put(key, field);
        }
        return new CollectionSchema(fields);
    }
}
