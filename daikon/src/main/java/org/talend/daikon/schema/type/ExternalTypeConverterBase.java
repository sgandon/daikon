package org.talend.daikon.schema.type;

/**
 * Created by bchen on 16-1-14.
 */
public abstract class ExternalTypeConverterBase<T, ReadContainerT, WriteContainerT>
        extends ExternalTypeConverter<T, T, ReadContainerT, WriteContainerT> {

    protected abstract T getValue(ReadContainerT obj, String key);

    protected abstract void setValue(WriteContainerT app, String key, T value);

    protected T convertToExternal(T value) {
        return value;
    }

    protected T convertFromExternal(T value) {
        return value;
    }
}
