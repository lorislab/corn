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
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.xs.XSImplementationImpl;
import org.apache.xerces.impl.xs.util.LSInputListImpl;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.lorislab.corn.model.DataDefinition;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.LSInput;

/**
 *
 * @author andrej
 */
public class XSDDefinition {

    private static final SchemaFactory SF = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

    private static final DOMImplementationRegistry DOM_REGISTRY;
    
    private static final XSImplementationImpl XS_IMPL;
    
    static {
        try {
            DOM_REGISTRY = DOMImplementationRegistry.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Error creating the DOM implementation registry instance", ex);
        }
        XS_IMPL = (XSImplementationImpl) DOM_REGISTRY.getDOMImplementation("XS-Loader");
    }
    
    private boolean wsdl;

    private final DataDefinition definition;

    private List<XSDResource> xsdResources;

    private Schema schema;

    private XSModel xsModel;

    public XSDDefinition(DataDefinition definition) {
        this.definition = definition;
        if (this.definition.xml.xsds != null) {
            xsdResources = new ArrayList<>(this.definition.xml.xsds.size());
            for (String xsd : definition.xml.xsds) {
                XSDResource res = new XSDResource(xsd);
                xsdResources.add(res);
            }
        }
        
        List<Source> tmp = new ArrayList<>(xsdResources.size());
        List<LSInput> tmp2 = new ArrayList<>(xsdResources.size());
        for (XSDResource r : xsdResources) {
            wsdl = wsdl || r.isWsdl();
            tmp.add(r.getSource());
            tmp2.add(r.getLSInput());
        }
        Source[] result = tmp.toArray(new Source[tmp.size()]);
        LSInput[] uris = tmp2.toArray(new LSInput[tmp2.size()]);
        
        try {
            schema = SF.newSchema(result);
        } catch (Exception ex) {
            throw new RuntimeException("Error create schema for the XSD definition", ex);
        }

        XSLoader xsLoader = XS_IMPL.createXSLoader(null);
        xsLoader.getConfig().setParameter(Constants.DOM_VALIDATE, Boolean.TRUE);
        
        xsModel = xsLoader.loadInputList(new LSInputListImpl(uris, uris.length));
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

}
