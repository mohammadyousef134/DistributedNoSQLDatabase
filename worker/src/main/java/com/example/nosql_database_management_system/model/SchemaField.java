package com.example.nosql_database_management_system.model;
import com.example.nosql_database_management_system.Enum.SchemaFieldType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.json.JSONObject;

@AllArgsConstructor
@Getter
public class SchemaField {
    private SchemaFieldType type;
    boolean nullable;

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("type", type.name()); // enum → String
        obj.put("nullable", nullable);
        return obj;
    }
}
