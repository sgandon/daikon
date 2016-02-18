package org.talend.daikon.schema.type;

/**
 * Callback for code that knows how to read a specific type from a container.
 */
public interface ContainerReaderByName<ReadContainerT, T> {

    public T readValue(ReadContainerT obj, String name);
}
