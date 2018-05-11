/*
 * Copyright 2018 lorislab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lorislab.corn.xml;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.transform.Source;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.xerces.dom.DOMXSImplementationSourceImpl;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.xs.XSImplementationImpl;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.lorislab.corn.model.DataDefinition;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.LSResourceResolver;

/**
 *
 * @author andrej
 */
public class XSDDefinition {

    private static SchemaFactory SF = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

    private boolean wsdl;

    private final DataDefinition definition;

    private List<XSDResource> xsdResources;

    private Schema schema;

    private XSModel xsModel;

    public XSDDefinition(DataDefinition definition) {
        this.definition = definition;
        if (this.definition.xsds != null) {
            xsdResources = new ArrayList<>(this.definition.xsds.size());
            for (String xsd : definition.xsds) {
                XSDResource res = new XSDResource(xsd);
                xsdResources.add(res);
            }
        }
        
        List<Source> tmp = new ArrayList<>(xsdResources.size());
        List<String> tmp2 = new ArrayList<>(xsdResources.size());
        for (XSDResource r : xsdResources) {
            tmp.add(r.getSource());
            tmp2.add(r.getXsdUri());
        }
        Source[] result = tmp.toArray(new Source[tmp.size()]);
        String[] uris = tmp2.toArray(new String[tmp2.size()]);
        
        try {
            schema = SF.newSchema(result);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        XSLoader xsLoader = createXSLoader(null, null, false);
        xsModel = xsLoader.loadURIList(new StringListImpl(uris, uris.length));
        if (xsModel == null) {
            throw new RuntimeException("Couldn't load XMLSchema from " + Arrays.asList(uris));
        }
    }

    public XSModel getXsModel() {
        return xsModel;
    }    

    public Schema getSchema() {
        return schema;
    }

    public boolean isWsdl() {
        return wsdl;
    }

    public DataDefinition getDefinition() {
        return definition;
    }

    public List<XSDResource> getXsdResources() {
        return xsdResources;
    }

    public boolean isEmpty() {
        return xsdResources.isEmpty();
    }

    private static XSLoader createXSLoader(LSResourceResolver entityResolver, DOMErrorHandler errorHandler, boolean enableSchema11) {
        System.setProperty(DOMImplementationRegistry.PROPERTY, DOMXSImplementationSourceImpl.class.getName());
        DOMImplementationRegistry registry;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Imposible error", ex);
        }
        XSImplementationImpl xsImpl = (XSImplementationImpl) registry.getDOMImplementation("XS-Loader");

        XSLoader xsLoader = xsImpl.createXSLoader(null);
        DOMConfiguration config = xsLoader.getConfig();
        config.setParameter(Constants.DOM_VALIDATE, Boolean.TRUE);

        if (entityResolver != null) {
            config.setParameter(Constants.DOM_RESOURCE_RESOLVER, entityResolver);
        }

        if (errorHandler != null) {
            config.setParameter(Constants.DOM_ERROR_HANDLER, errorHandler);
        }
        if (enableSchema11) {
            config.setParameter(Constants.XERCES_PROPERTY_PREFIX + "validation/schema/version", "http://www.w3.org/XML/XMLSchema/v1.1");
        }
        return xsLoader;
    }
}
