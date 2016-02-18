package org.talend.daikon.schema.type;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.apache.avro.Schema;
import org.apache.avro.generic.IndexedRecord;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DatumRegistryTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testWrapPrimitivesAsSingleColumn() {
        IndexedRecord ir = DatumRegistry.getFacadeFactory(String.class).convertToAvro("Hello");

        Schema s = ir.getSchema();
        assertThat(s.getType(), is(Schema.Type.RECORD));
        assertThat(s.getFields(), hasSize(1));
        assertThat(s.getFields().get(0).schema().getType(), is(Schema.Type.STRING));
    }

    @Test
    public void testUnconvertedIndexRecords() {
        // Just create a fake object that is known to be an index record.
        IndexedRecord in = DatumRegistry.getFacadeFactory(String.class).convertToAvro("Hello");

        // Get a facade factory for it.
        IndexedRecordFacadeFactory<?, ?> irff = DatumRegistry.getFacadeFactory(in.getClass());
        assertThat(irff, not(nullValue()));

        @SuppressWarnings({ "rawtypes", "unchecked" })
        IndexedRecord out = (IndexedRecord) ((IndexedRecordFacadeFactory) irff).convertToAvro(in);
        assertThat(out, sameInstance(in));
    }

    @Test
    public void testUnknownDatum() {
        // Create a datum that is a unknown class.
        Object datum = new Object() {
        };

        // thrown.expect(RuntimeException.class);
        IndexedRecordFacadeFactory<?, ?> irff = DatumRegistry.getFacadeFactory(datum.getClass());
        assertThat(irff, nullValue());
    }

}
