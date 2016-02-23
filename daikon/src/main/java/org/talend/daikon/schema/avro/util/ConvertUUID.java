package org.talend.daikon.schema.avro.util;

import java.util.UUID;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.talend.daikon.schema.avro.AvroConverter;

public class ConvertUUID implements AvroConverter<UUID, String> {

    @Override
    public Schema getSchema() {
        return Schema.create(Type.STRING);
    }

    @Override
    public Class<UUID> getDatumClass() {
        return UUID.class;
    }

    @Override
    public UUID convertToDatum(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    @Override
    public String convertToAvro(UUID value) {
        return value == null ? null : value.toString();
    }
}