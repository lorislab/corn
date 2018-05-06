package org.lorislab.corn.xml;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

public class XmlWritter {

    public static Path writeToFile(Path parent, XmlObject xml) {
        Path path = parent.resolve(xml.getFileName());

        try {

            Source sc;
            if (xml.isWsdl()) {
                SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
                soapMessage.getSOAPBody().addDocument(xml.getDocument());

                soapMessage.getSOAPPart().getEnvelope().removeNamespaceDeclaration("SOAP-ENV");
                soapMessage.getSOAPPart().getEnvelope().addNamespaceDeclaration("soap", "http://schemas.xmlsoap.org/soap/envelope/");
                soapMessage.getSOAPPart().getEnvelope().setPrefix("soap");
                soapMessage.getSOAPHeader().setPrefix("soap");
                soapMessage.getSOAPBody().setPrefix("soap");
                sc = soapMessage.getSOAPPart().getContent();
            } else {
                sc = new DOMSource(xml.getDocument());
            }
            
            try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                XMLUtil.write(sc, writer);
            } catch (Exception ex) {
                throw new RuntimeException("Error write XML ", ex);
            }
        } catch (Exception exx) {
            throw new RuntimeException(exx);
        }
        return path;
    }
}
