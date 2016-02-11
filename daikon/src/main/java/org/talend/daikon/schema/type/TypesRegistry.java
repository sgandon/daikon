package org.talend.daikon.schema.type;

import java.util.Map;

import org.talend.daikon.schema.MakoElement;

public interface TypesRegistry<T extends ExternalBaseType<?, ?, ?, ?>> {

    public String getFamilyName();

    public Map<Class<? extends T>, MakoElement.Type> getMapping();
}
