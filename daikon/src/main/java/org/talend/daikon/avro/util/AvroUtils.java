package org.talend.daikon.avro.util;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.SchemaBuilder;
import org.talend.daikon.avro.SchemaConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper methods for accessing Avro {@link Schema} and Avro-compatible objects.
 */
public class AvroUtils {

    /**
     * @return Given any Schema, return whether the null value is possible.
     */
    public static boolean isNullable(Schema schema) {
        if (schema.getType() == Schema.Type.NULL) {
            return true;
        }
        if (schema.getType() == Schema.Type.UNION) {
            for (Schema unionType : schema.getTypes()) {
                if (unionType.getType() == Schema.Type.NULL) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return Given any Schema, return the schema as a {@link Schema.Type#UNION} containing {@link Schema.Type#NULL} as
     * an option.
     */
    public static Schema wrapAsNullable(Schema schema) {
        if (schema.getType() == Schema.Type.UNION) {
            // TODO(rskraba): The nullable schema can be a singleton?
            List<Schema> unionTypes = schema.getTypes();
            if (unionTypes.contains(Schema.create(Schema.Type.NULL))) {
                return schema;
            }

            ArrayList<Schema> typesWithNullable = new ArrayList<>(unionTypes);
            typesWithNullable.add(Schema.create(Schema.Type.NULL));
            return Schema.createUnion(typesWithNullable);
        }
        return SchemaBuilder.nullable().type(schema);
    }

    /**
     * @return Given any Schema, removes {@link Schema.Type#NULL} as an option.
     */
    public static Schema unwrapIfNullable(Schema schema) {
        // If this is a simple type wrapped in a nullable, then just use the
        // non-nullable
        if (schema.getType() == Schema.Type.UNION) {
            List<Schema> unionTypes = schema.getTypes();
            // The majority of cases can be unwrapped by removing the union with null.
            if (unionTypes.size() == 2) {
                if (unionTypes.get(0).getType().equals(Schema.Type.NULL)) {
                    return unionTypes.get(1);
                } else if (unionTypes.get(1).getType().equals(Schema.Type.NULL)) {
                    return unionTypes.get(0);
                }
            } else if (unionTypes.contains(Schema.create(Type.NULL))) {
                ArrayList<Schema> typesWithoutNullable = new ArrayList<>(unionTypes);
                typesWithoutNullable.remove(Schema.create(Schema.Type.NULL));
                return Schema.createUnion(typesWithoutNullable);
            }
        }
        return schema;
    }

    public static Map<String, Schema.Field> makeFieldMap(Schema schema) {
        Map<String, Schema.Field> map = new HashMap<>();
        for (Schema.Field field : schema.getFields()) {
            map.put(field.name(), field);
        }
        return map;
    }

    /**
     * Schema don't support overwrite property's value, so have to clone it then put the new value
     *
     * @param schema
     * @param key
     * @param value
     * @return schema with the new value for the property: key
     */
    public static Schema setProperty(Schema schema, String key, String value) {
        Schema newSchema = schema;
        if (schema.getProp(key) != null) {
            if (schema.getType() == Type.RECORD) {
                newSchema = Schema.createRecord(schema.getName(), schema.getDoc(), schema.getNamespace(), schema.isError());
                List<Schema.Field> copyFieldList = new ArrayList<>();
                for (Schema.Field se : schema.getFields()) {
                    copyFieldList.add(new Schema.Field(se.name(), se.schema(), se.doc(), se.defaultVal()));
                }
                newSchema.setFields(copyFieldList);
                Map<String, Object> props = schema.getObjectProps();
                for (String propKey : props.keySet()) {
                    if (propKey.equals(key)) {
                        newSchema.addProp(key, value);
                    } else {
                        newSchema.addProp(propKey, props.get(propKey));
                    }
                }
            } else {//FIXME for other type
                throw new RuntimeException("Not support this type " + newSchema.getType() + " now, need to implement");
            }
        } else {
            schema.addProp(key, value);
        }
        return newSchema;
    }

    /**
     * @param schema
     * @return the value of the property include-all-fields, false if there is no this property
     */
    public static boolean isIncludeAllFields(Schema schema) {
        String prop = schema.getProp(SchemaConstants.INCLUDE_ALL_FIELDS);
        if (prop == null || !Boolean.valueOf(prop)) {
            return false;
        }
        return true;
    }

    /**
     * @param schema
     * @param value
     * @return set boolean value for the property include-all-fields
     */
    public static Schema setIncludeAllFields(Schema schema, boolean value) {
        return setProperty(schema, SchemaConstants.INCLUDE_ALL_FIELDS, String.valueOf(value));
    }

}
