package org.talend.daikon.schema.type;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

/**
 * 
 */
public interface IndexedRecordFacadeFactory<SpecificT, IndexedRecordT extends IndexedRecord> extends
        AvroConverter<SpecificT, IndexedRecordT> {

    /**
     * @return the Avro Schema for the {@link IndexedRecord} that this knows how to create. This must be a Type.RECORD,
     * and might be inferred from the specific record, known in advance or unknown. If the schema is null, it should be
     * inferred from the incoming data.
     */
    Schema getSchema();

    /**
     * If the Avro Schema is known, this can be called to prevent it from being inferred from the specific incoming
     * record. This can be set to null to re-infer the schema from the next incoming datum.
     */
    void setSchema(Schema schema);

}
