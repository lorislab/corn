package org.lorislab.corn.xml;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.lorislab.corn.el.Expressions;
import org.lorislab.corn.model.DataGeneratorOutput;
import org.lorislab.corn.model.XmlConfig;
import org.lorislab.corn.model.XmlDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlObject implements Map {

    private String fileName;

    private final XmlDefinition definition;

    private Document document;

    private boolean wsdl;
    
    private final DataGeneratorOutput output;

    public XmlObject(DataGeneratorOutput output, XmlDefinition definition) {
        this.output = output;
        this.definition = definition;
    }

    public boolean isWsdl() {
        return wsdl;
    }

    public XmlDefinition getDefinition() {
        return definition;
    }

    public String getFileName() {
        return fileName;
    }

    public Document getDocument() {
        return document;
    }

    public void generate(Expressions expresion) {        
        GeneratorConfig config = new GeneratorConfig();
        XmlConfig xc = this.output.xml.config;
        if (xc != null) {
            if (xc.maximumRecursionDepth != null) {
                config.maximumRecursionDepth = xc.maximumRecursionDepth;
            }
        }
        Generator generator = new Generator(config, this.definition.xsds) {
            @Override
            protected Object evaluate(Object value) {
                return expresion.evaluate(value);
            }

        };
        generator.generate(output.xml.namespace, output.xml.root, output.xml.content);
        document = generator.getDocument();
        wsdl = generator.isWsdl();
        fileName = expresion.evaluateAllValueExpressions(output.file);
    }

    @Override
    public Object get(Object key) {
        if ("definition".equals(key)) {
            return getDefinition();
        }
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
            if (nodeList.getLength() == 1) {
                    return new XmlPathItem(nodeList.item(0).getTextContent(), pathKey, document);
            } else if (nodeList.getLength() > 1) {
                return new XmlPathList(nodeList, pathKey, document);
            }
            return null;
        } catch (Exception ex) {
            throw new RuntimeException("Error reading the xml " + key, ex);
        }
    }

    public static Object convert(Object data) {
        if (data instanceof NodeList) {
            
        }
        return null;
    }
    
    @Override
    public int size() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEmpty() {
        return document == null;
    }

    @Override
    public boolean containsKey(Object key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object put(Object key, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void putAll(Map m) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set keySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection values() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set entrySet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
