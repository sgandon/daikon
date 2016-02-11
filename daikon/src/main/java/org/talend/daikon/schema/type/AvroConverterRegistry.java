package org.talend.daikon.schema.type;

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
import org.apache.avro.generic.IndexedRecord;

/**
 * 
 */
public class AvroConverterRegistry {

    private Map<Class<?>, AvroConverter<?, ?>> mRegistry = new HashMap<>();

    private static Map<Class<?>, AvroConverter<?, ? extends IndexedRecord>> mRecordWrappers = new HashMap<>();

    {
        // Other Avro types that require more information in the schema.
        // RECORD,ENUM,ARRAY,MAP,UNION,FIXED,NULL

        // The Avro types that have direct mappings to primitive types.
        // STRING,BYTES,INT,LONG,FLOAT,DOUBLE,BOOLEAN
        registerSimpleConverter(String.class, Schema.create(Type.STRING));
        registerSimpleConverter(ByteBuffer.class, Schema.create(Type.BYTES));
        registerSimpleConverter(Integer.class, Schema.create(Type.INT));
        registerSimpleConverter(Long.class, Schema.create(Type.LONG));
        registerSimpleConverter(Float.class, Schema.create(Type.FLOAT));
        registerSimpleConverter(Double.class, Schema.create(Type.DOUBLE));
        registerSimpleConverter(Boolean.class, Schema.create(Type.BOOLEAN));

        // Other classes that have well defined Avro representations.
        register(BigDecimal.class, new BigDecimalConverter());
        register(BigInteger.class, new BigIntegerConverter());
        register(Date.class, new DateConverter());
        register(InetAddress.class, new InetAddressConverter());
        register(UUID.class, new UUIDConverter());

    }

    private <T> void registerSimpleConverter(Class<T> specificClass, Schema schema) {
        mRegistry.put(specificClass, new Unconverted<T>(specificClass, schema));
    }

    public <T> void register(Class<T> specificClass, AvroConverter<T, ?> converter) {
        mRegistry.put(specificClass, converter);
    }

    @SuppressWarnings("unchecked")
    public <T> AvroConverter<T, ?> getConverter(Class<T> specificClass) {
        return (AvroConverter<T, ?>) mRegistry.get(specificClass);
    }

    public static <T> void registerRecordWrapper(Class<T> containerClass, AvroConverter<T, ? extends IndexedRecord> converter) {
        mRecordWrappers.put(containerClass, converter);
    }

    public static <T> IndexedRecord getAsIndexedRecord(T value) {
        if (value == null)
            return null;
        if (value instanceof IndexedRecord)
            return (IndexedRecord) value;
        @SuppressWarnings("unchecked")
        AvroConverter<T, ? extends IndexedRecord> wrapper = (AvroConverter<T, ? extends IndexedRecord>) mRecordWrappers.get(value
                .getClass());
        return wrapper.convertToAvro(value);
    }

    /**
     * Utility class that doesn't perform any conversion.
     */
    public static final class Unconverted<T> implements AvroConverter<T, T> {

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
