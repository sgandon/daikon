package org.talend.daikon.schema.type;

import org.apache.avro.Schema;

/**
 * Callback for code that knows how to convert between a specific type and an Avro-compatible type.
 */
public interface AvroConverter<SpecificT, AvroT> {

    /** @return the Avro Schema that is compatible with the AvroT type. */
    public Schema getSchema();

    /** @return the class of the specific type that this converter knows how to convert from. */
    public Class<SpecificT> getSpecificClass();

    /** Takes the avro type and converts to the specific type. */
    public SpecificT convertFromAvro(AvroT value);

    /** Takes the specific type and converts to the avro type. */
    public AvroT convertToAvro(SpecificT value);
}
