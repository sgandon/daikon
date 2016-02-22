package org.talend.daikon.schema.avro;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.UUID;

import org.apache.avro.SchemaBuilder;
import org.junit.Test;

/**
 * Unit tests for {AvroRegistry}.
 */
public class AvroRegistryTest {

    @Test
    public void testBasic() {
        // Some basic cases for the AvroRegistry.
        AvroRegistry registry = new AvroRegistry();
        assertThat(registry.inferSchema(UUID.fromString("12341234-1234-1234-1234-123412341234")),
                is(SchemaBuilder.builder().stringType()));
    }

    @Test
    public void testSerializability() {
        // TODO: Adding shared stuff to the AvroRegistry should be serializable so it can be used across different
        // worker threads on different machines.
    }
}
