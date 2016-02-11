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

public class SchemaTest {

    @Test
    public void testSerializeSchema() {
        DataSchema s = DataSchemaFactory.newSchema();
        MakoElement root = s.setRoot(DataSchemaFactory.newSchemaElement("root"));
        root.addChild(DataSchemaFactory.newSchemaElement("c1"));
        String ser = s.toSerialized();

        DataSchema s2 = DataSchemaFactory.fromSerialized(ser);
        assertEquals("root", s2.getRoot().getName());
        assertNotNull(s2.getRoot().getChild("c1"));
    }

}
