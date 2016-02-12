package org.talend.daikon.schema.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.IndexedRecord;

/**
 * 
 */
public class DatumRegistry {

    private static Map<Class<?>, AvroConverter<?, ?>> mRegistry = new HashMap<>();

    private static Map<Class<?>, Factory<? extends IndexedRecordFacadeFactory<?>>> mFacadeFactories = new HashMap<>();

    // Set up the registry with common converters.
    static {
        // Other Avro types that require more information in the schema.
        // RECORD,ENUM,ARRAY,MAP,UNION,FIXED,NULL

        // The Avro types that have direct mappings to primitive types.
        // STRING,BYTES,INT,LONG,FLOAT,DOUBLE,BOOLEAN
        registerBasicClass(String.class, Schema.create(Type.STRING));
        registerBasicClass(ByteBuffer.class, Schema.create(Type.BYTES));
        registerBasicClass(Integer.class, Schema.create(Type.INT));
        registerBasicClass(Long.class, Schema.create(Type.LONG));
        registerBasicClass(Float.class, Schema.create(Type.FLOAT));
        registerBasicClass(Double.class, Schema.create(Type.DOUBLE));
        registerBasicClass(Boolean.class, Schema.create(Type.BOOLEAN));

        // Other classes that have well-defined Avro representations.
        register(BigDecimal.class, new BigDecimalConverter());
        register(BigInteger.class, new BigIntegerConverter());
        register(Date.class, new DateConverter());
        register(InetAddress.class, new InetAddressConverter());
        register(UUID.class, new UUIDConverter());

    }

    private static <T> void registerBasicClass(Class<T> specificClass, Schema schema) {
        register(specificClass, new Unconverted<T>(specificClass, schema));
        registerFacadeFactory(specificClass, new PrimitiveAsIndexedRecordFacadeFactoryFactory<T>(specificClass, schema));
    }

    public static <T> void register(Class<T> specificClass, AvroConverter<T, ?> converter) {
        mRegistry.put(specificClass, converter);
    }

    @SuppressWarnings("unchecked")
    public static <T> AvroConverter<T, ?> getConverter(Class<T> specificClass) {
        return (AvroConverter<T, ?>) mRegistry.get(specificClass);
    }

    public static <T> void registerFacadeFactory(Class<T> datumClass,
            Class<? extends IndexedRecordFacadeFactory<? super T>> facadeFactoryClass) {
        mFacadeFactories.put(datumClass, new NewInstanceFactory<T>(datumClass, facadeFactoryClass));
    }

    protected static <T> void registerFacadeFactory(Class<T> datumClass,
            Factory<IndexedRecordFacadeFactory<T>> facadeFactoryFactory) {
        mFacadeFactories.put(datumClass, facadeFactoryFactory);
    }

    public static <DatumT> IndexedRecordFacadeFactory<? super DatumT> getFacadeFactory(Class<DatumT> datumClass) {
        if (datumClass == null)
            return null;

        // This is guaranteed to be correctly typed if it exists, because of the register methods.
        @SuppressWarnings("unchecked")
        Factory<? extends IndexedRecordFacadeFactory<DatumT>> irfff = (Factory<? extends IndexedRecordFacadeFactory<DatumT>>) mFacadeFactories
                .get(datumClass);
        if (irfff != null) {
            return irfff.create();
        }

        // Attempt to go up through the super classes.
        IndexedRecordFacadeFactory<? super DatumT> sirfff = getFacadeFactory(datumClass.getSuperclass());
        if (sirfff != null) {
            // TODO(rskraba): Should we register that this is the best match?
            return sirfff;
        }

        for (Class<?> c : datumClass.getInterfaces()) {
            Class<? super DatumT> iClass = (Class<? super DatumT>) c;
            IndexedRecordFacadeFactory<? super DatumT> iirfff = getFacadeFactory(iClass);
            if (iirfff != null) {
                // TODO(rskraba): Should we register that this is the best match?
                return iirfff;
            }
        }

        if (IndexedRecord.class.isAssignableFrom(datumClass)) {
            @SuppressWarnings("unchecked")
            IndexedRecordFacadeFactory<DatumT> irff = (IndexedRecordFacadeFactory<DatumT>) new UnconvertedIndexedRecordFacadeFactory<IndexedRecord>();
            return irff;
        }

        return null;
    }

