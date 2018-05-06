package org.lorislab.corn.xml;

import java.util.Enumeration;
import java.util.Properties;
import javax.xml.XMLConstants;
import org.xml.sax.helpers.NamespaceSupport;

public class MyNamespaceSupport extends NamespaceSupport {

    public static final Properties SUG = new Properties();
    
    static{
        SUG.put("", "");
        /** The official XML Namespace name URI */
        SUG.put("xml", XMLConstants.XML_NS_URI);
        /** Namespace URI used by the official XML attribute used for specifying XML Namespace declarations <code>"xmlns"</code> */
        SUG.put("xmlns", XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        /** Schema namespace as defined by XSD **/
        SUG.put("xsd", XMLConstants.W3C_XML_SCHEMA_NS_URI);
        /** Instance namespace as defined by XSD **/
        SUG.put("xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        /** Namespace used by XSL Documents */
        SUG.put("xsl", "http://www.w3.org/1999/XSL/Transform");
        SUG.put("bpws", "http://schemas.xmlsoap.org/ws/2003/03/business-process/");
        SUG.put("xhtml", "http://www.w3.org/1999/xhtml");
        SUG.put("xpath", "http://www.w3.org/TR/1999/REC-xpath-19991116");
        SUG.put("plink", "http://schemas.xmlsoap.org/ws/2003/05/partner-link/");
        /** WSDL namespace for WSDL framework **/
        SUG.put("wsdl", "http://schemas.xmlsoap.org/wsdl/");
        /** WSDL namespace for WSDL HTTP GET & POST binding **/
        SUG.put("http", "http://schemas.xmlsoap.org/wsdl/http/");
        /** WSDL namespace for WSDL MIME binding **/
        SUG.put("mime", "http://schemas.xmlsoap.org/wsdl/mime/");
        /**	WSDL namespace for WSDL SOAP 1.1 binding **/
        SUG.put("soap", "http://schemas.xmlsoap.org/wsdl/soap/");
        /**	WSDL namespace for WSDL SOAP 1.2 binding **/
        SUG.put("soap12", "http://schemas.xmlsoap.org/wsdl/soap12/");
        /** Envelope namespace as defined by SOAP 1.1 **/
        SUG.put("", "http://schemas.xmlsoap.org/soap/envelope/");
        /** Envelope namespace as defined by SOAP 1.2 **/
        SUG.put("soap12env", "http://www.w3.org/2003/05/soap-envelope");
        /** Encoding namespace as defined by SOAP 1.1 **/
        SUG.put("soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        /** Encoding namespace as defined by SOAP 1.2 **/
        SUG.put("soap12enc", "http://www.w3.org/2003/05/soap-encoding");        
    }
    
    private Properties suggested;
    
    private String suggestPrefix = "ns";
        
    public MyNamespaceSupport(){
        this.suggested = new Properties(SUG);
    }

    public void suggestPrefix(String prefix, String uri){
        suggested.put(uri, prefix);
    }

    public String findPrefix(String uri){
        if(uri==null)
            uri = "";
        String prefix = getPrefix(uri);
        if(prefix==null){
            String defaultURI = getURI("");
            if(defaultURI==null)
                defaultURI = "";
            if(uri.equals(defaultURI))
                prefix = "";
        }
        return prefix;
    }

    public String findURI(String prefix){
        if(prefix==null)
            return "";
        String uri = getURI(prefix);
        if(uri==null){
            if(prefix.isEmpty())
                return "";
        }
        return uri;
    }

    public String declarePrefix(String uri){
        String prefix = findPrefix(uri);
        if(prefix==null){
            if(uri.isEmpty())
                prefix = ""; // non-empty prefix cannot be used for empty namespace
            else{
                prefix = suggested.getProperty(uri, suggestPrefix);
                if(getURI(prefix)!=null){
                    if(prefix.isEmpty())
                        prefix = "ns";
                    int i = 1;
                    String _prefix;
                    while(true){
                        _prefix = prefix + i;
                        if(getURI(_prefix)==null){
                            prefix = _prefix;
                            break;
                        }
                        i++;
                    }
                }
            }
            declarePrefix(prefix, uri);
        }
        return prefix;
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Enumeration<String> getPrefixes(String uri){
        return super.getPrefixes(uri);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Enumeration<String> getPrefixes(){
        return super.getPrefixes();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public Enumeration<String> getDeclaredPrefixes(){
        return super.getDeclaredPrefixes();
    }
}
