package org.talend.daikon.avro;

import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for {DynamicTypeFactory}.
 */
public class DynamicTypeFactoryTest {

    @Test
    public void testIsDynamic() {
        Schema s = SchemaBuilder.record("dynamic").fields().name("field1").type().nullType().noDefault().endRecord();
        assertFalse(DynamicTypeFactory.isDynamic(s.getField("field1").schema()));
        s = SchemaBuilder.record("dynamic").fields().name("field1").type(DynamicTypeFactory.getDynamic()).noDefault().endRecord();
        assertTrue(DynamicTypeFactory.isDynamic(s.getField("field1").schema()));
    }

    @Test
    public void testGetDynamic() {
        Schema dynamic = DynamicTypeFactory.getDynamic();
        assertThat(dynamic.getType(), is(Schema.Type.NULL));
        assertThat(dynamic.getProp(LogicalType.LOGICAL_TYPE_PROP), is(SchemaConstants.LOGICAL_DYNAMIC));
    }
}