    /**
     * 
     */
    public static class NewInstanceFactory<T> implements Factory<IndexedRecordFacadeFactory<T>> {

        private Class<T> mDatumClass;

        private Class<? extends IndexedRecordFacadeFactory<? super T>> mFactoryClass;

        public NewInstanceFactory(Class<T> datumClass, Class<? extends IndexedRecordFacadeFactory<? super T>> factoryClass) {
            mDatumClass = datumClass;
            mFactoryClass = factoryClass;
        }

        @Override
        public IndexedRecordFacadeFactory<T> create() {
            try {
                Constructor<?>[] cs = mFactoryClass.getConstructors();
                for (Constructor<?> c : cs) {
                    if (c.getParameterTypes().length == 1) {
                        // TODO: ensure that the first parameter is a specific class of the correct type.
                        @SuppressWarnings("unchecked")
                        Constructor<? extends IndexedRecordFacadeFactory<T>> cc = (Constructor<? extends IndexedRecordFacadeFactory<T>>) c;
                        return cc.newInstance(mDatumClass);
                    }
                }
                // Otherwise use the empty constructor.
                return (IndexedRecordFacadeFactory<T>) mFactoryClass.newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * This creates facade factories that wrap types that are already Avro compatible into an {@link IndexedRecord} with
     * a single column containing that Avro object.
     */
    private static class PrimitiveAsIndexedRecordFacadeFactoryFactory<T> implements Factory<IndexedRecordFacadeFactory<T>> {

        private final Class<T> mSpecificClass;

        private final Schema mSchema;

        private final InternalFactory mFactory;

        public PrimitiveAsIndexedRecordFacadeFactoryFactory(Class<T> specificClass, Schema schema) {
            mSpecificClass = specificClass;
            mSchema = SchemaBuilder.record(specificClass.getSimpleName() + "Record") //
                    .fields().name("col1").type(schema).noDefault() //
                    .endRecord();
            mFactory = new InternalFactory();
        }

        @Override
        public IndexedRecordFacadeFactory<T> create() {
            return mFactory;
        }

        private class InternalFactory implements IndexedRecordFacadeFactory<T> {

            @Override
            public Schema getSchema() {
                return mSchema;
            }

            @Override
            public void setSchema(Schema s) {
                throw new UnsupportedOperationException("Unmodifiable schema: " + mSchema);
            }

            public Class<T> getSpecificClass() {
                return mSpecificClass;
            }

            @Override
            public IndexedRecord createFacade(T value) {
                return new PrimitiveAsIndexedRecordFacade(value);
            }
        }

        private class PrimitiveAsIndexedRecordFacade implements IndexedRecord {

            private final T mValue;

            private PrimitiveAsIndexedRecordFacade(T value) {
                mValue = value;
            }

            @Override
            public Schema getSchema() {
                return mSchema;
            }

            public Object get(int i) {
                return mValue;
            }

            @Override
            public void put(int i, Object v) {
                throw new UnsupportedOperationException("Unmodifiable facade: " + mSchema);
            }
        }
    }

    /** Passes through an indexed record without modification. */
    public static class UnconvertedIndexedRecordFacadeFactory<T extends IndexedRecord> implements IndexedRecordFacadeFactory<T> {

        private Schema mSchema;

        public UnconvertedIndexedRecordFacadeFactory() {
        }

        @Override
        public Schema getSchema() {
            return mSchema;
        }

        @Override
        public void setSchema(Schema schema) {
            mSchema = schema;
        }

        public Class<T> getSpecificClass() {
            return null;
        }

        @Override
        public IndexedRecord createFacade(T value) {
            return value;
        }
    }

    // public static <T> IndexedRecord getAsIndexedRecord(T value) {
    // if (value == null)
    // return null;
    // if (value instanceof IndexedRecord)
    // return (IndexedRecord) value;
    // // This is guaranteed to be the correct type, enforced by the register method.
    // @SuppressWarnings("unchecked")
    // AvroIndexedRecordConverter<T, ?> wrapper = (AvroIndexedRecordConverter<T, ?>)
    // mRecordWrappers.get(value.getClass());
    // return wrapper.convertToAvro(value);
    // }

    // public static final class UnconvertedIndexRecord<T extends IndexedRecord> implements AvroIndexedRecordFacade<T,
    // T> {
    //
    // private final Class<T> mSpecificClass;
    //
    // private Schema mSchema;
    //
    // public UnconvertedIndexRecord(Class<T> specificClass) {
    // mSpecificClass = specificClass;
    // }
    //
    // @Override
    // public Schema getAvroSchema() {
    // return mSchema;
    // }
    //
    // @Override
    // public void setAvroSchema(Schema s) {
    // mSchema = s;
    // }
    //
    // @Override
    // public Class<T> getSpecificClass() {
    // return mSpecificClass;
    // }
    //
    // @Override
    // public T convertFromAvro(T value) {
    // return value;
    // }
    //
    // @Override
    // public T convertToAvro(T value) {
    // return value;
    // }
    // }

    /**
     * Utility class that doesn't perform any conversion.
     */
    public static class Unconverted<T> implements AvroConverter<T, T> {

        private final Class<T> mSpecificClass;

        private final Schema mSchema;

        public Unconverted(Class<T> specificClass, Schema schema) {
            mSpecificClass = specificClass;
            mSchema = schema;
        }

        @Override
        public Schema getAvroSchema() {
            return mSchema;
        }

        @Override
        public Class<T> getSpecificClass() {
            return mSpecificClass;
        }

        @Override
        public T convertFromAvro(T value) {
            return value;
        }

        @Override
        public T convertToAvro(T value) {
            return value;
        }
    }

    /** TODO(rskraba): Use the logical type correctly here! */
    public static class BigDecimalConverter implements AvroConverter<BigDecimal, String> {

        @Override
        public Schema getAvroSchema() {
            return Schema.create(Type.LONG);
        }

        @Override
        public Class<BigDecimal> getSpecificClass() {
            return BigDecimal.class;
        }

        @Override
        public BigDecimal convertFromAvro(String value) {
            return value == null ? null : new BigDecimal(value);
        }

        @Override
        public String convertToAvro(BigDecimal value) {
            return value == null ? null : value.toString();
        }
    }

    /** TODO(rskraba): Use the logical type correctly here! */
    public static class BigIntegerConverter implements AvroConverter<BigInteger, String> {

        @Override
        public Schema getAvroSchema() {
            return Schema.create(Type.LONG);
        }

        @Override
        public Class<BigInteger> getSpecificClass() {
            return BigInteger.class;
        }

        @Override
        public BigInteger convertFromAvro(String value) {
            return value == null ? null : new BigInteger(value);
        }

        @Override
        public String convertToAvro(BigInteger value) {
            return value == null ? null : value.toString();
        }
    }

    /** TODO(rskraba): Use the logical type here? Or just leave it as long for processing...? */
    public static class DateConverter implements AvroConverter<Date, Long> {

        @Override
        public Schema getAvroSchema() {
            return Schema.create(Type.LONG);
        }

        @Override
        public Class<Date> getSpecificClass() {
            return Date.class;
        }

        @Override
        public Date convertFromAvro(Long value) {
            return value == null ? null : new Date(value);
        }

        @Override
        public Long convertToAvro(Date value) {
            return value == null ? null : value.getTime();
        }

    }

    public static class InetAddressConverter implements AvroConverter<InetAddress, byte[]> {

        @Override
        public Schema getAvroSchema() {
            return Schema.createUnion( //
                    Arrays.asList(Schema.createFixed(null, null, null, 4), //
                            Schema.createFixed(null, null, null, 16)));
        }

        @Override
        public Class<InetAddress> getSpecificClass() {
            return InetAddress.class;
        }

        @Override
        public InetAddress convertFromAvro(byte[] value) {
            try {
                return value == null ? null : InetAddress.getByAddress(value);
            } catch (UnknownHostException e) {
                // TODO(rskraba): Error handling
                throw new RuntimeException(e);
            }
        }

        @Override
        public byte[] convertToAvro(InetAddress value) {
            return value == null ? null : value.getAddress();
        }

    }

    public static class UUIDConverter implements AvroConverter<UUID, String> {

        @Override
        public Schema getAvroSchema() {
            return Schema.create(Type.STRING);
        }

        @Override
        public Class<UUID> getSpecificClass() {
            return UUID.class;
        }

        @Override
        public UUID convertFromAvro(String value) {
            return value == null ? null : UUID.fromString(value);
        }

        @Override
        public String convertToAvro(UUID value) {
            return value == null ? null : value.toString();
        }
    }
}
