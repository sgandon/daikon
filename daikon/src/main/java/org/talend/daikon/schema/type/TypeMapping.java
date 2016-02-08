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
package org.talend.daikon.schema.type;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.daikon.schema.SchemaElement;
import org.talend.daikon.schema.internal.DataSchemaElement;
import org.talend.daikon.schema.type.utils.FastDateParser;
import org.talend.daikon.schema.type.utils.ParserUtils;


public class TypeMapping {

    private static Map<String, Map<Class<? extends ExternalBaseType>, SchemaElement.Type>> externalTypesGroup = new HashMap<>();
    public static final List<SchemaElement.Type> NUMBER_TYPES = new ArrayList<>();// in order
    public static final Set<SchemaElement.Type> COLUMN_TYPES = new HashSet<>();

    static {
        NUMBER_TYPES.add(SchemaElement.Type.BYTE);
        NUMBER_TYPES.add(SchemaElement.Type.SHORT);
        NUMBER_TYPES.add(SchemaElement.Type.INT);
        NUMBER_TYPES.add(SchemaElement.Type.LONG);
        NUMBER_TYPES.add(SchemaElement.Type.FLOAT);
        NUMBER_TYPES.add(SchemaElement.Type.DOUBLE);

        COLUMN_TYPES.addAll(NUMBER_TYPES);
        COLUMN_TYPES.add(SchemaElement.Type.STRING);
        COLUMN_TYPES.add(SchemaElement.Type.BOOLEAN);
        COLUMN_TYPES.add(SchemaElement.Type.DATE);
        COLUMN_TYPES.add(SchemaElement.Type.DECIMAL);
        COLUMN_TYPES.add(SchemaElement.Type.BYTE_ARRAY);
        COLUMN_TYPES.add(SchemaElement.Type.OBJECT);
        COLUMN_TYPES.add(SchemaElement.Type.CHARACTER);
        COLUMN_TYPES.add(SchemaElement.Type.LIST);
    }


    /**
     * when there is new family, should registry it before use these types
     *
     * @param registry
     */
    public static void registryTypes(TypesRegistry registry) {
        externalTypesGroup.put(registry.getFamilyName(), registry.getMapping());
    }

    /**
     * each app type should has only one default talend type
     *
     * @param appFamily
     * @param appType
     * @return
     */
    public static SchemaElement.Type getDefaultTalendType(String appFamily, Class<? extends ExternalBaseType> appType) {
        Map<Class<? extends ExternalBaseType>, SchemaElement.Type> appTypesGroup = externalTypesGroup.get(appFamily);
        return appTypesGroup.get(appType);
    }

    /**
     * All -> Object
     * All(except List) -> String/byte[], if we support separator, then support list
     * byte -> short -> int -> long -> float -> double
     * char -> int
     * date -> long
     * long -> date
     *
     * @param appFamily
     * @param appType
     * @return
     */
    //TODO re-implement it, now it's very dirty
    public static Set<SchemaElement.Type> getTalendTypes(String appFamily, Class<? extends ExternalBaseType> appType) {
        Set<SchemaElement.Type> talendTypes = new HashSet<>();
        SchemaElement.Type bestType = getDefaultTalendType(appFamily, appType);
        talendTypes.add(bestType);
        if (bestType == SchemaElement.Type.CHARACTER) {
            talendTypes.add(SchemaElement.Type.INT);
        } else if (bestType == SchemaElement.Type.DATE) {
            talendTypes.add(SchemaElement.Type.LONG);
        } else if (bestType == SchemaElement.Type.LONG) {
            talendTypes.add(SchemaElement.Type.DATE);
        } else if (NUMBER_TYPES.contains(bestType)) {
            boolean find = false;
            for (SchemaElement.Type numberType : NUMBER_TYPES) {
                if (!find) {
                    if (numberType == bestType)
                        find = true;
                    continue;
                } else {
                    talendTypes.add(numberType);
                }
            }
        }

        if (!talendTypes.contains(SchemaElement.Type.LIST)) {
            talendTypes.add(SchemaElement.Type.STRING);
            talendTypes.add(SchemaElement.Type.BYTE_ARRAY);
        }
        talendTypes.add(SchemaElement.Type.OBJECT);

        return talendTypes;
    }

