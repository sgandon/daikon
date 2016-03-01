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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.talend.daikon.talend6.Talend6SchemaConstants.*;

import org.apache.avro.Schema;
import org.junit.Test;
import org.talend.daikon.properties.Property;
import org.talend.daikon.properties.PropertyFactory;
import org.talend.daikon.schema.SchemaElement;
import org.talend.daikon.schema.internal.SchemaImpl;

/**
 * Tests for the {@link Talend6ComponentToAvroUtil} utility.
 */
@SuppressWarnings({ "nls", "deprecation" })
public class Talend6ComponentToAvroUtilTest {

    @Test
    public void testBasic() {
        // Create a very basic component schema with a very basic string column.
        org.talend.daikon.schema.Schema cmpSchema = new SchemaImpl();
        cmpSchema.setRoot(PropertyFactory.newProperty("Root").setType(SchemaElement.Type.SCHEMA));
        cmpSchema.getRoot().addChild(new Property("column1"));

        Schema avroSchema = Talend6ComponentToAvroUtil.toAvro(cmpSchema);

        assertThat(avroSchema.getType(), is(Schema.Type.RECORD));
        assertThat(avroSchema.getName(), is("Root"));
        assertThat(avroSchema.getProp(TALEND6_NAME), is("Root"));
        assertThat(avroSchema.getProp(TALEND6_TITLE), nullValue());
        assertThat(avroSchema.getProp(TALEND6_TYPE), is("SCHEMA"));
        assertThat(avroSchema.getProp(TALEND6_SIZE), nullValue());
        assertThat(avroSchema.getProp(TALEND6_IS_UNBOUNDED), is("true"));
        assertThat(avroSchema.getProp(TALEND6_OCCUR_MIN_TIMES), nullValue());
        assertThat(avroSchema.getProp(TALEND6_OCCUR_MAX_TIMES), nullValue());
        assertThat(avroSchema.getProp(TALEND6_IS_REQUIRED), is("false"));
        assertThat(avroSchema.getProp(TALEND6_PRECISION), nullValue());
        assertThat(avroSchema.getProp(TALEND6_PATTERN), nullValue());
        assertThat(avroSchema.getProp(TALEND6_DEFAULT_VALUE), nullValue());
        assertThat(avroSchema.getProp(TALEND6_IS_NULLABLE), is("false"));
        assertThat(avroSchema.getProp(TALEND6_ENUM_CLASS), nullValue());
        assertThat(avroSchema.getProp(TALEND6_POSSIBLE_VALUES), nullValue());

        assertThat(avroSchema.getFields(), hasSize(1));
        {
            Schema.Field field = avroSchema.getFields().get(0);
            assertThat(field.name(), is("column1"));
            assertThat(field.getProp(TALEND6_NAME), is("column1"));
            assertThat(field.getProp(TALEND6_TITLE), nullValue());
            assertThat(field.getProp(TALEND6_TYPE), is("STRING"));
            assertThat(field.getProp(TALEND6_SIZE), nullValue());
            assertThat(field.getProp(TALEND6_IS_UNBOUNDED), is("true"));
            assertThat(field.getProp(TALEND6_OCCUR_MIN_TIMES), nullValue());
            assertThat(field.getProp(TALEND6_OCCUR_MAX_TIMES), nullValue());
            assertThat(field.getProp(TALEND6_IS_REQUIRED), is("false"));
            assertThat(field.getProp(TALEND6_PRECISION), nullValue());
            assertThat(field.getProp(TALEND6_PATTERN), nullValue());
            assertThat(field.getProp(TALEND6_DEFAULT_VALUE), nullValue());
            assertThat(field.getProp(TALEND6_IS_NULLABLE), is("false"));
            assertThat(field.getProp(TALEND6_ENUM_CLASS), nullValue());
            assertThat(field.getProp(TALEND6_POSSIBLE_VALUES), nullValue());
        }
        System.out.println(avroSchema);
    }
}
