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
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import static org.lorislab.corn.log.Logger.error;
import static org.lorislab.corn.log.Logger.info;
import org.lorislab.corn.model.AbstractDataObject;
import org.lorislab.corn.model.DataGeneratorItem;
import org.w3c.dom.Document;

public class XmlObject extends AbstractDataObject implements Map {

    private Document document;

    private Map<String, Object> data;

    private String root;

    private String namespace;

    private String xpath;

    private XSDDefinition xsdDefinition;

    public XmlObject(XSDDefinition definition, DataGeneratorItem output) {
        super(definition.getDefinition(), output);
        this.xsdDefinition = definition;
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

    public Document getDocument() {
        return document;
    }

    @Override
    protected void addCustomAttribute() {  
        info("   \"root\": \"xml_root_name is mandatory\",");
        info("   \"namespace\": \"xml_root_namespace is optional\",");
        info("   \"data\": \"xml_structure is mandatory\",");
    }

    @Override
    protected void createData(Map<String, Object> data) {

        root = (String) data.get("root");
        if (root == null || root.isEmpty()) {
            missingAttribute("root");
        }
        
        if (fileName == null || fileName.isEmpty()) {
            missingAttribute("file");
        }
        data = (Map<String, Object>) data.get("data");
        if (data == null || data.isEmpty()) {
            missingAttribute("data");
        }
        
        namespace = (String) data.get("namespace");
        xpath = XmlPathItem.createXPath("", root);

        GeneratorConfig config = createGeneratorConfig(this.output.config);
        Generator generator = new Generator(config, xsdDefinition);
        document = generator.generate(namespace, root, data);
    }

    @Override
    protected void validation(Path path) {
        XmlValidator.validate(path, xsdDefinition);
    }
    
    private static GeneratorConfig createGeneratorConfig(Map<String, Object> data) {
        GeneratorConfig config = new GeneratorConfig();
        if (data == null || data.isEmpty()) {
            return config;
        }
        Object tmp = data.get("xml-max-recursion-depth");
        if (tmp instanceof BigDecimal) {
            config.maximumRecursionDepth = ((BigDecimal) tmp).intValue();
        }
        tmp = data.get("xml-max-elements");
        if (tmp instanceof BigDecimal) {
            config.maximumElementsGenerated = ((BigDecimal) tmp).intValue();
        }
        tmp = data.get("xml-min-elements");
        if (tmp instanceof BigDecimal) {
            config.minimumElementsGenerated = ((BigDecimal) tmp).intValue();
        }
        tmp = data.get("xml-max-list-items");
        if (tmp instanceof BigDecimal) {
            config.maximumListItemsGenerated = ((BigDecimal) tmp).intValue();
        }
        tmp = data.get("xml-min-list-items");
        if (tmp instanceof BigDecimal) {
            config.minimumListItemsGenerated = ((BigDecimal) tmp).intValue();
        }
        tmp = data.get("xml-date-format");
        if (tmp instanceof String) {
            config.dateFormat = (String) tmp;
        }
        tmp = data.get("xml-time-format");
        if (tmp instanceof String) {
            config.timeFormat = (String) tmp;
        }
        tmp = data.get("xml-all-choices");
        if (tmp instanceof Boolean) {
            config.generateAllChoices = (Boolean) tmp;
        }
        tmp = data.get("xml-optional-attributes");
        if (tmp instanceof Boolean) {
            config.generateOptionalAttributes = (Boolean) tmp;
        }
        tmp = data.get("xml-fixed-attributes");
        if (tmp instanceof Boolean) {
            config.generateFixedAttributes = (Boolean) tmp;
        }
        tmp = data.get("xml-default-attributes");
        if (tmp instanceof Boolean) {
            config.generateDefaultAttributes = (Boolean) tmp;
        }
        tmp = data.get("xml-optional-elements");
        if (tmp instanceof Boolean) {
            config.generateOptionalElements = (Boolean) tmp;
        }
        tmp = data.get("xml-default-elements-value");
        if (tmp instanceof Boolean) {
            config.generateDefaultElementValues = (Boolean) tmp;
        }
        return config;
    }

    @Override
    protected Path writeToFile(Path parent) {
        Path path = parent.resolve(fileName);
        try {
            Source sc;
            if (xsdDefinition.isWsdl()) {
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
                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer t = tf.newTransformer();
                t.setOutputProperty(OutputKeys.INDENT, "yes");
                t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                t.transform(sc, new StreamResult(writer));
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
        return XmlPathItem.getObject(document, xpath, key);
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
        return XmlPathItem.containsObject(document, xpath, key);
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
