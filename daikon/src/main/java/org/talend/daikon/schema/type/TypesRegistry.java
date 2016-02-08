package org.talend.daikon.schema.type;


import java.util.Map;

import org.talend.daikon.schema.SchemaElement;

public interface TypesRegistry {
    public String getFamilyName();

    public Map<Class<? extends ExternalBaseType>, SchemaElement.Type> getMapping();
}
