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

import org.talend.daikon.schema.MakoElement;
import org.talend.daikon.schema.internal.DataSchemaElement;
import org.talend.daikon.schema.type.utils.FastDateParser;
import org.talend.daikon.schema.type.utils.ParserUtils;

public class TypeMapping {

    private static Map<String, Map<Class<? extends ExternalBaseType>, MakoElement.Type>> externalTypesGroup = new HashMap<>();

    public static final List<MakoElement.Type> NUMBER_TYPES = new ArrayList<>();// in order

    public static final Set<MakoElement.Type> COLUMN_TYPES = new HashSet<>();

    static {
        NUMBER_TYPES.add(MakoElement.Type.BYTE);
        NUMBER_TYPES.add(MakoElement.Type.SHORT);
        NUMBER_TYPES.add(MakoElement.Type.INT);
        NUMBER_TYPES.add(MakoElement.Type.LONG);
        NUMBER_TYPES.add(MakoElement.Type.FLOAT);
        NUMBER_TYPES.add(MakoElement.Type.DOUBLE);

        COLUMN_TYPES.addAll(NUMBER_TYPES);
        COLUMN_TYPES.add(MakoElement.Type.STRING);
        COLUMN_TYPES.add(MakoElement.Type.BOOLEAN);
        COLUMN_TYPES.add(MakoElement.Type.DATE);
        COLUMN_TYPES.add(MakoElement.Type.DECIMAL);
        COLUMN_TYPES.add(MakoElement.Type.BYTE_ARRAY);
        COLUMN_TYPES.add(MakoElement.Type.OBJECT);
        COLUMN_TYPES.add(MakoElement.Type.CHARACTER);
        COLUMN_TYPES.add(MakoElement.Type.LIST);
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
    public static MakoElement.Type getDefaultTalendType(String appFamily, Class<? extends ExternalBaseType> appType) {
        Map<Class<? extends ExternalBaseType>, MakoElement.Type> appTypesGroup = externalTypesGroup.get(appFamily);
        return appTypesGroup.get(appType);
    }

    /**
     * All -> Object All(except List) -> String/byte[], if we support separator, then support list byte -> short -> int
     * -> long -> float -> double char -> int date -> long long -> date
     *
     * @param appFamily
     * @param appType
     * @return
     */
    // TODO re-implement it, now it's very dirty
    public static Set<MakoElement.Type> getTalendTypes(String appFamily, Class<? extends ExternalBaseType> appType) {
        Set<MakoElement.Type> talendTypes = new HashSet<>();
        MakoElement.Type bestType = getDefaultTalendType(appFamily, appType);
        talendTypes.add(bestType);
        if (bestType == MakoElement.Type.CHARACTER) {
            talendTypes.add(MakoElement.Type.INT);
        } else if (bestType == MakoElement.Type.DATE) {
            talendTypes.add(MakoElement.Type.LONG);
        } else if (bestType == MakoElement.Type.LONG) {
            talendTypes.add(MakoElement.Type.DATE);
        } else if (NUMBER_TYPES.contains(bestType)) {
            boolean find = false;
            for (MakoElement.Type numberType : NUMBER_TYPES) {
                if (!find) {
                    if (numberType == bestType)
                        find = true;
                    continue;
                } else {
                    talendTypes.add(numberType);
                }
            }
        }

        if (!talendTypes.contains(MakoElement.Type.LIST)) {
            talendTypes.add(MakoElement.Type.STRING);
            talendTypes.add(MakoElement.Type.BYTE_ARRAY);
        }
        talendTypes.add(MakoElement.Type.OBJECT);

        return talendTypes;
    }

    /**
     * @param appFamily
     * @param talendType
     * @return
     */
    public static Set<Class<? extends ExternalBaseType>> getAppTypes(String appFamily, MakoElement.Type talendType) {
        Map<MakoElement.Type, Set<Class<? extends ExternalBaseType>>> mapping = new HashMap<>();
        for (MakoElement.Type type : COLUMN_TYPES) {
            if (mapping.get(type) == null) {
                mapping.put(type, new HashSet<Class<? extends ExternalBaseType>>());
            }
        }
        // TODO improve the mapping init & cache
        Map<Class<? extends ExternalBaseType>, MakoElement.Type> appTypes = externalTypesGroup.get(appFamily);
        Set<Class<? extends ExternalBaseType>> allAppTypes = new HashSet<>();
        for (Class<? extends ExternalBaseType> appType : appTypes.keySet()) {
            MakoElement.Type tType = appTypes.get(appType);
            Set<Class<? extends ExternalBaseType>> appTypeList = mapping.get(tType);
            appTypeList.add(appType);
            allAppTypes.add(appType);
        }
        mapping.get(MakoElement.Type.OBJECT).addAll(allAppTypes);
        mapping.get(MakoElement.Type.STRING).addAll(allAppTypes);
        mapping.get(MakoElement.Type.STRING).removeAll(mapping.get(MakoElement.Type.LIST));
        mapping.get(MakoElement.Type.BYTE_ARRAY).addAll(allAppTypes);
        mapping.get(MakoElement.Type.BYTE_ARRAY).removeAll(mapping.get(MakoElement.Type.LIST));

        for (MakoElement.Type currentNumberType : NUMBER_TYPES) {
            for (MakoElement.Type numberType : NUMBER_TYPES) {
                if (numberType == currentNumberType) {
                    // the one bigger then currentNumberType won't get the possible app types from it
                    break;
                }
                mapping.get(numberType).addAll(mapping.get(currentNumberType));
            }
        }

        mapping.get(MakoElement.Type.DATE).addAll(mapping.get(MakoElement.Type.LONG));
        mapping.get(MakoElement.Type.LONG).addAll(mapping.get(MakoElement.Type.DATE));
        mapping.get(MakoElement.Type.CHARACTER).addAll(mapping.get(MakoElement.Type.INT));

        return mapping.get(talendType);
    }

    // TODO implement all convert between internal talend type
    public static Object convert(String familyName, DataSchemaElement schemaElement, Object inValue) {
        // FIXME
        MakoElement.Type inType = TypeMapping.getDefaultTalendType(familyName, null/* schemaElement.getAppColType() */);
        MakoElement.Type outType = schemaElement.getType();
        if (inType == outType) {
            return inValue;
        } else {
            if (outType == MakoElement.Type.OBJECT) {
                return inValue;
            } else if (outType == MakoElement.Type.STRING && inType != MakoElement.Type.LIST) {
                return convertToString(inType, inValue, schemaElement);
            } else if (outType == MakoElement.Type.BYTE_ARRAY && inType != MakoElement.Type.LIST) {
                return (convertToString(inType, inValue, schemaElement).getBytes());// TODO support encoding for each
                                                                                    // column in future?
            } else if (outType == MakoElement.Type.DECIMAL) {
            } else if (outType == MakoElement.Type.DOUBLE) {
            } else if (outType == MakoElement.Type.FLOAT) {
            } else if (outType == MakoElement.Type.LONG) {
                if (inType == MakoElement.Type.INT) {
                    return ((Integer) inValue).longValue();
                }
            } else if (outType == MakoElement.Type.INT) {
            } else if (outType == MakoElement.Type.SHORT) {
            } else if (outType == MakoElement.Type.BYTE) {
            } else if (outType == MakoElement.Type.BOOLEAN) {

            } else if (outType == MakoElement.Type.CHARACTER) {

            } else if (outType == MakoElement.Type.DATE) {
                if (inType == MakoElement.Type.LONG) {
                    return new Date((Long) inValue);
                } else if (inType == MakoElement.Type.STRING) {
                    return ParserUtils.parseTo_Date((String) inValue, schemaElement.getPattern());
                }
            }
        }
        return null;
    }

    private static String convertToString(MakoElement.Type inType, Object inValue, MakoElement schemaElement) {
        if (inType == MakoElement.Type.BYTE_ARRAY) {
            return new String(((byte[]) inValue));
        } else if (inType == MakoElement.Type.DATE) {
            DateFormat format = FastDateParser.getInstance(schemaElement.getPattern());
            return format.format((Date) inValue);
        } else if (inType == MakoElement.Type.SHORT || inType == MakoElement.Type.BYTE) {
            return String.valueOf(Integer.valueOf((int) inValue));
        } else {
            return String.valueOf(inValue);
        }
    }
}
