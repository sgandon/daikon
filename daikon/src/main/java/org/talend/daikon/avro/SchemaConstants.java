package org.talend.daikon.avro;

import org.apache.avro.Schema.Type;

/**
 * Constants to be used to augment the Avro schema
 */
public class SchemaConstants {

    /**
     * A dynamic schema field. The Avro type for this is {@link Type.RECORD}
     */
    public static final String LOGICAL_DYNAMIC = "dynamic";

}
