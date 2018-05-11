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

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMResult;
import nl.flotsam.xeger.Xeger;
import org.apache.xerces.impl.xs.util.StringListImpl;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSConstants;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSFacet;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSMultiValueFacet;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSNamespaceItem;
import org.apache.xerces.xs.XSNamespaceItemList;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.XSWildcard;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Generator {

    private Document document;

    private XMLDocument doc;

    private XSModel xsModel;

    private final Stack<StackItem> stack = new Stack<>();

    private String xsiSchemaLocation;
    private String xsiNoNamespaceSchemaLocation;
    private GeneratorConfig config;

    private Map<String, Integer> counters = new HashMap<>();

    public Generator(GeneratorConfig config, XSDDefinition xsdDefinition) {
        try {
            if (config == null) {
                this.config = new GeneratorConfig();
            } else {
                this.config = config;
            }
            DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = db.newDocument();
            DOMResult output = new DOMResult(document);
            doc = new XMLDocument(output, true, 8, null);
            xsModel = xsdDefinition.getXsModel();
                     
        } catch (Exception ex) {
            throw new RuntimeException("Impossible error", ex);
        }
    }

    public GeneratorConfig getConfig() {
        return config;
    }

    public Document getDocument() {
        return document;
    }
    
    public Document generate(String namespace, String rootName, Map<String, Object> definition) {
        QName rootElement;
        if (namespace == null) {
            rootElement = getQName(xsModel, rootName);
        } else {
            rootElement = new QName(namespace, rootName);
        }

        String ns = rootElement.getNamespaceURI();
        XSElementDeclaration root = xsModel.getElementDeclaration(rootElement.getLocalPart(), ns);
        if (root == null) {
            throw new IllegalArgumentException("Element " + rootElement + " is not found");
        }

        try {
            doc.startDocument();
            doc.declarePrefix(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
            if (rootElement.getPrefix() != null && !rootElement.getPrefix().isEmpty() && !rootElement.getNamespaceURI().isEmpty()) {
                doc.declarePrefix(rootElement.getPrefix(), rootElement.getNamespaceURI());
            }

            XSNamespaceItemList namespaceItems = xsModel.getNamespaceItems();
            for (int i = 0; i < namespaceItems.getLength(); i++) {
                XSNamespaceItem namespaceItem = namespaceItems.item(i);
                if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(namespaceItem.getSchemaNamespace())) {
                    doc.declarePrefix(namespaceItem.getSchemaNamespace());
                }
            }

            stack.push(new StackItem(root, root.getName(), definition));
            generate();
            doc.endDocument();
        } catch (SAXException ex) {
            throw new RuntimeException("Impossible error", ex);
        }
        return document;
    }

    private void generate() {
        try {
            while (!stack.isEmpty()) {
                StackItem item = stack.pop();
                Object elem = item.getData();

                boolean notMacRecursion = !(item.getDefinitionLevel() > config.maximumRecursionDepth);
//                XSObject oo = (XSObject) elem;
//                System.out.println(oo.getName());
                    
                if (!item.isFirst()) {

                    item.setFirst(true);
                    stack.push(item);

                    // begin phase   
                    if (elem instanceof XSElementDeclaration) {
                        XSElementDeclaration item2 = (XSElementDeclaration) elem;
                        if (notMacRecursion) {
                            doc.startElement(item2.getNamespace(), item2.getName());
                            addXSILocations();

                            // add the children
                            boolean added = false;
                            if (item2.getAbstract()) {
                                XSObjectList substitutionGroup = xsModel.getSubstitutionGroup(item2);
                                if (!(substitutionGroup.getLength() == 0)) {
                                    int rand = RandomUtil.random(0, substitutionGroup.getLength() - 1);
                                    stack.push(new StackItem(substitutionGroup.item(rand), item));
                                    added = true;
                                }
                            }
                            if (!added && item2.getTypeDefinition() instanceof XSComplexTypeDefinition) {
                                XSComplexTypeDefinition complexType = (XSComplexTypeDefinition) item2.getTypeDefinition();
                                if (complexType.getAbstract()) {
                                    List<XSComplexTypeDefinition> subTypes = getSubTypes(xsModel, complexType);
                                    if (!subTypes.isEmpty()) {
                                        int rand = RandomUtil.random(0, subTypes.size() - 1);
                                        stack.push(new StackItem(subTypes.get(rand), item));
                                        added = true;
                                    }
                                }
                            }
                            if (!added) {
                                stack.push(new StackItem(item2.getTypeDefinition(), item));
                            }
                        }
                    } else if (elem instanceof XSWildcard) {
                        preProcessXSWildcard((XSWildcard) elem, item.getParent().getData());

                        // add the children
                    } else if (elem instanceof XSComplexTypeDefinition) {
                        XSComplexTypeDefinition complexType = (XSComplexTypeDefinition) elem;
                        XSElementDeclaration elem1 = (XSElementDeclaration) item.getParent().getData();
                        XSComplexTypeDefinition elemType = (XSComplexTypeDefinition) elem1.getTypeDefinition();
                        if (elemType.getAbstract()) {
                            doc.addAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type", doc.toQName(complexType.getNamespace(), complexType.getName()));
                        }

                        // add the children
                        for (int i = 0; i < complexType.getAttributeUses().getLength(); i++) {
                            stack.push(new StackItem(complexType.getAttributeUses().get(i), item));
                        }
                        if (complexType.getAttributeWildcard() != null) {
                            stack.push(new StackItem(complexType.getAttributeWildcard(), item));
                        }
                        if (complexType.getParticle() != null) {
                            stack.push(new StackItem(complexType.getParticle(), item));
                        }

                    } else if (elem instanceof XSAttributeUse) {
                        preProcessXSAttributeUse((XSAttributeUse) elem);
                    } else if (elem instanceof XSParticle) {

                        boolean added = false;
                        XSParticle particle = (XSParticle) elem;
                        XSTerm term = particle.getTerm();
                        if (term instanceof XSModelGroup) {
                            XSModelGroup group = (XSModelGroup) term;
                            if (group.getCompositor() == XSModelGroup.COMPOSITOR_CHOICE) {
                                XSObjectList particles = group.getParticles();
                                int count = particles.getLength();
                                if (!config.generateAllChoices && !particle.getMaxOccursUnbounded()) {
                                    count = Math.min(count, particle.getMaxOccurs());
                                }

                                List<XSParticle> list = new ArrayList<>(particles.getLength());
                                List<XSParticle> listNo = new ArrayList<>(particles.getLength());

                                Map<String, Object> definition = item.getParentElement().getDefinition();
                                if (definition != null && !definition.isEmpty()) {
                                    for (int i = 0; i < particles.getLength(); i++) {
                                        XSParticle p = (XSParticle) particles.item(i);
                                        if (definition.containsKey(p.getTerm().getName())) {
                                            list.add(p);
                                        } else {
                                            listNo.add(p);
                                        }
                                    }
                                } else {
                                    for (int i = 0; i < particles.getLength(); i++) {
                                        list.add((XSParticle) particles.item(i));
                                    }
                                    Collections.shuffle(list);
                                }

                                if (list.size() < count && !listNo.isEmpty()) {
                                    int tmp = count - list.size();
                                    Collections.shuffle(listNo);
                                    for (int i = 0; i < listNo.size() && i < tmp; i++) {
                                        list.add(listNo.get(i));
                                    }
                                }

                                for (int i = 0; i < count; i++) {
                                    stack.push(new StackItem(list.get(i), item));
                                }
                                added = true;
                            }
                        }
                        if (!added) {
                            int maxOccurs = particle.getMaxOccursUnbounded() ? -1 : particle.getMaxOccurs();
                            int repeatCount = generateRepeatCount(config, particle.getMinOccurs(), maxOccurs);
                            if (term instanceof XSElementDeclaration) {

                                boolean add = false;
                                
                                if (item.isParentElemet()) {
                                    Map<String, Object> def = item.getParentElement().getDefinition();
                                    if (def != null) {
                                                                              
                                        Object val = def.get(term.getName());
                                        if (val != null) {
                                            if (val instanceof Map) {
                                                Map<String, Object> definition = (Map<String, Object>) val;
                                                stack.push(new StackItem(term, item, term.getName(), definition, null));
                                                add = true;
                                            } else if (val instanceof List) {
                                                List valueList = (List) val;
                                                // TODO: valueList find item with value _size and _index
                                                
                                                for (int i = 0; i < valueList.size(); i++) {
                                                    Object t = valueList.get(i);
                                                    Object value = null;
                                                    Map<String, Object> definition = null;
                                                    if (t instanceof Map) {
                                                        definition = (Map<String, Object>) t;
                                                    } else {
                                                        value = t;
                                                    }
                                                    stack.push(new StackItem(term, item, term.getName(), definition, value));
                                                    add = true;
                                                }                                                
                                            } else {
                                                stack.push(new StackItem(term, item, term.getName(), null, val));
                                                add = true;
                                            }
                                        }
                                    }
                                }

                                if (!add) {
                                    for (int i = 0; i < repeatCount; i++) {
                                        stack.push(new StackItem(term, item, term.getName(), null, null));
                                    }
                                }
                            } else {
                                for (int i = 0; i < repeatCount; i++) {
                                    stack.push(new StackItem(term, item));
                                }
                            }

                        }
                    } else if (elem instanceof XSModelGroup) {
                        XSModelGroup modelGroup = (XSModelGroup) elem;
                        if (modelGroup.getCompositor() == XSModelGroup.COMPOSITOR_ALL) {
                            XSObjectList particles = modelGroup.getParticles();
                            List<XSParticle> list = new ArrayList<>(particles.getLength());
                            for (int i = 0; i < particles.getLength(); i++) {
                                list.add((XSParticle) particles.item(i));
                            }
                            Collections.shuffle(list);
                            for (XSParticle p : list) {
                                stack.push(new StackItem(p, item));
                            }
                        } else {
                            XSObjectList tmp = modelGroup.getParticles();
                            for (int i=tmp.getLength()-1; 0<=i; i--) {
                                stack.push(new StackItem(tmp.item(i), item));
                            }
                        }
                    } else if (elem instanceof XSNamespaceItem) {
                        XSNamespaceItem data = (XSNamespaceItem) elem;
                        XSNamedMap map = data.getComponents(XSConstants.ELEMENT_DECLARATION);
                        for (int i = 0; i < map.getLength(); i++) {
                            stack.push(new StackItem(map.item(i), item));
                        }
                    } else if (elem instanceof XSModel) {
                        XSModel data = (XSModel) elem;
                        XSNamespaceItemList tmp = data.getNamespaceItems();
                        for (int i = 0; i < tmp.getLength(); i++) {
                            stack.push(new StackItem(tmp.item(i), item));
                        }
                    } else {
//                        System.out.println("INGORE children: " + elem.getClass());
                    }

                } else {

                    if (elem instanceof XSElementDeclaration) {
                        if (notMacRecursion) {
//                            System.out.println("GENERATE: " + item.getDefinitionLevel() + "/" + item.getLevel() + " element " + item.getName() + " parent " + item.getParentElementName());
                            postProcessXSElementDeclaration((XSElementDeclaration) elem, item);
                        }
                    } else if (elem instanceof XSWildcard) {
                        if (!isAttribute((XSWildcard) elem, item.getParent().getData())) {
                            doc.endElement();
                        }
                    }
                }
            }

        } catch (Exception ex) {
            throw new RuntimeException("This is impossible", ex);
        }
    }

    private void addXSILocations() throws SAXException {
        if (doc.getDepth() == 1) {
            if (xsiSchemaLocation != null) {
                doc.addAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "schemaLocation", xsiSchemaLocation);
            }
            if (xsiNoNamespaceSchemaLocation != null) {
                doc.addAttribute(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "noNamespaceSchemaLocation", xsiNoNamespaceSchemaLocation);
            }
        }
    }

    private void postProcessXSElementDeclaration(XSElementDeclaration elem, StackItem item) throws Exception {
        switch (elem.getConstraintType()) {
            case XSConstants.VC_FIXED:
                doc.addText(elem.getValueConstraintValue().getNormalizedValue());
                break;
            case XSConstants.VC_DEFAULT:
                if (RandomUtil.randomBoolean(config.generateDefaultElementValues)) {
                    doc.addText(elem.getValueConstraintValue().getNormalizedValue());
                    break;
                }
            default:
                XSSimpleTypeDefinition simpleType = null;
                if (elem.getTypeDefinition().getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
                    simpleType = (XSSimpleTypeDefinition) elem.getTypeDefinition();
                } else {
                    XSComplexTypeDefinition complexType = (XSComplexTypeDefinition) elem.getTypeDefinition();
                    if (complexType.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_SIMPLE) {
                        simpleType = complexType.getSimpleType();
                    }
                }
                if (simpleType != null) {
                    String sampleValue;
                    if (item.getValue() != null) {
                        sampleValue = "" + item.getValue();
                    } else {
                        sampleValue = generateSampleValue(config, simpleType, elem.getName(), counters);
                    }
                    doc.addText(sampleValue);
                }
        }
        doc.endElement();
    }

    private void preProcessXSAttributeUse(XSAttributeUse attr) throws Exception {
        XSAttributeDeclaration decl = attr.getAttrDeclaration();

        String sampleValue = null;
        switch (attr.getConstraintType()) {
            case XSConstants.VC_FIXED:
                if (RandomUtil.randomBoolean(config.generateFixedAttributes)) {
                    sampleValue = attr.getValueConstraintValue().getNormalizedValue();
                }
                break;
            case XSConstants.VC_DEFAULT:
                if (RandomUtil.randomBoolean(config.generateDefaultAttributes)) {
                    sampleValue = attr.getValueConstraintValue().getNormalizedValue();
                }
                break;
            default:
                if (attr.getRequired() || RandomUtil.randomBoolean(config.generateOptionalAttributes)) {
                    if (sampleValue == null) {
                        sampleValue = generateSampleValue(config, decl.getTypeDefinition(), decl.getName(), counters);
                    }
                }
        }
        if (sampleValue != null) {
            doc.addAttribute(decl.getNamespace(), decl.getName(), sampleValue);
        }
    }

    private void preProcessXSWildcard(XSWildcard wildcard, Object parentData) throws Exception {
        String uri;
        switch (wildcard.getConstraintType()) {
            case XSWildcard.NSCONSTRAINT_ANY:
                uri = "anyNS";
                break;
            case XSWildcard.NSCONSTRAINT_LIST:
                StringList list = wildcard.getNsConstraintList();
                int rand = RandomUtil.random(0, list.getLength() - 1);
                uri = list.item(rand);
                if (uri == null) {
                    uri = ""; // <xs:any namespace="##local"/> returns nsConstraintList with null
                }
                break;
            case XSWildcard.NSCONSTRAINT_NOT:
                list = wildcard.getNsConstraintList();
                List<String> namespaces = new ArrayList<>();
                for (int i = 0; i < list.getLength(); i++) {
                    namespaces.add(list.item(i));
                }
                uri = "anyNS";
                if (namespaces.contains(uri)) {
                    for (int i = 1;; i++) {
                        if (!namespaces.contains(uri + i)) {
                            uri += i;
                            break;
                        }
                    }
                }
                break;
            default:
                throw new RuntimeException("This is impossible");
        }
        if (isAttribute(wildcard, parentData)) {
            doc.addAttribute(uri, "anyAttr", "anyValue");
        } else {
            doc.startElement(uri, "anyElement");
            addXSILocations();
        }
    }

    private boolean isAttribute(XSWildcard wildcard, Object parentData) {
        if (parentData instanceof XSComplexTypeDefinition) {
            XSComplexTypeDefinition complexType = (XSComplexTypeDefinition) parentData;
            if (complexType.getAttributeWildcard() == wildcard) {
                return true;
            }
        }
        return false;
    }

    public static List<XSComplexTypeDefinition> getSubTypes(XSModel xsModel, XSComplexTypeDefinition complexType) {
        List<XSComplexTypeDefinition> subTypes = new ArrayList<>();
        XSNamedMap namedMap = xsModel.getComponents(XSConstants.TYPE_DEFINITION);
        XSObject anyType = namedMap.itemByName(XMLConstants.W3C_XML_SCHEMA_NS_URI, "anyType");
        for (int i = 0; i < namedMap.getLength(); i++) {
            XSObject item = namedMap.item(i);
            if (item instanceof XSComplexTypeDefinition) {
                XSComplexTypeDefinition complexItem = (XSComplexTypeDefinition) item;
                if (!complexItem.getAbstract()) {
                    do {
                        complexItem = (XSComplexTypeDefinition) complexItem.getBaseType();
                    } while (complexItem != anyType && complexItem != complexType);
                    if (complexItem == complexType) {
                        subTypes.add((XSComplexTypeDefinition) item);
                    }
                }
            }
        }
        return subTypes;
    }

    public static int generateRepeatCount(GeneratorConfig config, int minOccurs, int maxOccurs) {
        if (minOccurs == 0 && maxOccurs == 1) //optional case
        {
            return RandomUtil.randomBoolean(config.generateOptionalElements) ? 1 : 0;
        }

        if (maxOccurs == -1) {
            maxOccurs = Math.max(minOccurs, config.maximumElementsGenerated);
        }

        int min, max;
        if (config.minimumElementsGenerated > maxOccurs || config.maximumElementsGenerated < minOccurs) { // doesn't intersect
            min = minOccurs;
            max = maxOccurs;
        } else { // find intersecting range
            min = Math.max(minOccurs, config.minimumElementsGenerated);
            max = Math.min(maxOccurs, config.maximumElementsGenerated);
        }
        return (min == max)
                ? min
                : RandomUtil.random(min, max);
    }

    public static String generateSampleValue(GeneratorConfig config, XSSimpleTypeDefinition simpleType, String hint, Map<String, Integer> counters) {
        if (simpleType.getBuiltInKind() == XSConstants.LIST_DT) {
            XSSimpleTypeDefinition itemType = simpleType.getItemType();

            int len;
            XSFacet facet = getFacet(itemType, XSSimpleTypeDefinition.FACET_LENGTH);
            if (facet != null) {
                len = Integer.parseInt(facet.getLexicalFacetValue());
            } else {
                int minOccurs = 0;
                facet = getFacet(itemType, XSSimpleTypeDefinition.FACET_MINLENGTH);
                if (facet != null) {
                    minOccurs = Integer.parseInt(facet.getLexicalFacetValue());
                }
                int maxOccurs = -1;
                facet = getFacet(itemType, XSSimpleTypeDefinition.FACET_MAXLENGTH);
                if (facet != null) {
                    maxOccurs = Integer.parseInt(facet.getLexicalFacetValue());
                }

                if (maxOccurs == -1) {
                    maxOccurs = Math.max(minOccurs, config.maximumListItemsGenerated);
                }

                int min, max;
                if (config.minimumListItemsGenerated > maxOccurs || config.maximumListItemsGenerated < minOccurs) { // doesn't intersect
                    min = minOccurs;
                    max = maxOccurs;
                } else { // find intersecting range
                    min = Math.max(minOccurs, config.minimumListItemsGenerated);
                    max = Math.min(maxOccurs, config.maximumListItemsGenerated);
                }
                len = (min == max)
                        ? min
                        : RandomUtil.random(min, max);
            }

            List<String> enums = getEnumeratedValues(itemType);
            if (enums.isEmpty()) {
                StringBuilder buff = new StringBuilder();
                while (len > 0) {
                    buff.append(" ");
                    buff.append(generateSampleValue(config, itemType, hint, counters));
                    len--;
                }
                return buff.toString().trim();
            } else {
                while (enums.size() < len) {
                    enums.addAll(new ArrayList<>(enums));
                }
                Collections.shuffle(enums);

                StringBuilder buff = new StringBuilder();
                while (len > 0) {
                    buff.append(" ");
                    buff.append(enums.remove(0));
                    len--;
                }
                return buff.toString().trim();
            }
        } else if (simpleType.getMemberTypes().getLength() > 0) {
            XSObjectList members = simpleType.getMemberTypes();
            int rand = RandomUtil.random(0, members.getLength() - 1);
            return generateSampleValue(config, (XSSimpleTypeDefinition) members.item(rand), hint, counters);
        }

        List<String> enums = getEnumeratedValues(simpleType);
        if (!enums.isEmpty()) {
            return enums.get(RandomUtil.random(0, enums.size() - 1));
        }

        XSSimpleTypeDefinition builtInType = simpleType;
        while (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(builtInType.getNamespace())) {
            builtInType = (XSSimpleTypeDefinition) builtInType.getBaseType();
        }

        String name = builtInType.getName().toLowerCase();
        if ("boolean".equals(name)) {
            return RandomUtil.randomBoolean() ? "true" : "false";
        }

        if ("double".equals(name)
                || "decimal".equals(name)
                || "float".equals(name)
                || name.endsWith("integer")
                || name.endsWith("int")
                || name.endsWith("long")
                || name.endsWith("short")
                || name.endsWith("byte")) {
            return randomNumber(simpleType, name);
        }

        if ("date".equals(name)) {
            return new SimpleDateFormat(config.dateFormat).format(new Date());
        }
        if ("time".equals(name)) {
            return new SimpleDateFormat(config.timeFormat).format(new Date());
        }
        if ("datetime".equals(name)) {
            Date date = new Date();
            return new SimpleDateFormat(config.dateFormat).format(date) + 'T' + new SimpleDateFormat(config.timeFormat).format(date);
        } else {
            Integer count = counters.get(hint);
            count = count == null ? 1 : ++count;
            counters.put(hint, count);
            String countStr = count.toString();

            XSFacet lengthFacet = getFacet(simpleType, XSSimpleTypeDefinition.FACET_LENGTH);

            XSFacet facet = getFacet(simpleType, XSSimpleTypeDefinition.FACET_MINLENGTH);
            if (facet == null) {
                facet = lengthFacet;
            }
            if (facet != null) {
                int len = Integer.parseInt(facet.getLexicalFacetValue());
                len -= hint.length();
                len -= countStr.length();
                if (len > 0) {
                    char ch[] = new char[len];
                    Arrays.fill(ch, '_');
                    hint += new String(ch);
                }
            }
            facet = getFacet(simpleType, XSSimpleTypeDefinition.FACET_MAXLENGTH);
            if (facet == null) {
                facet = lengthFacet;
            }
            if (facet != null) {
                int maxLen = Integer.parseInt(facet.getLexicalFacetValue());
                int len = maxLen;
                len = hint.length() + countStr.length() - len;
                if (len > 0) {
                    if (hint.length() > len) {
                        hint = hint.substring(0, hint.length() - len);
                    } else {
                        hint = hint.substring(0, maxLen);
                        countStr = "";
                    }
                }
            }
            String value = hint + countStr;

            StringList pattern = simpleType.getLexicalPattern();
            if (pattern != null && !pattern.isEmpty()) {
                String v = pattern.item(0);
                v = v.replaceAll("\\.", "\\\\.");
                Xeger xeger = new Xeger(v);
                value = xeger.generate();
            }

            if ("base64binary".equals(name)) {
                return DatatypeConverter.printBase64Binary(value.getBytes(StandardCharsets.UTF_8));
            } else {
                return value;
            }
        }
    }

    private static XSFacet getFacet(XSSimpleTypeDefinition simpleType, int kind) {
        XSObjectList facets = simpleType.getFacets();
        for (int i = 0; i < facets.getLength(); i++) {
            XSFacet facet = (XSFacet) facets.item(i);
            if (facet.getFacetKind() == kind) {
                return facet;
            }
        }
        return null;
    }

    private static String randomNumber(XSSimpleTypeDefinition simpleType, String builtinName) {
        boolean exponentAllowed = "double".equals(builtinName) || "float".equals(builtinName);

        String minInclusive = null;
        XSFacet facet = getFacet(simpleType, XSSimpleTypeDefinition.FACET_MININCLUSIVE);
        if (facet != null) {
            minInclusive = facet.getLexicalFacetValue();
        }

        String minExclusive = null;
        facet = getFacet(simpleType, XSSimpleTypeDefinition.FACET_MINEXCLUSIVE);
        if (facet != null) {
            minExclusive = facet.getLexicalFacetValue();
        }

        String maxInclusive = null;
        facet = getFacet(simpleType, XSSimpleTypeDefinition.FACET_MAXINCLUSIVE);
        if (facet != null) {
            maxInclusive = facet.getLexicalFacetValue();
        }

        String maxExclusive = null;
        facet = getFacet(simpleType, XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE);
        if (facet != null) {
            maxExclusive = facet.getLexicalFacetValue();
        }

        int totalDigits = -1;
        facet = getFacet(simpleType, XSSimpleTypeDefinition.FACET_TOTALDIGITS);
        if (facet != null) {
            totalDigits = Integer.parseInt(facet.getLexicalFacetValue());
        }

        int fractionDigits = -1;
        facet = getFacet(simpleType, XSSimpleTypeDefinition.FACET_FRACTIONDIGITS);
        if (facet != null) {
            fractionDigits = Integer.parseInt(facet.getLexicalFacetValue());
        }

        Object randomNumber;
        if (fractionDigits == 0) {
            // NOTE: min/max facets can have fractional part even though fractionDigits is zero
            Long min = null;
            if (minInclusive != null) {
                min = Long.parseLong(minInclusive);
            }
            if (minExclusive != null) {
                min = Long.parseLong(minExclusive) + 1;
            }

            Long max = null;
            if (maxInclusive != null) {
                max = Long.parseLong(maxInclusive);
            }
            if (maxExclusive != null) {
                max = Long.parseLong(maxExclusive) - 1;
            }

            if (min == null && max == null) {
                min = -1000L;
                max = 1000L;
            } else if (min == null) {
                min = Math.max(Long.MIN_VALUE, max - 1000);
            } else if (max == null) {
                max = Math.min(Long.MAX_VALUE, min + 1000);
            }

            randomNumber = RandomUtil.random(min, max);
        } else {
            Double min = null;
            if (minInclusive != null) {
                min = Double.parseDouble(minInclusive);
            }
            if (minExclusive != null) {
                min = Double.parseDouble(minExclusive) + 1;
            }

            Double max = null;
            if (maxInclusive != null) {
                max = Double.parseDouble(maxInclusive);
            }
            if (maxExclusive != null) {
                max = Double.parseDouble(maxExclusive) - 1;
            }

            if (min == null && max == null) {
                min = -1000d;
                max = 1000d;
            } else if (min == null) {
                min = Math.max(Double.MIN_VALUE, max - 1000);
            } else if (max == null) {
                max = Math.min(Double.MAX_VALUE, min + 1000);
            }

            randomNumber = RandomUtil.random(min, max);
        }

        String str;
        if (randomNumber instanceof Double && !exponentAllowed) {
            str = String.format(Locale.US, "%." + (fractionDigits >= 0 ? fractionDigits : 3) + "f", (Double) randomNumber);
        } else {
            str = String.valueOf(randomNumber);
        }
        String number, fraction;
        int dot = str.indexOf(".");
        if (dot == -1) {
            number = str;
            fraction = "";
        } else {
            number = str.substring(0, dot);
            fraction = str.substring(dot + 1);
        }
        boolean negative = false;
        if (number.startsWith("-")) {
            negative = true;
            number = number.substring(1);
        }
        if (totalDigits >= 0) {
            if (number.length() > totalDigits) {
                number = number.substring(0, totalDigits);
            }
        }
        if (fractionDigits >= 0) {
            if (fraction.length() > fractionDigits) {
                fraction = fraction.substring(0, fractionDigits);
            }
        }

        str = negative ? "-" : "";
        str += number;
        if (fraction.length() > 0) {
            str += '.' + fraction;
        }
        return str;
    }

    private static List<String> getEnumeratedValues(XSSimpleTypeDefinition simpleType) {
        ArrayList<String> enums = new ArrayList<>();

        XSObjectList facets = simpleType.getMultiValueFacets();
        if (facets != null) {
            for (int i = 0; i < facets.getLength(); i++) {
                XSMultiValueFacet facet = (XSMultiValueFacet) facets.item(i);
                if (facet.getFacetKind() == XSSimpleTypeDefinition.FACET_ENUMERATION) {
                    StringList values = facet.getLexicalFacetValues();
                    for (int j = 0; j < values.getLength(); j++) {
                        enums.add(values.item(j));
                    }
                }
            }
        }
        return enums;
    }

    private static QName getQName(XSModel xsModel, String root) {
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
    
}
