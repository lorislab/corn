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

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XmlValidator {

    public static void validate(Path path, XSDDefinition xsdDefinition) {
        try {
            Schema schema = xsdDefinition.getSchema();
            Validator validator = schema.newValidator();
            
            Source source;
            if (xsdDefinition.isWsdl()) {
                MessageFactory mf = MessageFactory.newInstance();
                SOAPMessage soapMessage = mf.createMessage(new MimeHeaders(), new ByteArrayInputStream(Files.readAllBytes(path)));            
                Document doc = soapMessage.getSOAPBody().extractContentAsDocument();
                source = new DOMSource(doc);
            } else {
                source = new StreamSource(path.toFile());
            }
            
            validator.setErrorHandler(new XmlErrorHandler(path));
            validator.validate(source);
        } catch (Exception ex) {
            throw new RuntimeException("Error validate XML " + path, ex);
        }
    }

    public static class XmlErrorHandler implements ErrorHandler {

        private final Path path;

        public XmlErrorHandler(Path path) {
            this.path = path;
        }

        private void log(SAXParseException ex, String type) {
            System.err.println();
            System.err.println("**********************************************************");
            System.err.println("TYPE: " + type);
            System.err.println("FILE: " + path);
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
