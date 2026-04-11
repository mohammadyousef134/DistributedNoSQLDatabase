package com.example.nosql_database_management_system.model;

import com.example.nosql_database_management_system.Enum.SchemaFieldType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

import java.util.HashMap;
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

    public static CollectionSchema fromJSON(JSONObject json) {

        Map<String, SchemaField> fields = new HashMap<>();

        JSONObject fieldsObject = json.getJSONObject("fields");

        for (String key : fieldsObject.keySet()) {

            JSONObject fieldObj = fieldsObject.getJSONObject(key);

            String typeStr = fieldObj.getString("type");
            SchemaFieldType type = SchemaFieldType.valueOf(typeStr);

            boolean nullable = fieldObj.getBoolean("nullable");

            SchemaField field = new SchemaField(type, nullable);

            fields.put(key, field);
        }

        return new CollectionSchema(fields);
    }

}
