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
package org.talend.daikon.properties.service;

import org.talend.daikon.properties.Properties;
import org.apache.avro.Schema;

/**
 * A design-time interface to the repository to allow {@link Properties} to be stored.
 *
 * FIXME - this is probably at the wrong level and will move
 */
public interface Repository<T extends Properties> {

    /**
     * Adds the specified {@link Properties} into the design environment.
     *
     * @param properties the {@code Properties} object to add.
     * @param name the name of the collection of properties
     * @param repositoryLocation the repositoryLocation under which this item should be stored (using the name
     * parameter).
     * @param schema an optional schema to be added at this location.
     * @return repositoryLocation, a String containing the location where this object was stored.
     */
    public String storeProperties(T properties, String name, String repositoryLocation, Schema schema);

}
