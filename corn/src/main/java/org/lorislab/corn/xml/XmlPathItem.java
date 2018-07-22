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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author andrej
 */
public class XmlPathItem implements Map {

    private final String xpath;

    private final Document document;

    private final XPathFactory factory;
    
    public XmlPathItem(String xpath, Document document, XPathFactory factory) {
        this.xpath = xpath;
        this.document = document;
        this.factory = factory;
    }

    public static Object getObject(Document document, String path, Object key, XPathFactory factory) {
        try {
            String xpath = createXPath(path, key);
            NodeList nodeList = findNodeList(document, xpath, factory);
            if (nodeList != null && nodeList.getLength() > 0) {
                Node node = nodeList.item(0);
                if (node.hasChildNodes()) {
                    if (node.getChildNodes().item(0) instanceof Text) {
                        return node.getTextContent();
                    }
                    return new XmlPathItem(xpath, document, factory);
                } else {
                    return node.getTextContent();
                }
            }
            return null;
        } catch (Exception ex) {
            throw new RuntimeException("Error reading the xml " + key, ex);
        }
    }

    public static boolean containsObject(Document document, String path, Object key, XPathFactory factory) {
        try {
            NodeList nodeList = findNodeList(document, path, key, factory);
            return nodeList.getLength() != 0;
        } catch (Exception ex) {
            throw new RuntimeException("Error containsKey the xml " + key, ex);
        }
    }

    public static NodeList findNodeList(Document document, String path, Object key, XPathFactory factory) {
        String pathKey = createXPath(path, key);
        return findNodeList(document, pathKey, factory);
    }

    public static NodeList findNodeList(Document document, String xpath, XPathFactory factory) {
        try {
            XPath xPath = factory.newXPath();
            return (NodeList) xPath.evaluate(xpath, document, XPathConstants.NODESET);
        } catch (Exception ex) {
            throw new RuntimeException("Error containsKey the xml " + xpath, ex);
        }
    }

    public static String createXPath(String path, Object key) {
        String result = path;
        if (key != null) {
            if (key instanceof Number) {
                Number n = (Number) key;
                int index = n.intValue();
                result = path + "[" + (index + 1) + "]";
            } else {
                result = path + "/*[local-name()='" + key.toString() + "']";
            }
        }
        return result;
    }

    @Override
    public Object get(Object key) {
        return getObject(document, xpath, key, factory);
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
        NodeList nodeList = findNodeList(document, xpath, null);
        if (nodeList != null) {
            return nodeList.getLength();
        }
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return containsObject(document, xpath, key, factory);
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
