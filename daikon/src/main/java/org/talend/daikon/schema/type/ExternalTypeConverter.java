package org.talend.daikon.schema.type;

/**
 * Created by bchen on 16-1-14.
 */
public abstract class ExternalTypeConverter<SpecificT, GenericT, ReadContainerT, WriteContainerT> {

    protected abstract SpecificT getValue(ReadContainerT obj, String key);

    protected abstract void setValue(WriteContainerT app, String key, SpecificT value);

    protected abstract SpecificT convertToExternal(GenericT value);

    protected abstract GenericT convertFromExternal(SpecificT value);
}
