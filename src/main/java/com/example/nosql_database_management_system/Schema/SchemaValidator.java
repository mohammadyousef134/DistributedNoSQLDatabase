package com.example.nosql_database_management_system.Schema;

import com.example.nosql_database_management_system.Enum.SchemaFieldType;
import com.example.nosql_database_management_system.exception.ValidationException;
import com.example.nosql_database_management_system.model.CollectionSchema;
import com.example.nosql_database_management_system.model.SchemaField;
import org.json.JSONObject;

import java.util.UUID;

public class SchemaValidator {

    public static JSONObject validate(JSONObject doc, CollectionSchema schema) {
        JSONObject newDocument = new JSONObject();
        newDocument.put("id", UUID.randomUUID());

        for (String key : schema.getFields().keySet()) {

            SchemaField field = schema.getField(key);
            // existing
            if (!doc.has(key)) {
                if (!field.isNullable()) {
                    throw new ValidationException("Missing field: " + key);
                }
                newDocument.put(key, JSONObject.NULL);
                continue;
            }

            Object value = doc.get(key);
            if (value == JSONObject.NULL) {
                if (!field.isNullable()) {
                    throw new ValidationException("Null not allowed: " + key);
                }
                newDocument.put(key, JSONObject.NULL);
                continue;
            }

            if (!isValidType(value, field.getType())) {
                throw new ValidationException("Invalid type for: " + key);
            }

            newDocument.put(key, value);
        }
        return newDocument;
    }

    private static boolean isValidType(Object value, SchemaFieldType type) {

        if (type.equals(SchemaFieldType.STRING)) {
            return value instanceof String;
        }
        else if (type.equals(SchemaFieldType.CHAR)) {
            return value instanceof String && ((String) value).length() == 1;
        }
        else if (type.equals(SchemaFieldType.BOOLEAN)) {
            return value instanceof Boolean;
        }
        else if (type.equals(SchemaFieldType.INT)) {
            return value instanceof Integer || (value instanceof Double && ((Double) value) % 1 == 0);
        }
        else if (type.equals(SchemaFieldType.DOUBLE)) {
            return value instanceof Double || value instanceof Float;
        }
        else {
            return false;
        }
    }

}
