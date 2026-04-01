package com.example.nosql_database_management_system.model;
import com.example.nosql_database_management_system.Enum.SchemaFieldType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SchemaField {
    private SchemaFieldType type;
    boolean nullable;
}
