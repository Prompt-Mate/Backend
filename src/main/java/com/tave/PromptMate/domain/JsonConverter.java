package com.tave.PromptMate.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class JsonConverter<T> implements AttributeConverter<T, String> {

    private static final ObjectMapper om = new ObjectMapper();
    private final Class<T> clazz;

    public JsonConverter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String convertToDatabaseColumn(T attribute) {
        if (attribute == null) return null;
        try {
            return om.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON serialize error", e);
        }
    }

    @Override
    public T convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            return om.readValue(dbData, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON deserialize error", e);
        }
    }
}