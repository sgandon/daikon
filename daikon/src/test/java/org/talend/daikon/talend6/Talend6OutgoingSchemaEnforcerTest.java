package org.talend.daikon.talend6;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.IndexedRecord;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.talend.daikon.avro.DynamicTypeFactory;
import org.talend.daikon.avro.util.SingleColumnIndexedRecordAdapterFactory;

import java.util.Date;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {Talend6SchemaOutputEnforcer}.
 */
@SuppressWarnings("nls")
public class Talend6OutgoingSchemaEnforcerTest {

    /**
     * An actual record that a component would like to be emitted, which may or may not contain enriched schema
     * information.
     */
    private IndexedRecord componentRecord;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        Schema componentSchema = SchemaBuilder.builder().record("Record").fields() //
                .name("id").type().intType().noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("age").type().intType().noDefault() //
                .name("valid").type().booleanType().noDefault() //
                .name("address").type().stringType().noDefault() //
                .name("comment").type().stringType().noDefault() //
                .endRecord();
        componentRecord = new GenericData.Record(componentSchema);
        componentRecord.put(0, 1);
        componentRecord.put(1, "User");
        componentRecord.put(2, 100);
        componentRecord.put(3, true);
        componentRecord.put(4, "Main Street");
        componentRecord.put(5, "This is a record with six columns.");
    }

    @Test
    public void testDynamicColumn_ByIndex_DynamicColumnAtStart() {
        // The expected schema after enforcement.
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("dyn").type(DynamicTypeFactory.getDynamic()).noDefault() //
                .name("out1").type().intType().noDefault() //
                .name("out2").type().stringType().noDefault() //
                .name("out3").type().intType().noDefault() //
                .endRecord();

        Talend6OutgoingSchemaEnforcer enforcer = new Talend6OutgoingSchemaEnforcer(talend6Schema, true);

        enforcer.setWrapped(componentRecord);

        // Check the resolved fields.
        assertThat(enforcer.get(1), is((Object) true));
        assertThat(enforcer.get(2), is((Object) "Main Street"));
        assertThat(enforcer.get(3), is((Object) "This is a record with six columns."));

        // Check the dynamic field.
        assertThat(enforcer.get(0), instanceOf(Map.class));
        Map<?, ?> unresolved = (Map<?, ?>) enforcer.get(0);
        assertThat(unresolved.keySet(), containsInAnyOrder((Object) "id", "name", "age"));
        assertThat(unresolved, hasEntry((Object) "id", (Object) 1));
        assertThat(unresolved, hasEntry((Object) "age", (Object) 100));
        assertThat(unresolved, hasEntry((Object) "name", (Object) "User"));

        Schema talend6SchemaWithoutDynamic = enforcer.getSchemaWithoutDynamic();
        assertThat(talend6SchemaWithoutDynamic.getFields(), hasSize(3));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(0).name(), is("out1"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(1).name(), is("out2"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(2).name(), is("out3"));
    }

    @Test
    public void testDynamicColumn_ByIndex_DynamicColumnAtMiddle() {
        // The expected schema after enforcement.
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("out1").type().intType().noDefault() //
                .name("dyn").type(DynamicTypeFactory.getDynamic()).noDefault() //
                .name("out2").type().stringType().noDefault() //
                .name("out3").type().intType().noDefault() //
                .endRecord();

        Talend6OutgoingSchemaEnforcer enforcer = new Talend6OutgoingSchemaEnforcer(talend6Schema, true);

        enforcer.setWrapped(componentRecord);

        // Check the resolved fields.
        assertThat(enforcer.get(0), is((Object) 1));
        assertThat(enforcer.get(2), is((Object) "Main Street"));
        assertThat(enforcer.get(3), is((Object) "This is a record with six columns."));

        // Check the dynamic field.
        assertThat(enforcer.get(1), instanceOf(Map.class));
        Map<?, ?> unresolved = (Map<?, ?>) enforcer.get(1);
        assertThat(unresolved.keySet(), containsInAnyOrder((Object) "name", "age", "valid"));
        assertThat(unresolved, hasEntry((Object) "name", (Object) "User"));
        assertThat(unresolved, hasEntry((Object) "age", (Object) 100));
        assertThat(unresolved, hasEntry((Object) "valid", (Object) true));

        Schema talend6SchemaWithoutDynamic = enforcer.getSchemaWithoutDynamic();
        assertThat(talend6SchemaWithoutDynamic.getFields(), hasSize(3));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(0).name(), is("out1"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(1).name(), is("out2"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(2).name(), is("out3"));
    }

    @Test
    public void testDynamicColumn_ByIndex_DynamicColumnAtEnd() {
        // The expected schema after enforcement.
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("out1").type().intType().noDefault() //
                .name("out2").type().stringType().noDefault() //
                .name("out3").type().intType().noDefault() //
                .name("dyn").type(DynamicTypeFactory.getDynamic()).noDefault() //
                .endRecord();

        Talend6OutgoingSchemaEnforcer enforcer = new Talend6OutgoingSchemaEnforcer(talend6Schema, true);

        enforcer.setWrapped(componentRecord);

        // Check the resolved fields.
        assertThat(enforcer.get(0), is((Object) 1));
        assertThat(enforcer.get(1), is((Object) "User"));
        assertThat(enforcer.get(2), is((Object) 100));

        // Check the dynamic field.
        assertThat(enforcer.get(3), instanceOf(Map.class));
        Map<?, ?> unresolved = (Map<?, ?>) enforcer.get(3);
        assertThat(unresolved.keySet(), containsInAnyOrder((Object) "valid", "address", "comment"));
        assertThat(unresolved, hasEntry((Object) "valid", (Object) true));
        assertThat(unresolved, hasEntry((Object) "address", (Object) "Main Street"));
        assertThat(unresolved, hasEntry((Object) "comment", (Object) "This is a record with six columns."));

        Schema talend6SchemaWithoutDynamic = enforcer.getSchemaWithoutDynamic();
        assertThat(talend6SchemaWithoutDynamic.getFields(), hasSize(3));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(0).name(), is("out1"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(1).name(), is("out2"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(2).name(), is("out3"));
    }

    @Test
    public void testDynamicColumn_ByName_DynamicColumnAtStart() {
        // The expected schema after enforcement.
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("dyn").type(DynamicTypeFactory.getDynamic()).noDefault() //
                .name("id").type().intType().noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("age").type().intType().noDefault() //
                .endRecord();
        Talend6OutgoingSchemaEnforcer enforcer = new Talend6OutgoingSchemaEnforcer(talend6Schema, false);

        enforcer.setWrapped(componentRecord);

        // Check the resolved fields.
        assertThat(enforcer.get(1), is((Object) 1));
        assertThat(enforcer.get(2), is((Object) "User"));
        assertThat(enforcer.get(3), is((Object) 100));

        // Check the dynamic field.
        assertThat(enforcer.get(0), instanceOf(Map.class));
        Map<?, ?> unresolved = (Map<?, ?>) enforcer.get(0);
        assertThat(unresolved.keySet(), containsInAnyOrder((Object) "valid", "address", "comment"));
        assertThat(unresolved, hasEntry((Object) "valid", (Object) true));
        assertThat(unresolved, hasEntry((Object) "address", (Object) "Main Street"));
        assertThat(unresolved, hasEntry((Object) "comment", (Object) "This is a record with six columns."));

        Schema talend6SchemaWithoutDynamic = enforcer.getSchemaWithoutDynamic();
        assertThat(talend6SchemaWithoutDynamic.getFields(), hasSize(3));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(0).name(), is("id"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(1).name(), is("name"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(2).name(), is("age"));
    }

    @Test
    public void testDynamicColumn_ByName_DynamicColumnAtMiddle() {
        // The expected schema after enforcement.
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("id").type().intType().noDefault() //
                .name("dyn").type(DynamicTypeFactory.getDynamic()).noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("age").type().intType().noDefault() //
                .endRecord();
        Talend6OutgoingSchemaEnforcer enforcer = new Talend6OutgoingSchemaEnforcer(talend6Schema, false);

        enforcer.setWrapped(componentRecord);

        // Check the resolved fields.
        assertThat(enforcer.get(0), is((Object) 1));
        assertThat(enforcer.get(2), is((Object) "User"));
        assertThat(enforcer.get(3), is((Object) 100));

        // Check the dynamic field.
        assertThat(enforcer.get(1), instanceOf(Map.class));
        Map<?, ?> unresolved = (Map<?, ?>) enforcer.get(1);
        assertThat(unresolved.keySet(), containsInAnyOrder((Object) "valid", "address", "comment"));
        assertThat(unresolved, hasEntry((Object) "valid", (Object) true));
        assertThat(unresolved, hasEntry((Object) "address", (Object) "Main Street"));
        assertThat(unresolved, hasEntry((Object) "comment", (Object) "This is a record with six columns."));

        Schema talend6SchemaWithoutDynamic = enforcer.getSchemaWithoutDynamic();
        assertThat(talend6SchemaWithoutDynamic.getFields(), hasSize(3));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(0).name(), is("id"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(1).name(), is("name"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(2).name(), is("age"));
    }

    @Test
    public void testDynamicColumn_ByName_DynamicColumnAtEnd() {
        // The expected schema after enforcement.
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("id").type().intType().noDefault() //
                .name("name").type().stringType().noDefault() //
                .name("age").type().intType().noDefault() //
                .name("dyn").type(DynamicTypeFactory.getDynamic()).noDefault() //
                .endRecord();
        Talend6OutgoingSchemaEnforcer enforcer = new Talend6OutgoingSchemaEnforcer(talend6Schema, false);

        enforcer.setWrapped(componentRecord);

        // Check the resolved fields.
        assertThat(enforcer.get(0), is((Object) 1));
        assertThat(enforcer.get(1), is((Object) "User"));
        assertThat(enforcer.get(2), is((Object) 100));

        // Check the dynamic field.
        assertThat(enforcer.get(3), instanceOf(Map.class));
        Map<?, ?> unresolved = (Map<?, ?>) enforcer.get(3);
        assertThat(unresolved.keySet(), containsInAnyOrder((Object) "valid", "address", "comment"));
        assertThat(unresolved, hasEntry((Object) "valid", (Object) true));
        assertThat(unresolved, hasEntry((Object) "address", (Object) "Main Street"));
        assertThat(unresolved, hasEntry((Object) "comment", (Object) "This is a record with six columns."));

        Schema talend6SchemaWithoutDynamic = enforcer.getSchemaWithoutDynamic();
        assertThat(talend6SchemaWithoutDynamic.getFields(), hasSize(3));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(0).name(), is("id"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(1).name(), is("name"));
        assertThat(talend6SchemaWithoutDynamic.getFields().get(2).name(), is("age"));
    }

    @Test
    public void testDynamicColumn_getOutOfBounds() {
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("id").type().intType().noDefault() //
                .endRecord();
        Talend6OutgoingSchemaEnforcer enforcer = new Talend6OutgoingSchemaEnforcer(talend6Schema, false);
        enforcer.setWrapped(componentRecord);

        assertThat(enforcer.get(0), is((Object) 1));

        thrown.expect(ArrayIndexOutOfBoundsException.class);
        enforcer.get(1); // Only one field available.
    }

    @Test
    public void testWrappedSingleColumnIndexedRecord() {
        // The expected schema after enforcement.
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("name").type().stringType().noDefault() //
                .endRecord();

        Talend6OutgoingSchemaEnforcer enforcer = new Talend6OutgoingSchemaEnforcer(talend6Schema, false);

        SingleColumnIndexedRecordAdapterFactory<String> factory = new SingleColumnIndexedRecordAdapterFactory<>(String.class,
                Schema.create(Schema.Type.STRING));

        enforcer.setWrapped(factory.convertToAvro("one"));

        assertThat(enforcer.get(0), is((Object) "one"));
    }

    @Test
    public void testValueConversion_toDate() {
        // The expected schema after enforcement.
        Schema talend6Schema = SchemaBuilder.builder().record("Record").fields() //
                .name("d").prop(Talend6SchemaConstants.TALEND6_COLUMN_TALEND_TYPE, //
                        "id_Date").type().longType().noDefault() //
                .endRecord();

        // The enforcer to test.
        Talend6OutgoingSchemaEnforcer enforcer = new Talend6OutgoingSchemaEnforcer(talend6Schema, false);

        // Use this factory to create a one-column indexed record.
        SingleColumnIndexedRecordAdapterFactory<Long> factory = new SingleColumnIndexedRecordAdapterFactory<>(Long.class,
                Schema.create(Schema.Type.LONG));
        IndexedRecord testData = factory.convertToAvro(1L);

        enforcer.setWrapped(testData);

        assertThat(enforcer.get(0), instanceOf(Date.class));
        assertThat(enforcer.get(0), is((Object) new Date(1L)));
    }
}
