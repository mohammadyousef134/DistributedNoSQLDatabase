package com.example.nosql_database_management_system.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

import java.util.Map;

@AllArgsConstructor
@Getter
public class CollectionSchema {
    private Map<String, SchemaField> fields;

    public SchemaField getField(String name) {
        return fields.get(name);
    }
    public JSONObject toJSON() {
        JSONObject schemaJson = new JSONObject();
        JSONObject fieldsJson = new JSONObject();

        for (Map.Entry<String, SchemaField> entry : fields.entrySet()) {
            fieldsJson.put(entry.getKey(), entry.getValue().toJSON());
        }

        schemaJson.put("fields", fieldsJson);
        return schemaJson;
    }

}
