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

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;
import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author andrej
 */
public class XMLUtil {

    public static String wsdl2xsd(String resource) throws Exception {
        String result = resource + ".xsd";
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.verbose", false);
        reader.setFeature("javax.wsdl.importDocuments", false);
        Definition definition = reader.readWSDL(resource);

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

        writeToFile(doc, new File(result));
        return result;
    }

    public static QName getQName(XSModel xsModel, String root) {
        String namespace = null;
        XSNamedMap m1 = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
        if (m1 != null) {
            for (int i = 0; i < m1.getLength(); i++) {
                XSObject o = m1.item(i);
                if (root.equals(o.getName())) {
                    namespace = o.getNamespace();
                }
            }
        }
        return new QName(namespace, root);
    }

    public static void writeToFile(Document document, File file) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(new DOMSource(document), new StreamResult(file));
    }

    public static void write(Document document, Writer writer) throws Exception {
        write(new DOMSource(document), writer);
    }

    public static void write(Source source, Writer writer) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(source, new StreamResult(writer));
    }

    public static String toString(Document document) throws Exception {
        StringWriter sw = new StringWriter();
        write(document, sw);
        return sw.toString();
    }

}
