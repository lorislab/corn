package org.lorislab.corn.xml.validator;

import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.List;
import javax.wsdl.Definition;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlValidator {

    private static SchemaFactory SF = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);

    public static void validate(Path path, List<String> resources) {
        try {
            Schema schema = createSchema(resources);
            Validator validator = schema.newValidator();
            String xml = new String(Files.readAllBytes(path));
//        if (handler != null) {
            validator.setErrorHandler(new XmlErrorHandler(path));
//        }
            StringReader reader = new StringReader(xml);
            validator.validate(new StreamSource(reader));
        } catch (Exception ex) {
            throw new RuntimeException("Error validate XML " + path, ex);
        }
    }

    public static Schema createSchema(String resource) throws Exception {
        return SF.newSchema(new URL(resource));
    }

    public static Schema createSchema(List<String> resources) throws Exception {
        List<Source> sources = new ArrayList<>();
        for (String item : resources) {
            if (item.endsWith(".wsdl")) {
                Source source = loadWsdlSource(item);
                if (source != null) {
                    sources.add(source);
                }
            } else {
                sources.add(new StreamSource(Service.class.getResourceAsStream(item), item));
            }
        }
        return SF.newSchema(sources.toArray(new Source[sources.size()]));
    }

    private static Source loadWsdlSource(String wsdl) throws Exception {
        WSDLFactory factory = WSDLFactory.newInstance();
        WSDLReader reader = factory.newWSDLReader();
        reader.setFeature("javax.wsdl.importDocuments", false);
        reader.setFeature("javax.wsdl.verbose", false);
        Definition definition = reader.readWSDL(wsdl);

        if (definition.getTypes() != null) {
            for (Object o : definition.getTypes().getExtensibilityElements()) {
                if (o instanceof javax.wsdl.extensions.schema.Schema) {
                    Element e = ((javax.wsdl.extensions.schema.Schema) o).getElement();
                    return new DOMSource(e);
                }
            }
        }
        return null;
    }

    public static class XmlErrorHandler implements ErrorHandler {

        private Path path;

        public XmlErrorHandler(Path path) {
            this.path = path;
        }

        private void log(SAXParseException ex, String type) {
            System.err.println();
            System.err.println("**********************************************************");
            System.err.println("TYPE: " + type);
            System.err.println("FILE: " + path);
//            System.err.println("XSD: " + def.getValue());
            System.err.println("MSG: " + ex.getMessage());
            System.err.println("LINE: " + ex.getLineNumber());
            System.err.println("COLUMN: " + ex.getColumnNumber());
            System.err.println("**********************************************************");
            System.err.println();
        }

        @Override
        public void warning(SAXParseException ex) throws SAXException {
            log(ex, "WARNING");
        }

        @Override
        public void error(SAXParseException ex) throws SAXException {
            log(ex, "ERROR");
            System.exit(1);
        }

        @Override
        public void fatalError(SAXParseException ex) throws SAXException {
            log(ex, "FATAL ERROR");
            System.exit(1);
        }
    }
}
