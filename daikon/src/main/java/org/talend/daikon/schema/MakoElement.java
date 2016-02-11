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

import java.util.List;
import java.util.Map;

import org.talend.daikon.NamedThing;
import org.talend.daikon.strings.ToStringIndent;

/**
 * Specifies data that can be used to specify component properties.
 * 
 * (Previously called SchemaElement, this is no longer used to specify schemas of data passed between components or row
 * structures.)
 */
public interface MakoElement extends NamedThing, ToStringIndent {

    public enum Type {
        STRING,
        BOOLEAN,
        BYTE,
        SHORT,
        INT,
        LONG,
        DATE,
        DATETIME,
        DECIMAL,
        FLOAT,
        DOUBLE,
        BYTE_ARRAY,
        ENUM,
        OBJECT,
        CHARACTER,
        LIST,
        DYNAMIC,
        GROUP,
        SCHEMA
    }

    public static int INFINITE = -1;

    @Override
    public String getName();

    public MakoElement setName(String name);

    @Override
    public String getTitle();

    public MakoElement setTitle(String description);

    public Type getType();

    public MakoElement setType(Type type);

    public int getSize();

    public MakoElement setSize(int size);

    public int getOccurMinTimes();

    public boolean isSizeUnbounded();

    public MakoElement setOccurMinTimes(int times);

    public int getOccurMaxTimes();

    public MakoElement setOccurMaxTimes(int times);

    public boolean isRequired();

    public MakoElement setRequired();

    public MakoElement setRequired(boolean required);

    public int getPrecision();

    public MakoElement setPrecision(int precision);

    public String getPattern();

    public MakoElement setPattern(String pattern);

    public String getDefaultValue();

    public MakoElement setDefaultValue(String defaultValue);

    public boolean isNullable();

    public MakoElement setNullable(boolean nullable);

    public Class<?> getEnumClass();

    public MakoElement setEnumClass(Class<?> enumClass);

    public List<?> getPossibleValues();

    public MakoElement setPossibleValues(List<?> possibleValues);

    public MakoElement setPossibleValues(Object... values);

    public List<MakoElement> getChildren();

    public MakoElement getChild(String name);

    public MakoElement setChildren(List<MakoElement> children);

    public MakoElement addChild(MakoElement child);

    public Map<String, MakoElement> getChildMap();

}
