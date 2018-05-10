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
import static org.lorislab.corn.log.Logger.debug;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 *
 * @author andrej
 */
public class XmlPathItem implements Map {

    private final String path;

    private final Document document;


    public XmlPathItem(String path, Document document) {
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

            debug(pathKey);
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
        return size() == 0;
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
