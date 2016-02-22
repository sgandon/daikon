package org.talend.daikon.schema.avro.util;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.IndexedRecord;
import org.talend.daikon.schema.type.IndexedRecordFacadeFactory;

/**
 * A factory for creating {@link IndexedRecord} facades from any object. The output records always have a single column
 * with a predefined {@link Schema} from the constructor, with the given value.
 * 
 * This is a useful mechanism to permit single, primitive values to be passed between components, or to pass non-record
 * values between components (if the top-level data is an ARRAY for example).
 */
public class SingleColumnIndexedRecordFacadeFactory<DatumT> implements
        IndexedRecordFacadeFactory<DatumT, SingleColumnIndexedRecordFacadeFactory.PrimitiveAsIndexedRecordFacade<DatumT>> {

    private final Class<DatumT> mDatumClass;

    /** The schema of the {@link IndexedRecord}s that this factory generates. */
    private final Schema mSchema;

    /**
     * @param datumClass The class of the instances that this factory knows how to create IndexedRecords for. This must
     * be an Avro-compatible class since it's instances will be directly inserted into the output records without
     * validation.
     * @param schema The schema that the datum class can be converted to. This will be the schema of the single field in
     * the generated {@link IndexedRecord}s.
     */
    public SingleColumnIndexedRecordFacadeFactory(Class<DatumT> datumClass, Schema schema) {
        this.mDatumClass = datumClass;
        this.mSchema = SchemaBuilder.record(datumClass.getSimpleName() + "Record") //
                .fields().name("col1").type(schema).noDefault() //
                .endRecord();
    }

    @Override
    public Class<DatumT> getDatumClass() {
        return mDatumClass;
    }

    @Override
    public Schema getSchema() {
        return mSchema;
    }

    @Override
    public void setSchema(Schema s) {
        throw new UnmodifiableFacadeException();
    }

    @Override
    public PrimitiveAsIndexedRecordFacade<DatumT> convertToAvro(DatumT value) {
        return new PrimitiveAsIndexedRecordFacade<DatumT>(mSchema, value);
    }

    @Override
    public DatumT convertToDatum(PrimitiveAsIndexedRecordFacade<DatumT> value) {
        return value.mValue;
    }

    /**
     * An {@link IndexedRecord} facade that can only contain a single value.
     * 
     * @param <T>
     */
    public static class PrimitiveAsIndexedRecordFacade<T> implements IndexedRecord {

        private final T mValue;

        private final Schema mSchema;

        private PrimitiveAsIndexedRecordFacade(Schema schema, T value) {
            mSchema = schema;
            mValue = value;
        }

        @Override
        public Schema getSchema() {
            return mSchema;
        }

        @Override
        public T get(int i) {
            return mValue;
        }

        @Override
        public void put(int i, Object v) {
            throw new UnmodifiableFacadeException();
        }
    }
}