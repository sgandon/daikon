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

import org.apache.avro.Schema;
import org.talend.daikon.schema.SchemaElement;

/**
 * Constants that can be used as keys in an Avro {@link Schema} properties in order to remain feature-equivalent to the
 * existing Talend 6 IMetadataTable.
 * 
 * Values that are null are always omitted from the properties.
 */
public interface Talend6SchemaConstants {

    public final static String TALEND6_NAME = "talend6.name"; //$NON-NLS-1$

    public final static String TALEND6_TITLE = "talend6.title"; //$NON-NLS-1$

    /** Value is {@link SchemaElement.Type#name()}. */
    public final static String TALEND6_TYPE = "talend6.type"; //$NON-NLS-1$

    /** Omitted if -1. */
    public final static String TALEND6_SIZE = "talend6.size"; //$NON-NLS-1$

    // by default, always true if size == -1???
    public final static String TALEND6_IS_UNBOUNDED = "talend6.sizeUnbounded"; //$NON-NLS-1$

    /** Omitted if 0. */
    public final static String TALEND6_OCCUR_MIN_TIMES = "talend6.occurMinTimes"; //$NON-NLS-1$

    /** Omitted if 0. */
    public final static String TALEND6_OCCUR_MAX_TIMES = "talend6.occurMaxTimes"; //$NON-NLS-1$

    // by default, *always* true if occurMinTimes > 0???
    public final static String TALEND6_IS_REQUIRED = "talend6.required"; //$NON-NLS-1$

    /** Omitted if 0. */
    public final static String TALEND6_PRECISION = "talend6.precision"; //$NON-NLS-1$

    public final static String TALEND6_PATTERN = "talend6.pattern"; //$NON-NLS-1$

    public final static String TALEND6_DEFAULT_VALUE = "talend6.defaultValue"; //$NON-NLS-1$

    public final static String TALEND6_IS_NULLABLE = "talend6.nullable"; //$NON-NLS-1$

    public final static String TALEND6_ENUM_CLASS = "talend6.enumClass"; //$NON-NLS-1$

    public final static String TALEND6_POSSIBLE_VALUES = "talend6.possibleValues"; //$NON-NLS-1$
}
