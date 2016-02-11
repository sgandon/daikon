// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.daikon.schema;

import org.talend.daikon.schema.internal.DataSchemaElement;
import org.talend.daikon.schema.internal.DataSchemaImpl;

import com.cedarsoftware.util.io.JsonReader;
import org.talend.daikon.schema.type.ExternalBaseType;
import org.talend.daikon.schema.type.TypeMapping;

/**
 * Make objects that are related to the schemas.
 */
@Deprecated
public class DataSchemaFactory {

    public static DataSchema newSchema() {
        return new DataSchemaImpl();
    }

    public static MakoElement newSchemaElement(MakoElement.Type type, String name) {
        MakoElement se = newSchemaElement(name);
        se.setType(type);
        return se;
    }

    public static MakoElement newSchemaElement(String name) {
        MakoElement se = new DataSchemaElement();
        se.setName(name);
        return se;
    }

    public static DataSchemaElement newDataSchemaElement(String familyName, String name, Class<? extends ExternalBaseType> appType) {
        DataSchemaElement se = new DataSchemaElement();
        se.setAppColName(name);
        se.setAppColType(appType);
        se.setName(name);
        se.setType(TypeMapping.getDefaultTalendType(familyName, appType));
        return se;
    }

    /**
     * Returns a {@link DataSchema} object materialized from the serialized string. See
     * {@link DataSchema#toSerialized()}.
     */
    public static DataSchema fromSerialized(String serialized) {
        DataSchema deser = null;
        ClassLoader originalContextClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(DataSchemaFactory.class.getClassLoader());
            deser = (DataSchema) JsonReader.jsonToJava(serialized);
        } finally {
            Thread.currentThread().setContextClassLoader(originalContextClassLoader);
        }
        return deser;

    }

}
