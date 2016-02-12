package org.talend.daikon.schema.type;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;

/**
 * 
 */
public interface IndexedRecordFacadeFactory<SpecificT> {

    /**
     * @return the Avro Schema that this knows how to convert from and to. This must be a Type.RECORD, and might be
     * inferred from the specific record, known in advance or unknown.
     */
    Schema getSchema();

    /**
     * If the Avro Schema is known, this can be called to prevent it from being inferred from the specific incoming
     * record.
     */
    void setSchema(Schema schema);

    /** @return The specific class that this factory knows how to process. */
    public Class<SpecificT> getSpecificClass();

    IndexedRecord createFacade(SpecificT value);
}
