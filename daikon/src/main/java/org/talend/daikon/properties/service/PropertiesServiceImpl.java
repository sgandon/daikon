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
package org.talend.daikon.properties.service;

import org.talend.daikon.properties.Properties;
import org.talend.daikon.properties.presentation.Form;
import org.apache.avro.Schema;

/**
 * Main Component Service implementation that is not related to any framework (neither OSGI, nor Spring) it uses a
 * ComponentRegistry implementation that will be provided by framework specific Service classes
 */
public class PropertiesServiceImpl<T extends Properties> implements PropertiesService<T> {

    private Repository<T> repository;

    @Override
    public T makeFormCancelable(T properties, String formName) {
        Form form = properties.getForm(formName);
        if (form == null) {
            throw new IllegalArgumentException("Form: " + formName + " not found");
        }
        form.setCancelable(true);
        return properties;
    }

    @Override
    public T commitFormValues(T properties, String formName) {
        Form form = properties.getForm(formName);
        if (form == null) {
            throw new IllegalArgumentException("Form: " + formName + " not found");
        }
        form.commitValues();
        return properties;
    }

    @Override
    public T validateProperty(String propName, T properties) throws Throwable {
        properties.validateProperty(propName);
        return properties;
    }

    @Override
    public T beforePropertyActivate(String propName, T properties) throws Throwable {
        properties.beforePropertyActivate(propName);
        return properties;
    }

    @Override
    public T beforePropertyPresent(String propName, T properties) throws Throwable {
        properties.beforePropertyPresent(propName);
        return properties;
    }

    @Override
    public T afterProperty(String propName, T properties) throws Throwable {
        properties.afterProperty(propName);
        return properties;
    }

    @Override
    public T beforeFormPresent(String formName, T properties) throws Throwable {
        properties.beforeFormPresent(formName);
        return properties;
    }

    @Override
    public T afterFormNext(String formName, T properties) throws Throwable {
        properties.afterFormNext(formName);
        return properties;
    }

    @Override
    public T afterFormBack(String formName, T properties) throws Throwable {
        properties.afterFormBack(formName);
        return properties;
    }

    @Override
    public T afterFormFinish(String formName, T properties) throws Throwable {
        properties.afterFormFinish(formName);
        return properties;
    }

    @Override
    public String storeProperties(T properties, String name, String repositoryLocation, Schema schema) {
        if (repository != null) {
            return repository.storeProperties(properties, name, repositoryLocation, schema);
        }
        return null;
    }

    @Override
    public void setRepository(Repository repository) {
        this.repository = repository;
    }

}
