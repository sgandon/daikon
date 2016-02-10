package org.talend.daikon.schema.type;

/**
 * Interface for callbacks thatconvert between application-specific (external) types and known types, including reading
 * and writing from container objects.
 * 
 * No component is required to know the application-specific types of another component, but all components should be
 * able to manage known types.
 */
public abstract class ExternalBaseType<SpecificT, KnownT, ReadContainerT, WriteContainerT> {

    public abstract SpecificT readValue(ReadContainerT obj, String key);

    public abstract void writeValue(WriteContainerT app, String key, SpecificT value);

    public abstract SpecificT convertFromKnown(KnownT value);

    public abstract KnownT convertToKnown(SpecificT value);

//    public final void assign2AValue(KnownT value, WriteContainerT app, String key) {
//        writeValue(app, key, convertFromKnown(value));
//    }
}
