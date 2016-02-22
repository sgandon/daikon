package org.talend.daikon.schema.avro.util;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.junit.Test;

/**
 * Unit tests for {AvroUtils}.
 */
public class AvroUtilsTest {

    @Test
    public void testWrapAsNullable() {
        // An already nullable schema is passed through.
        Schema alreadyNullable = SchemaBuilder.builder().nullable().booleanType();
        assertThat(AvroUtils.wrapAsNullable(alreadyNullable), sameInstance(alreadyNullable));

        // A non-nullable schema is converted to a union.
        Schema unnullable = SchemaBuilder.builder().booleanType();
        Schema expected = SchemaBuilder.builder().unionOf().type(unnullable).and().nullType().endUnion();
        assertThat(AvroUtils.wrapAsNullable(unnullable), is(expected));

        // A union with three elements, with the middle one null is passed through.
        Schema mixWithNullable = SchemaBuilder.builder().unionOf().booleanType().and().nullType().and().intType().endUnion();
        assertThat(AvroUtils.wrapAsNullable(mixWithNullable), sameInstance(mixWithNullable));

        // A union with three elements, none nullable, has a nullable added. to the end.
        Schema mixWithNoNullable = SchemaBuilder.builder().unionOf().booleanType().and().doubleType().and().intType().endUnion();
        expected = SchemaBuilder.builder().unionOf().booleanType().and().doubleType().and().intType().and().nullType().endUnion();
        assertThat(AvroUtils.wrapAsNullable(mixWithNoNullable), is(expected));
    }

    @Test
    public void testUnwrapIfNullable() {
        // An already nullable schema is passed through.
        Schema alreadyNullable = SchemaBuilder.builder().nullable().booleanType();
        Schema expected = SchemaBuilder.builder().booleanType();
        assertThat(AvroUtils.unwrapIfNullable(alreadyNullable), is(expected));

        // A non-nullable schema is passed through.
        Schema unnullable = SchemaBuilder.builder().booleanType();
        assertThat(AvroUtils.unwrapIfNullable(unnullable), sameInstance(unnullable));

        // A union with three elements, with the middle one null is passed through.
        Schema mixWithNullable = SchemaBuilder.builder().unionOf().booleanType().and().nullType().and().intType().endUnion();
        expected = SchemaBuilder.builder().unionOf().booleanType().and().intType().endUnion();
        assertThat(AvroUtils.unwrapIfNullable(mixWithNullable), is(expected));

        // A union with three elements, none nullable, is passed through.
        Schema mixWithNoNullable = SchemaBuilder.builder().unionOf().booleanType().and().doubleType().and().intType().endUnion();
        assertThat(AvroUtils.unwrapIfNullable(mixWithNoNullable), sameInstance(mixWithNoNullable));
    }

}
