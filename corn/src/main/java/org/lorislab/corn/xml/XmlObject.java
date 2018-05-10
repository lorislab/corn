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
import org.lorislab.corn.model.DataGeneratorItem;
import org.lorislab.corn.model.XmlConfig;
import org.lorislab.corn.model.XmlDefinition;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class XmlObject implements Map {

    private String fileName;

    private final XmlDefinition definition;

    private Document document;

    private boolean wsdl;
    
    private final DataGeneratorItem output;

    private Map<String, Object> data;
        
    private String root;
    
    private String namespace;
    
    public XmlObject(DataGeneratorItem output, XmlDefinition definition) {
        this.output = output;
        this.definition = definition;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getRoot() {
        return root;
    }
    
    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public void generate() {        
        GeneratorConfig config = new GeneratorConfig();
        XmlConfig xc = this.output.config;
        if (xc != null) {
            if (xc.maximumRecursionDepth != null) {
                config.maximumRecursionDepth = xc.maximumRecursionDepth;
            }
        }
        Generator generator = new Generator(config, this.definition.xsds);
        generator.generate(namespace, root, data);
        document = generator.getDocument();
        wsdl = generator.isWsdl();
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
                return new XmlPathItem(nodeList.item(0).getTextContent(), pathKey, document);
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
