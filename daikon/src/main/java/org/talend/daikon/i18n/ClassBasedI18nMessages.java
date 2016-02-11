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
package org.talend.daikon.i18n;

import java.util.MissingResourceException;

/**
 * Look for a i18n <b>.properties</b> file according to the following policy. <br>
 * <ul>
 * <li>first the files with the name <b>clazz.getName() + ".properties" </b> are searched.</li>w
 * <li>if none is found then the files named <b>clazz.getPackage().getName() + ".messages.properties"</b> are searched.</li>
 * </ul>
 * Not only it looks for the current class with the following policy but if nothing found it applies the policy above to
 * the super class until java.lang.Object is reached.
 */
public class ClassBasedI18nMessages extends I18nMessages {

    transient private Class<?> clazz;

    transient private String unknownKeyPrefix;

    /**
     * return the value associated to the key found in the bundle associated to the localeProvider and using the package
     * of the clazz to look for the message.properties file.
     *
     * @param localeProvider, if null the java.util.Locale.getDefault() shall be used
     * @param clazz, clazz used to find the resource based on the it name or package name.
     * @param unknownKeyPrefix string used to prefix the returned key if the value was not found (if null then an empty
     * String is used)
     */
    public ClassBasedI18nMessages(LocaleProvider localeProvider, Class<?> clazz, String unknownKeyPrefix) {
        super(localeProvider);
        this.clazz = clazz;
        this.unknownKeyPrefix = unknownKeyPrefix == null ? "" : unknownKeyPrefix; //$NON-NLS-1$
    }

    /**
     * same as {@link I18nMessages#I18nMessages(LocaleProvider, Class<?>, String)} with unknowKeyPrefix set to null
     * 
     * @param localeProvider, if null the java.util.Locale.getDefault() shall be used
     * @param clazz, clazz used to find the resource based on the it name or package name.
     */
    public ClassBasedI18nMessages(LocaleProvider localeProvider, Class<?> clazz) {
        this(localeProvider, clazz, null);
    }

    /**
     * same as {@link I18nMessages#I18nMessages(LocaleProvider, Class<?>, String)} with localeProvider set to null
     * 
     * @param clazz, clazz used to find the resource based on the it name or package name.
     * @param unknowKeyPrefix string used to prefix the returned key if the value was not found (if null then an empty
     * String is used)
     */
    public ClassBasedI18nMessages(Class<?> clazz, String unknowKeyPrefix) {
        this(null, clazz, unknowKeyPrefix);
    }

    /**
     * same as {@link I18nMessages#I18nMessages(Class<?>, String)} with unknowKeyPrefix set to null
     * 
     * @param clazz, clazz used to find the resource based on the it name or package name.
     */
    public ClassBasedI18nMessages(Class<?> clazz) {
        this(clazz, null);
    }

    @Override
    public String getMessage(String key, Object... arguments) {
        // get the ResouceBundle Value
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            // try first ClassNameMessage.properties
            String baseName = computeBaseName(currentClass, true);
            try {
                return getFormattedMessage(key, currentClass.getClassLoader(), baseName, arguments);
            } catch (MissingResourceException mre) {
                // try then PackageName.messages.properties
                baseName = computeBaseName(currentClass, false);
                try {
                    return getFormattedMessage(key, currentClass.getClassLoader(), baseName, arguments);
                } catch (MissingResourceException mre2) {
                    currentClass = currentClass.getSuperclass();
                }
            }
        }
        return unknownKeyPrefix + key;
    }

    /**
     * return a base name base the currentClass Package name and named messages.properties.
     * 
     * @param currentClass used to derive the package of the file
     * @return the base name base on the package name of the class.
     */
    private String computeBaseName(Class<?> currentClass, boolean useClassName) {
        if (useClassName) {
            return currentClass.getName();
        } else {
            return currentClass.getPackage().getName().concat(".messages"); //$NON-NLS-1$
        }
    }

}
