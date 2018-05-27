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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Map.Entry;
import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.xerces.dom.DOMInputImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSInput;

/**
 *
 * @author andrej
 */
public class XSDResource {

    private String path;

    private String xsdUri;

    private boolean wsdl;

    private boolean classpath;

    public XSDResource(String tmp) {
        classpath = false;
        path = tmp;
        xsdUri = tmp;
        try {
            wsdl = path.endsWith(".wsdl");
            if (!Files.exists(Paths.get(path))) {
                URL url = XSDResource.class.getResource(path);
                if (url != null) {
                    xsdUri = url.toURI().toString();
                    classpath = true;
                } else {
                    throw new RuntimeException("The XSD could not be found on the file system or classpath. XSD: " + path);
                }
            }
        } catch (RuntimeException | URISyntaxException ex) {
            throw new RuntimeException("Error reading the xsd definition for " + path, ex);
        }
    }

    public LSInput getLSInput() {
        if (wsdl) {
            String tmp = wsdl2xsd(xsdUri);
            LSInput result = new DOMInputImpl(null, xsdUri, null, tmp, "UTF-16");
            return result;
        }
        LSInput result = new DOMInputImpl(null, xsdUri, null);
        return result;
    }

    public Source getSource() {
        try {
            Source tmp;
            if (wsdl) {
                tmp = loadWsdlSource(xsdUri);
            } else {
                InputStream in;
                if (classpath) {
                    in = XmlValidator.class.getResourceAsStream(path);
                } else {
                    in = new FileInputStream(xsdUri);
                }
                tmp = new StreamSource(in, xsdUri);
            }
            return tmp;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isWsdl() {
        return wsdl;
    }

    public boolean isClasspath() {
        return classpath;
    }

    public boolean isFileSystem() {
        return !classpath;
    }

    public String getXsdUri() {
        return xsdUri;
    }

    private static Definition loadDefinition(String name) throws Exception {
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.importDocuments", false);
        reader.setFeature("javax.wsdl.verbose", false);
        Definition definition = reader.readWSDL(name);
        return definition;
    }

    private static Source loadWsdlSource(String wsdl) throws Exception {
        Definition definition = loadDefinition(wsdl);
        if (definition.getTypes() != null) {
            for (Object o : definition.getTypes().getExtensibilityElements()) {
                if (o instanceof javax.wsdl.extensions.schema.Schema) {
                    Element e = ((javax.wsdl.extensions.schema.Schema) o).getElement();
                    return new DOMSource(e);
                }
            }
        }
        return null;
    }

    public static String wsdl2xsd(String resource) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Definition definition = loadDefinition(resource);

            if (definition.getTypes() != null) {
                for (Object o : definition.getTypes().getExtensibilityElements()) {
                    if (o instanceof javax.wsdl.extensions.schema.Schema) {
                        Element ele = ((javax.wsdl.extensions.schema.Schema) o).getElement();
                        Node newNode = doc.importNode(ele, true);
                        doc.appendChild(newNode);
                    }
                }
            }
            Map<String, String> ns = definition.getNamespaces();
            if (ns != null) {
                for (Entry<String, String> e : ns.entrySet()) {
                    if (e.getKey() != null && !e.getKey().isEmpty()) {
                        doc.getDocumentElement().setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:" + e.getKey(), e.getValue());
                    }
                }
            }

            StringWriter writer = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
//            t.setOutputProperty(OutputKeys.INDENT, "yes");
//            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.transform(new DOMSource(doc), new StreamResult(writer));
            
            return writer.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error loading the XSD from the WSLD schema.", ex);
        }
    }

}
