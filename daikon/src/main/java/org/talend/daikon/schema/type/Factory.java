package org.talend.daikon.schema.type;

/**
 * Implements the factory pattern.
 */
public interface Factory<T> {

    T create();
}
