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

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.lorislab.corn.js.Engine;
import static org.lorislab.corn.log.Logger.debug;
import org.lorislab.corn.model.AbstractDataObject;
import org.lorislab.corn.model.DataDefinition;
import org.lorislab.corn.model.DataGeneratorItem;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlObject extends AbstractDataObject implements Map {

    private Document document;

    private boolean wsdl;

    private Map<String, Object> data;

    private String root;

    private String namespace;

    public XmlObject(DataDefinition definition, DataGeneratorItem output) {
        super(definition, output);
    }

    public String getNamespace() {
        return namespace;
    }

    public String getRoot() {
        return root;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public boolean isWsdl() {
        return wsdl;
    }

    public Document getDocument() {
        return document;
    }

    @Override
    public Path generate(Path directory, Engine engine) {
        try {
            Map<String, Object> tmp = engine.evalFile(output.js);
            fileName = (String) tmp.get("file");
            root = (String) tmp.get("root");
            namespace = (String) tmp.get("namespace");
            data = (Map<String, Object>) tmp.get("data");
        } catch (Exception ex) {
            throw new RuntimeException("Error reading the xml model", ex);
        }
        
        GeneratorConfig config = new GeneratorConfig();
        Map<String, Object> xc = this.output.config;
        if (xc != null) {
            Object maximumRecursionDepth = xc.get("maximumRecursionDepth");
            if (maximumRecursionDepth instanceof Integer) {
                config.maximumRecursionDepth = (Integer) maximumRecursionDepth;
            }
        }
        Generator generator = new Generator(config, this.definition.xsds);
        generator.generate(namespace, root, data);
        document = generator.getDocument();
        wsdl = generator.isWsdl();

        return writeToFile(directory);
    }

    private Path writeToFile(Path parent) {
        Path path = parent.resolve(fileName);
        try {
            Source sc;
            if (wsdl) {
                SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
                soapMessage.getSOAPBody().addDocument(document);

                soapMessage.getSOAPPart().getEnvelope().removeNamespaceDeclaration("SOAP-ENV");
                soapMessage.getSOAPPart().getEnvelope().addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
                soapMessage.getSOAPPart().getEnvelope().setPrefix("soap");
                soapMessage.getSOAPHeader().setPrefix("soap");
                soapMessage.getSOAPBody().setPrefix("soap");
                sc = soapMessage.getSOAPPart().getContent();
            } else {
                sc = new DOMSource(document);
            }

            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                XMLUtil.write(sc, writer);
            } catch (Exception ex) {
                throw new RuntimeException("Error write XML ", ex);
            }
        } catch (Exception exx) {
            throw new RuntimeException(exx);
        }
        return path;
    }

    @Override
    public Object get(Object key) {
        try {
            String pathKey = (String) key;
            if (pathKey.startsWith("/")) {
                String[] tmp = pathKey.split("/");
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < tmp.length; i++) {
                    sb.append("/*[local-name()='");
                    sb.append(tmp[i]);
                    sb.append("']");
                }
                pathKey = sb.toString();
            } else {
                pathKey = "/*[local-name()='" + pathKey + "']";
            }

            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate(pathKey, document, XPathConstants.NODESET);
            Node node = nodeList.item(0);
            if (node.hasChildNodes()) {
                if (node.getChildNodes().item(0) instanceof Text) {
                    debug("TEXT: " + node.getTextContent());
                    return node.getTextContent();
                }
                return new XmlPathItem(pathKey, document);
            } else {
                debug("RESULT: " + node.getTextContent());
                return node.getTextContent();
            }

        } catch (Exception ex) {
            throw new RuntimeException("Error reading the xml " + key, ex);
        }
    }

    @Override
    public int size() {
        if (!isEmpty()) {
            return document.getChildNodes().getLength();
        }
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return document == null && (document.hasAttributes() || document.hasChildNodes());
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public Collection values() {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

    @Override
    public Set entrySet() {
        throw new UnsupportedOperationException("Not supported method for the object.");
    }

}
