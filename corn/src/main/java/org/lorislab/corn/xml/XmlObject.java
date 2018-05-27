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
import java.util.HashMap;
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
import org.lorislab.corn.xml.XmlObjectInput.XmlConfig;
import org.w3c.dom.Document;

public class XmlObject implements Map {

    private static final Map<Integer, XSDDefinition> XSD_DEFINITIONS = new HashMap<>();

    private Document document;

    private String xpath;

    private XSDDefinition xsdDefinition;

    private GeneratorConfig config;

    private final XmlObjectInput input;

    public XmlObject(XmlObjectInput input) {
        this.input = input;
        config = createGeneratorConfig(input.config);

        int code = 10;
        for (String s : input.definition.xsds) {
            code = code * 31 + s.hashCode();
        }

        xsdDefinition = XSD_DEFINITIONS.get(code);
        if (xsdDefinition == null) {
            xsdDefinition = new XSDDefinition(input.definition.xsds);
            XSD_DEFINITIONS.put(code, xsdDefinition);
        }

    }

    public Map<String, Object> getData() {
        return input.data;
    }

    public Document getDocument() {
        return document;
    }

    public Path generate(Path outpout) {
        xpath = XmlPathItem.createXPath("", input.root);
        Generator generator = new Generator(config, xsdDefinition);
        document = generator.generate(input.namespace, input.root, input.data);        
        Path path = writeToFile(outpout);
        XmlValidator.validate(path, xsdDefinition);
        return path;
    }

    private static GeneratorConfig createGeneratorConfig(XmlConfig data) {
        GeneratorConfig config = new GeneratorConfig();
        if (data == null) {
            return config;
        }
        if (data.maximumRecursionDepth != null) {
            config.maximumRecursionDepth = data.maximumRecursionDepth;
        }
        if (data.maximumElementsGenerated != null) {
            config.maximumElementsGenerated = data.maximumElementsGenerated;
        }
        if (data.minimumElementsGenerated != null) {
            config.minimumElementsGenerated = data.minimumElementsGenerated;
        }
        if (data.maximumListItemsGenerated != null) {
            config.maximumListItemsGenerated = data.maximumListItemsGenerated;
        }
        if (data.minimumListItemsGenerated != null) {
            config.minimumListItemsGenerated = data.minimumListItemsGenerated;
        }
        if (data.dateFormat != null) {
            config.dateFormat = data.dateFormat;
        }
        if (data.timeFormat != null) {
            config.timeFormat = data.timeFormat;
        }
        if (data.generateAllChoices != null) {
            config.generateAllChoices = data.generateAllChoices;
        }
        if (data.generateOptionalAttributes != null) {
            config.generateOptionalAttributes = data.generateOptionalAttributes;
        }
        if (data.generateFixedAttributes != null) {
            config.generateFixedAttributes = data.generateFixedAttributes;
        }
        if (data.generateDefaultAttributes != null) {
            config.generateDefaultAttributes = data.generateDefaultAttributes;
        }
        if (data.generateOptionalElements != null) {
            config.generateOptionalElements = data.generateOptionalElements;
        }
        if (data.generateDefaultElementValues != null) {
            config.generateDefaultElementValues = data.generateDefaultElementValues;
        }
        return config;
    }

    private Path writeToFile(Path parent) {
        Path path = parent.resolve(input.file);
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

    public int length() {
        return size();
    }

    public int getLength() {
        return size();
    }

    public int getSize() {
        return size();
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
