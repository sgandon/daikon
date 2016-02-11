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

/**
 * A schema that can be used by the component service
 */
public interface DataSchema {

    public MakoElement getRoot();

    public MakoElement setRoot(MakoElement root);

    /**
     * Returns a string that is the serialized form of this Schema. Use {@link DataSchemaFactory#fromSerialized(String)}
     * to materialize the object from the string.
     */
    public String toSerialized();

}
