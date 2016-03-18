package org.talend.daikon.avro;

import org.apache.avro.LogicalType;
import org.apache.avro.LogicalTypes;
import org.apache.avro.Schema;

/**
 * Created by bchen on 16-3-18.
 */
public class DynamicTypeFactory {
    static {
        //TODO where can do a global registry
        LogicalTypes.register(SchemaConstants.LOGICAL_DYNAMIC, new DynamicLogicalTypeFactory());
    }

    public static Schema getDynamic() {
        Schema s = Schema.create(Schema.Type.NULL);
        new DynamicLogicalType().addToSchema(s);
        return s;
    }

    public static boolean isDynamic(Schema schema) {
        //schema.getLogicalType() will return null if you don't registry custom logicalType before Serialization, refer to LogicalTypes.register
        return SchemaConstants.LOGICAL_DYNAMIC.equals(schema.getProp(LogicalType.LOGICAL_TYPE_PROP));
    }

    public static class DynamicLogicalType extends LogicalType {


        public DynamicLogicalType() {
            super(SchemaConstants.LOGICAL_DYNAMIC);
        }

        @Override
        public void validate(Schema schema) {
            super.validate(schema);
            if (schema.getType() != Schema.Type.NULL) {
                throw new IllegalArgumentException(
                        "Dynamic can only be used with an underlying null type");
            }
        }

    }

    public static class DynamicLogicalTypeFactory implements LogicalTypes.LogicalTypeFactory {

        @Override
        public LogicalType fromSchema(Schema schema) {
            return new DynamicLogicalType();
        }
    }
}



