package org.talend.daikon.schema.type;

/**
 * Interface for callbacks that convert between application-specific (external) types and known types, including reading
 * and writing from container objects.
 * 
 * No component is required to know the application-specific types of another component, but all components should be
 * able to manage known types.
 */
public interface ExternalBaseType<SpecificT, KnownT, ReadContainerT, WriteContainerT> {

    public SpecificT readValue(ReadContainerT obj, String key);

    public void writeValue(WriteContainerT app, String key, SpecificT value);

    public SpecificT convertFromKnown(KnownT value);

    public KnownT convertToKnown(SpecificT value);
}
