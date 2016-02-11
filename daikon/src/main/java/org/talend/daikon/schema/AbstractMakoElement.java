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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.daikon.SimpleNamedThing;
import org.talend.daikon.strings.ToStringIndentUtil;

/**
 * This implementation shall be used to represent meta data elements
 */
public abstract class AbstractMakoElement extends SimpleNamedThing implements MakoElement {

    private Type type;

    private int size;

    private int occurMinTimes;

    private int occurMaxTimes;

    // Number of decimal places - DI
    private int precision;

    // Used for date conversion - DI
    private String pattern;

    private String defaultValue;

    private boolean nullable;

    private Class<?> enumClass;

    private List<?> possibleValues;

    protected List<MakoElement> children;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public MakoElement setName(String name) {
        this.name = name;
        return this;
    }

    public MakoElement setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public MakoElement setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public MakoElement setType(Type type) {
        this.type = type;
        return this;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public MakoElement setSize(int size) {
        this.size = size;
        return this;
    }

    @Override
    public boolean isSizeUnbounded() {
        if (size == -1) {
            return true;
        }
        return false;
    }

    @Override
    public int getOccurMinTimes() {
        return occurMinTimes;
    }

    @Override
    public MakoElement setOccurMinTimes(int times) {
        this.occurMinTimes = times;
        return this;
    }

    @Override
    public int getOccurMaxTimes() {
        return occurMaxTimes;
    }

    @Override
    public MakoElement setOccurMaxTimes(int times) {
        this.occurMaxTimes = times;
        return this;
    }

    @Override
    public boolean isRequired() {
        return occurMinTimes > 0;
    }

    @Override
    public MakoElement setRequired() {
        return setRequired(true);
    }

    @Override
    public MakoElement setRequired(boolean required) {
        setOccurMinTimes(1);
        setOccurMaxTimes(1);
        return this;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public MakoElement setPrecision(int precision) {
        this.precision = precision;
        return this;
    }

    @Override
    public String getPattern() {
        return pattern;
    }

    @Override
    public MakoElement setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public MakoElement setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    @Override
    public boolean isNullable() {
        return nullable;
    }

    @Override
    public MakoElement setNullable(boolean nullable) {
        this.nullable = nullable;
        return this;
    }

    @Override
    public Class<?> getEnumClass() {
        return enumClass;
    }

    @Override
    public MakoElement setEnumClass(Class<?> enumClass) {
        this.enumClass = enumClass;
        return this;
    }

    @Override
    public List<?> getPossibleValues() {
        return possibleValues;
    }

    @Override
    public MakoElement setPossibleValues(List<?> possibleValues) {
        this.possibleValues = possibleValues;
        return this;
    }

    @Override
    public MakoElement setPossibleValues(Object... values) {
        this.possibleValues = Arrays.asList(values);
        return this;
    }

    @Override
    public List<MakoElement> getChildren() {
        return children;
    }

    @Override
    public MakoElement setChildren(List<MakoElement> children) {
        this.children = children;
        return this;
    }

    @Override
    public MakoElement addChild(MakoElement child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
        return this;
    }

    @Override
    public MakoElement getChild(String name) {
        if (children != null) {
            for (MakoElement child : children) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, MakoElement> getChildMap() {
        Map<String, MakoElement> map = new HashMap<>();
        for (MakoElement se : getChildren()) {
            map.put(se.getName(), se);
        }
        return map;
    }

    @Override
    public String toStringIndent(int indent) {
        return ToStringIndentUtil.indentString(indent) + getName();
    }

}
