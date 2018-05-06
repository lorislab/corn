package org.lorislab.corn.xml;

import java.util.Enumeration;
import java.util.Stack;
import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLDocument {
    
    private TransformerHandler xml;
    private MyNamespaceSupport nsSupport = new MyNamespaceSupport();
    private boolean needsNewContext = true;

    private Stack<QName> elemStack = new Stack<QName>();
    private QName elem;
    private int marks = -1;
    int depth = 0;  // document is at 0; root is at 1
    
    public static final String OUTPUT_KEY_INDENT_AMOUT = "{http://xml.apache.org/xslt}indent-amount";
    public static String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    
    public static String LEXICAL_HANDLER_ALT = "http://xml.org/sax/handlers/LexicalHandler";

    public static String DECL_HANDLER = "http://xml.org/sax/properties/declaration-handler";
    
    public static String DECL_HANDLER_ALT = "http://xml.org/sax/handlers/DeclHandler"; 
    
    public static String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    
    private AttributesImpl attrs = new AttributesImpl();
    

    public XMLDocument(Result result, boolean omitXMLDeclaration, int indentAmount, String encoding) throws TransformerConfigurationException{
        TransformerHandler handler = newTransformerHandler(null, omitXMLDeclaration, indentAmount, encoding);
        handler.setResult(result);
        xml = handler;
    }
    
    /**
     * Creates TransformerHandler
     *
     * @param source                source of xsl document, use null for identity transformer
     * @param omitXMLDeclaration    omit xml declaration or not
     * @param indentAmount          the number fo spaces used for indentation.
     *                              use <=0, in case you dont want indentation
     * @param encoding              required encoding. use null to don't set any encoding
     *
     * @return the same transformer which is passed as argument
     */
    private static TransformerHandler newTransformerHandler(Source source, boolean omitXMLDeclaration, int indentAmount, String encoding) throws TransformerConfigurationException{
        SAXTransformerFactory factory = (SAXTransformerFactory)TransformerFactory.newInstance();
        TransformerHandler handler = source!=null ? factory.newTransformerHandler(source) : factory.newTransformerHandler();
        setOutputProperties(handler.getTransformer(), omitXMLDeclaration, indentAmount, encoding);
        return handler;
    }
    
   /**
     * to set various output properties on given transformer.
     *
     * @param transformer           transformer on which properties are set
     * @param omitXMLDeclaration    omit xml declaration or not
     * @param indentAmount          the number fo spaces used for indentation.
     *                              use <=0, in case you dont want indentation
     * @param encoding              required encoding. use null to don't set any encoding
     *
     * @return the same transformer which is passed as argument
     */
    private static Transformer setOutputProperties(Transformer transformer, boolean omitXMLDeclaration, int indentAmount, String encoding){
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, omitXMLDeclaration ? "yes" : "no");

        // indentation
        if(indentAmount>0){
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OUTPUT_KEY_INDENT_AMOUT, String.valueOf(indentAmount));
        }

        if(!isWhitespace(encoding))
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding.trim());

        return transformer;
    }    
    
    private static boolean isWhitespace(CharSequence str){
        if(str!=null){
            for(int i=0; i<str.length(); i++){
                if(!Character.isWhitespace(str.charAt(i)))
                    return false;
            }
        }
        return true;
    }

    public XMLDocument startDocument() throws SAXException{
        nsSupport.reset();
        attrs.clear();
        elemStack.clear();
        elem = null;
        depth = 0;
        nsSupport.pushContext();

        xml.startDocument();
        mark();
        return this;
    }

    public XMLDocument endDocument() throws SAXException{
        release(0);
        xml.endDocument();
        return this;
    }


    public MyNamespaceSupport getNamespaceSupport(){
        return nsSupport;
    }

    public String declarePrefix(String uri){
        if(uri==null)
            uri = "";
        String prefix = nsSupport.findPrefix(uri);
        if(prefix==null){
            if(needsNewContext){
                nsSupport.pushContext();
                needsNewContext = false;
            }
            prefix = nsSupport.declarePrefix(uri);
        }
        return prefix;
    }

    public boolean declarePrefix(String prefix, String uri){
        if(needsNewContext){
            nsSupport.pushContext();
            needsNewContext = false;
        }
        return nsSupport.declarePrefix(prefix, uri);
    }

    private QName declareQName(String uri, String localPart){
        if(uri==null)
            uri = "";
        return new QName(uri, localPart, declarePrefix(uri));
    }

    public String toQName(String uri, String localPart){
        if(uri==null)
            uri = "";
        String prefix = declarePrefix(uri);
        return prefix.length()==0 ? localPart : prefix+':'+localPart;
    }

    private String toAttrQName(String uri, String localPart){
        return uri.equals("") ? localPart : toQName(uri, localPart);
    }

    private void startPrefixMappings() throws SAXException{
        Enumeration enumer = nsSupport.getDeclaredPrefixes();
        while(enumer.hasMoreElements()){
            String prefix = (String) enumer.nextElement();
            xml.startPrefixMapping(prefix, nsSupport.findURI(prefix));
        }
    }

    private void endPrefixMappings() throws SAXException{
        Enumeration enumer = nsSupport.getDeclaredPrefixes();
        while(enumer.hasMoreElements())
            xml.endPrefixMapping((String)enumer.nextElement());
    }

    public int getDepth(){
        return depth;
    }

    
    private int mark() throws SAXException{
        finishStartElement();
        elemStack.push(null);
        return ++marks;
    }

    private int release() throws SAXException{
        if(marks==-1 || elemStack.empty())
            throw new SAXException("no mark found to be released");
        endElements();
        if(elemStack.peek()!=null)
            throw new SAXException("expected </"+toString(elemStack.peek())+'>');
        elemStack.pop();
        return --marks;
    }

    private void release(int mark) throws SAXException{
        while(marks>=mark)
            release();
    }

    private String toString(QName qname){
        return qname.getPrefix().length()==0 ? qname.getLocalPart() : qname.getPrefix()+':'+qname.getLocalPart();
    }

    private void finishStartElement() throws SAXException{
        if(elem!=null){
            if(needsNewContext)
                nsSupport.pushContext();
            else
                needsNewContext = true;
            startPrefixMappings();

            elemStack.push(elem);
            xml.startElement(elem.getNamespaceURI(), elem.getLocalPart(), toString(elem), attrs);
            elem = null;
            attrs.clear();
        }
    }

    public XMLDocument startElement(String uri, String name) throws SAXException{
        if(uri==null)
            uri = "";
        finishStartElement();
        elem = declareQName(uri, name);
        depth++;
        return this;
    }

    public XMLDocument addAttribute(String uri, String name, String value) throws SAXException{
        if(elem==null)
            throw new SAXException("no start element found to associate this attribute");
        if(value!=null){
            if(uri==null)
                uri = "";
            attrs.addAttribute(uri, name, toAttrQName(uri, name), "CDATA", value);
        }
        return this;
    }

    public XMLDocument addText(String text) throws SAXException{
        if(text != null && !text.isEmpty()){
            finishStartElement();
            xml.characters(text.toCharArray(), 0, text.length());
        }
        return this;
    }

    private QName findEndElement() throws SAXException{
        finishStartElement();
        if(elemStack.empty() || elemStack.peek()==null)
            throw new SAXException("can't find matching start element");
        return elemStack.pop();
    }

    private XMLDocument endElement(QName qname) throws SAXException{
        xml.endElement(qname.getNamespaceURI(), qname.getLocalPart(), toString(qname));
        depth--;

        endPrefixMappings();
        nsSupport.popContext();
        needsNewContext = true;
        return this;
    }

    public XMLDocument endElement() throws SAXException{
        return endElement(findEndElement());
    }

    private XMLDocument endElements() throws SAXException{
        finishStartElement();
        while(!elemStack.empty() && elemStack.peek()!=null)
            endElement();
        return this;
    }

    public XMLDocument addComment(String text) throws SAXException{
        if(text != null && !text.isEmpty()){
            finishStartElement();
            xml.comment(text.toCharArray(), 0, text.length());
        }
        return this;
    }

}
