// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.talend6;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.SchemaBuilder.BaseFieldTypeBuilder;
import org.apache.avro.SchemaBuilder.FieldAssembler;
import org.apache.avro.SchemaBuilder.FieldBuilder;
import org.apache.avro.SchemaBuilder.PropBuilder;
import org.apache.avro.SchemaBuilder.RecordBuilder;
import org.talend.daikon.schema.SchemaElement;

/**
 * Turns SchemaElement objects into Avro {@link Schema} objects.
 * 
 * Parts of the component framework describe the metadata of data passed between components at run-time using the same
 * descriptive API as the component properties. This utility provides a mechanism to turn these objects into Avro
 * Schemas annotated with properties, so that the representation should be reversable and feature-equivalent.
 * 
 * During the run-time of a component, the SchemaElement should not be used to describe data.
 * 
 * @deprecated Where possible, this should be pushed up into the Studio so that the conversion is from IMetadataTable
 * directly to the necessary Avro Schema that the component needs.
 */
@Deprecated
public class Talend6ComponentToAvroUtil implements Talend6SchemaConstants {

    /**
     * Utility method for taking a daikon Schema and returning a standard Avro Schema.
     */
    @Deprecated
    public static Schema toAvro(org.talend.daikon.schema.Schema inSchema) {
        RecordBuilder<Schema> builder = SchemaBuilder.builder().record(inSchema.getRoot().getName());
        copySchemaElementProperties(builder, inSchema.getRoot());

        FieldAssembler<Schema> fa = builder.fields();
        for (SchemaElement field : inSchema.getRoot().getChildren()) {
            fa = buildInto(fa, field);
        }
        return fa.endRecord();
    }

    private static FieldAssembler<Schema> buildInto(FieldAssembler<Schema> fa, SchemaElement field) {
        FieldBuilder<Schema> fb = fa.name(field.getName());
        copySchemaElementProperties(fb, field);
        BaseFieldTypeBuilder<Schema> ftb = field.isNullable() ? fb.type() : fb.type().nullable();
        switch (field.getType()) {
        case BOOLEAN:
            return ftb.booleanType().booleanDefault(Boolean.parseBoolean(field.getDefaultValue()));
        case BYTE_ARRAY:
            return ftb.bytesType().bytesDefault(field.getDefaultValue());
        case DATE:
            // TODO
            throw new UnsupportedOperationException("Unrecognized SchemaElement.Type"); //$NON-NLS-1$
        case DATETIME:
            // TODO
            throw new UnsupportedOperationException("Unrecognized SchemaElement.Type"); //$NON-NLS-1$
        case DECIMAL:
            // TODO
            throw new UnsupportedOperationException("Unrecognized SchemaElement.Type"); //$NON-NLS-1$
        case DOUBLE:
            return ftb.doubleType().doubleDefault(Double.parseDouble(field.getDefaultValue()));
        case DYNAMIC:
            // Dynamic fields are declared to be bytes, but will actually turn into a Map<String, Object> when passed
            // through a Talend6SchemaOutputEnforcer.
            return ftb.bytesType().bytesDefault(field.getDefaultValue());
        case ENUM:
            // TODO
            throw new UnsupportedOperationException("Unrecognized SchemaElement.Type"); //$NON-NLS-1$
        case FLOAT:
            return ftb.floatType().floatDefault(Float.parseFloat(field.getDefaultValue()));
        case GROUP:
            // TODO
            throw new UnsupportedOperationException("Unrecognized SchemaElement.Type"); //$NON-NLS-1$
        case INT:
            return ftb.intType().intDefault(Integer.parseInt(field.getDefaultValue()));
        case SCHEMA:
            // TODO
            // This should never occur.
            throw new UnsupportedOperationException("Can't parse SchemaElement.Type.SCHEMA in Schema"); //$NON-NLS-1$
        case STRING:
            return ftb.stringType().stringDefault(field.getDefaultValue());
        default:
            // This should never occur.
            throw new UnsupportedOperationException("Unrecognized SchemaElement.Type"); //$NON-NLS-1$
        }
    }

    /**
     * Copy all of the information from the SchemaElement in the form of Properties into the Avro Schema object.
     * 
     * @param builder Any builder capable of taking key/value in the form of strings.
     * @param schemaElement The element to copy information from.
     * @return the instance of the builder passed in.
     */
    public static <T extends PropBuilder<T>> PropBuilder<T> copySchemaElementProperties(PropBuilder<T> builder,
            SchemaElement schemaElement) {
        if (schemaElement.getName() != null) {
            builder.prop(TALEND6_NAME, schemaElement.getName());
        }
        if (schemaElement.getTitle() != null) {
            builder.prop(TALEND6_TITLE, schemaElement.getTitle());
        }
        if (schemaElement.getType() != null) {
            builder.prop(TALEND6_TYPE, schemaElement.getType().name());
        }
        if (schemaElement.getSize() != -1) {
            builder.prop(TALEND6_SIZE, String.valueOf(schemaElement.getSize()));
        }
        builder.prop(TALEND6_IS_UNBOUNDED, String.valueOf(schemaElement.isSizeUnbounded())); // true if size == -1
        if (schemaElement.getOccurMinTimes() != 0) {
            builder.prop(TALEND6_OCCUR_MIN_TIMES, String.valueOf(schemaElement.getOccurMinTimes()));
        }
        if (schemaElement.getOccurMaxTimes() != 0) {
            builder.prop(TALEND6_OCCUR_MAX_TIMES, String.valueOf(schemaElement.getOccurMaxTimes()));
        }
        builder.prop(TALEND6_IS_REQUIRED, String.valueOf(schemaElement.isRequired())); // true if minTimes > 0
        if (schemaElement.getPrecision() != 0) {
            builder.prop(TALEND6_PRECISION, String.valueOf(schemaElement.getPrecision()));
        }
        if (schemaElement.getPattern() != null) {
            builder.prop(TALEND6_PATTERN, schemaElement.getPattern());
        }
        if (schemaElement.getDefaultValue() != null) {
            builder.prop(TALEND6_DEFAULT_VALUE, schemaElement.getDefaultValue());
        }
        builder.prop(TALEND6_IS_NULLABLE, String.valueOf(schemaElement.isNullable()));
        if (schemaElement.getEnumClass() != null) {
            builder.prop(TALEND6_ENUM_CLASS, String.valueOf(schemaElement.getEnumClass()));
        }
        if (schemaElement.getPossibleValues() != null) {
            builder.prop(TALEND6_POSSIBLE_VALUES, String.valueOf(schemaElement.getPossibleValues()));
        }
        return builder;
    }

}
