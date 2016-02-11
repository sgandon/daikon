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
import java.util.ResourceBundle;

/**
 * This class will provide ResourceBundle string handler with formatted messages using java.text.MessageFormat It will
 * also work with user definined Local that will be used for each message retrieval. This way web application may
 * provide their how LocalProvder to use dynamic local according to the current Request for instance.
 * 
 */
public class ClassLoaderBasedI18nMessages extends I18nMessages {

    private String baseName;

    private String unknownKeyPrefix;

    transient private ClassLoader classLoader;

    /**
     * return the value associated to the key found in the bundle associated to the localeProvider and the baseName
     *
     * @param localeProvider, if null the java.util.Locale.getDefault() shall be used
     * @param ClassLoader, classloader used to find the resource with the baseName
     * @param baseName, used to create the underlying resource bundle, see {@link ResourceBundle#getBundle(String)}
     * @param unknowKeyPrefix string used to prefix the returned key if the value was not found (if null then an empty
     * String is used)
     */
    public ClassLoaderBasedI18nMessages(LocaleProvider localeProvider, ClassLoader classloader, String baseName,
            String unknowKeyPrefix) {
        super(localeProvider);
        this.classLoader = classloader;
        this.baseName = baseName;
        this.unknownKeyPrefix = unknowKeyPrefix == null ? "" : unknowKeyPrefix; //$NON-NLS-1$
    }

    /**
     * same as {@link I18nMessages#I18nMessages(LocaleProvider, ClassLaoder , String, null)}
     *
     * @param localeProvider, if null the java.util.Locale.getDefault() shall be used
     * @param ClassLoader, classloader used to find the resource with the baseName
     * @param baseName, used to create the underlying resource bundle, see {@link ResourceBundle#getBundle(String)}
     */
    public ClassLoaderBasedI18nMessages(LocaleProvider localeProvider, ClassLoader classloader, String baseName) {
        this(localeProvider, classloader, baseName, null);
    }

    /**
     * same as {@link I18nMessages#I18nMessages(LocaleProvider, this.getClass().getClassLaoder() , String, String)}
     * 
     * @param localeProvider, if null the java.util.Locale.getDefault() shall be used
     * @param baseName, used to create the underlying resource bundle, see {@link ResourceBundle#getBundle(String)}
     * @param unknowKeyPrefix string used to prefix the returned key if the value was not found (if null then an empty
     * String is used)
     */
    public ClassLoaderBasedI18nMessages(LocaleProvider localeProvider, String baseName, String unknowKeyPrefix) {
        this(localeProvider, I18nMessages.class.getClassLoader(), baseName, unknowKeyPrefix);
    }

    /**
     * same as {@link I18nMessages#I18nMessages(LocaleProvider, String, String)} with unknowKeyPrefix set to null
     * 
     * @param localeProvider, if null the java.util.Locale.getDefault() shall be used
     * @param baseName, used to create the underlying resource bundle, see {@link ResourceBundle#getBundle(String)}
     */
    public ClassLoaderBasedI18nMessages(LocaleProvider localeProvider, String baseName) {
        this(localeProvider, baseName, null);
    }

    /**
     * same as {@link I18nMessages#I18nMessages(LocaleProvider, String, String)} with localeProvider set to null
     * 
     * @param baseName, used to create the underlying resource bundle, see {@link ResourceBundle#getBundle(String)}
     * @param unknowKeyPrefix string used to prefix the returned key if the value was not found (if null then an empty
     * String is used)
     */
    public ClassLoaderBasedI18nMessages(String baseName, String unknowKeyPrefix) {
        this(null, baseName, unknowKeyPrefix);
    }

    /**
     * same as {@link I18nMessages#I18nMessages(String, String)} with unknowKeyPrefix set to null
     * 
     * @param baseName, used to create the underlying resource bundle, see {@link ResourceBundle#getBundle(String)}
     * @param unknownKeyPrefix string used to prefix the returned key if the value was not found (default is empty)
     */
    public ClassLoaderBasedI18nMessages(String baseName) {
        this(baseName, null);
    }

    @Override
    public String getMessage(String key, Object... arguments) {
        // get the ResouceBundle Value
        try {
            return getFormattedMessage(key, classLoader, baseName, arguments);

        } catch (MissingResourceException mre) {
            return unknownKeyPrefix + key;
        }
    }

    /**
     * Create an new FromatedMessage based on the current instance localProvider. The same as same as {@link
     * I18nMessages#I18nMessages(this.localeProvider, ClassLaoder , String)}
     * 
     * @param ClassLoader, classloader used to find the resource with the baseName
     * @param baseName, used to create the underlying resource bundle, see {@link ResourceBundle#getBundle(String)}
     * @return a new instance.
     */
    public I18nMessages createNew(ClassLoader aClassLoader, String aBaseName) {
        return new ClassLoaderBasedI18nMessages(this.localeProvider, aClassLoader, aBaseName);
    }

    /**
     * Getter for baseName.
     * 
     * @return the baseName
     */
    public String getBaseName() {
        return this.baseName;
    }

}