    /**
     * @param appFamily
     * @param talendType
     * @return
     */
    public static Set<Class<? extends ExternalBaseType>> getAppTypes(String appFamily, SchemaElement.Type talendType) {
        Map<SchemaElement.Type, Set<Class<? extends ExternalBaseType>>> mapping = new HashMap<>();
        for (SchemaElement.Type type : COLUMN_TYPES) {
            if (mapping.get(type) == null) {
                mapping.put(type, new HashSet<Class<? extends ExternalBaseType>>());
            }
        }
        //TODO improve the mapping init & cache
        Map<Class<? extends ExternalBaseType>, SchemaElement.Type> appTypes = externalTypesGroup.get(appFamily);
        Set<Class<? extends ExternalBaseType>> allAppTypes = new HashSet<>();
        for (Class<? extends ExternalBaseType> appType : appTypes.keySet()) {
            SchemaElement.Type tType = appTypes.get(appType);
            Set<Class<? extends ExternalBaseType>> appTypeList = mapping.get(tType);
            appTypeList.add(appType);
            allAppTypes.add(appType);
        }
        mapping.get(SchemaElement.Type.OBJECT).addAll(allAppTypes);
        mapping.get(SchemaElement.Type.STRING).addAll(allAppTypes);
        mapping.get(SchemaElement.Type.STRING).removeAll(mapping.get(SchemaElement.Type.LIST));
        mapping.get(SchemaElement.Type.BYTE_ARRAY).addAll(allAppTypes);
        mapping.get(SchemaElement.Type.BYTE_ARRAY).removeAll(mapping.get(SchemaElement.Type.LIST));

        for (SchemaElement.Type currentNumberType : NUMBER_TYPES) {
            for (SchemaElement.Type numberType : NUMBER_TYPES) {
                if (numberType == currentNumberType) {
                    //the one bigger then currentNumberType won't get the possible app types from it
                    break;
                }
                mapping.get(numberType).addAll(mapping.get(currentNumberType));
            }
        }

        mapping.get(SchemaElement.Type.DATE).addAll(mapping.get(SchemaElement.Type.LONG));
        mapping.get(SchemaElement.Type.LONG).addAll(mapping.get(SchemaElement.Type.DATE));
        mapping.get(SchemaElement.Type.CHARACTER).addAll(mapping.get(SchemaElement.Type.INT));

        return mapping.get(talendType);
    }

    //TODO implement all convert between internal talend type
    public static Object convert(String familyName, DataSchemaElement schemaElement, Object inValue) {
        // FIXME
        SchemaElement.Type inType = TypeMapping.getDefaultTalendType(familyName, null/*schemaElement.getAppColType()*/);
        SchemaElement.Type outType = schemaElement.getType();
        if (inType == outType) {
            return inValue;
        } else {
            if (outType == SchemaElement.Type.OBJECT) {
                return inValue;
            } else if (outType == SchemaElement.Type.STRING && inType != SchemaElement.Type.LIST) {
                return convertToString(inType, inValue, schemaElement);
            } else if (outType == SchemaElement.Type.BYTE_ARRAY && inType != SchemaElement.Type.LIST) {
                return (convertToString(inType, inValue, schemaElement).getBytes());//TODO support encoding for each column in future?
            } else if (outType == SchemaElement.Type.DECIMAL) {
            } else if (outType == SchemaElement.Type.DOUBLE) {
            } else if (outType == SchemaElement.Type.FLOAT) {
            } else if (outType == SchemaElement.Type.LONG) {
                if (inType == SchemaElement.Type.INT) {
                    return ((Integer) inValue).longValue();
                }
            } else if (outType == SchemaElement.Type.INT) {
            } else if (outType == SchemaElement.Type.SHORT) {
            } else if (outType == SchemaElement.Type.BYTE) {
            } else if (outType == SchemaElement.Type.BOOLEAN) {

            } else if (outType == SchemaElement.Type.CHARACTER) {

            } else if (outType == SchemaElement.Type.DATE) {
                if (inType == SchemaElement.Type.LONG) {
                    return new Date((Long) inValue);
                } else if (inType == SchemaElement.Type.STRING) {
                    return ParserUtils.parseTo_Date((String) inValue, schemaElement.getPattern());
                }
            }
        }
        return null;
    }

    private static String convertToString(SchemaElement.Type inType, Object inValue, SchemaElement schemaElement) {
        if (inType == SchemaElement.Type.BYTE_ARRAY) {
            return new String(((byte[]) inValue));
        } else if (inType == SchemaElement.Type.DATE) {
            DateFormat format = FastDateParser.getInstance(schemaElement.getPattern());
            return format.format((Date) inValue);
        } else if (inType == SchemaElement.Type.SHORT || inType == SchemaElement.Type.BYTE) {
            return String.valueOf(Integer.valueOf((int) inValue));
        } else {
            return String.valueOf(inValue);
        }
    }
}
