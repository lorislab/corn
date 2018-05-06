/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.lorislab.corn.xml;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
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
import org.lorislab.corn.wsdl.TDefinitions;
import org.lorislab.corn.wsdl.TDocumented;
import org.lorislab.corn.wsdl.TTypes;
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
        
//        WSDLFactory factory = WSDLFactory.newInstance();
//        WSDLReader reader = factory.newWSDLReader();
//        reader.setFeature("javax.wsdl.importDocuments", false);
//        Definition definition = reader.readWSDL(resource);
//        
//        if (definition.getTypes() != null) {
//            for (Object o : definition.getTypes().getExtensibilityElements()) {
//                if (o instanceof javax.wsdl.extensions.schema.Schema) {
//                    Element ele = ((javax.wsdl.extensions.schema.Schema) o).getElement();
//                    if ("schema".equals(ele.getLocalName())) {
//                        Node newNode = doc.importNode(ele, true);
//                        doc.appendChild(newNode);                 
//                    }
//                }
//            }
//        }

        File file = new File(resource);
        JAXBContext jaxbContext = JAXBContext.newInstance(TDefinitions.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement e = (JAXBElement) jaxbUnmarshaller.unmarshal(file);
        TDefinitions def = (TDefinitions) e.getValue();
        for (TDocumented d : def.getAnyTopLevelOptionalElement()) {
            if (d instanceof TTypes) {
                TTypes t = (TTypes) d;
                for (Object o : t.getAny()) {
                    Element ele = (Element) o;
                    if ("schema".equals(ele.getLocalName())) {
                        Node newNode = doc.importNode(ele, true);
                        doc.appendChild(newNode);                 
                    }
                }
            }
        }
        
        writeToFile(doc, new File(result));  
        return result;
    }
    
    public static QName getQName(XSModel xsModel, String root) {
        String namespace = getNamespace(xsModel, root);        
        return new QName(namespace, root);
    }
    
    public static String getNamespace(XSModel xsModel, String root) {
        String namespace = null;
        XSNamedMap m1 = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
        if (m1 != null) {
            for (int i = 0; i < m1.getLength(); i++) {                
                XSObject o = m1.item(i);
//                System.out.println("DEFINITION: " + o.getName());
                if (root.equals(o.getName())) {
                    namespace = o.getNamespace();
                }
            }
        }
        return namespace;
    }
    
    public static void writeToFile(Document document, File file) throws Exception {
        // XML to string
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
        // XML to string
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.transform(source, new StreamResult(writer));
    }
    
    public static void toString(Document document) throws Exception {
        StringWriter sw = new StringWriter();
        write(document, sw);
        System.out.println(sw.toString());
    }
    
}
