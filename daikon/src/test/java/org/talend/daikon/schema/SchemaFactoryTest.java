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

import static org.junit.Assert.*;

import org.junit.Test;
import org.talend.daikon.schema.DataSchema;
import org.talend.daikon.schema.MakoElement;
import org.talend.daikon.schema.DataSchemaFactory;
import org.talend.daikon.schema.MakoElement.Type;
import org.talend.daikon.schema.internal.DataSchemaImpl;

/**
 * created by pbailly on 5 Nov 2015 Detailled comment
 *
 */
public class SchemaFactoryTest {

    @Test
    public void testNewSchema() {
        assertEquals(DataSchemaImpl.class, DataSchemaFactory.newSchema().getClass());
    }

    @Test
    public void testNewSchemaElement() {
        MakoElement element = DataSchemaFactory.newSchemaElement(Type.DECIMAL, "schemaElement");
        assertEquals("schemaElement", element.getName());
        assertNull(element.getDefaultValue());
        assertNull(element.getDefaultValue());
        assertNull(element.getTitle());
        assertEquals(Type.DECIMAL, element.getType());
    }

    @Test
    // TUP-3898 Generic codegen for a Schema does not probably handle escaped quotes.
    public void testQuoteDeserialize() {
        MakoElement element = DataSchemaFactory.newSchemaElement(Type.DATETIME, "dateTime");
        element.setPattern("\"pattern\"");
        DataSchema schema = DataSchemaFactory.newSchema();
        schema.setRoot(element);
        String ser = schema.toSerialized();
        System.out.println(ser);

        String quoted = "\"" + ser.replace("\\\"", "\\\\\"").replace("\"", "\\\"") + "\"";
        System.out.println(quoted);
        assertTrue(quoted.contains("\\\\\\\"pattern"));

    }
}
