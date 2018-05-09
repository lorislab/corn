package org.lorislab.corn.xml;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import static org.lorislab.corn.Logger.debug;
import static org.lorislab.corn.Logger.info;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author andrej
 */
public class XmlPathItem implements Map {

    private String path;

    private Document document;

    private String text;

    public XmlPathItem(String text, String path, Document document) {
        this.text = text;
        this.path = path;
        this.document = document;
    }

    @Override
    public Object get(Object key) {
        try {

            String pathKey = path + "/*[local-name()='" + key.toString() + "']";
            if (key instanceof Integer) {
                int index = (Integer) key;
                pathKey = path + "[" + (index + 1) + "]";
            }

            info(pathKey);
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate(pathKey, document, XPathConstants.NODESET);
            Node node = nodeList.item(0);
            if (node.hasChildNodes()) {
                if (node.getChildNodes().item(0) instanceof Text) {
                    debug("TEXT: " + node.getTextContent());
                    return node.getTextContent();
                }
                return new XmlPathItem(node.getTextContent(), pathKey, document);
            } else {
                debug("RESULT: " + node.getTextContent());
                return node.getTextContent();
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error reading the xml " + key, ex);
        }
    }

    @Override
    public String toString() {
        return text;
    }

    public int getSize() {
        return size();
    }
    
    @Override
    public int size() {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate(path, document, XPathConstants.NODESET);
            return nodeList.getLength();
        } catch (Exception ex) {
            throw new RuntimeException("Error size the xml " + path, ex);
        }
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
//        return true;
        try {
            String pathKey = path + "/*[local-name()='" + key.toString() + "']";
            if (key instanceof Integer) {
                int index = (Integer) key;
                pathKey = path + "[" + (index + 1) + "]";
            }
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList nodeList = (NodeList) xPath.evaluate(pathKey, document, XPathConstants.NODESET);
            return nodeList.getLength() != 0;
        } catch (Exception ex) {
            throw new RuntimeException("Error containsKey the xml " + key, ex);
        }
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
