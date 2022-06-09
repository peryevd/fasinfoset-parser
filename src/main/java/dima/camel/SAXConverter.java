package dima.camel;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.TreeMap;

public class SAXConverter {
    public static TreeMap items = new TreeMap<String, String>();

    public static TreeMap main(String args) throws ParserConfigurationException, SAXException, IOException {
        // Создание фабрики и образца парсера
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        XMLHandler handler = new XMLHandler();
        parser.parse(new InputSource(new StringReader(args)), handler);

        return items;
    }

    private static class XMLHandler extends DefaultHandler {
        private String lastElementName;
        private String ownerAttr;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            if(qName.equals("ОбъектВладелец")){
                ownerAttr = attributes.getValue("xsi:type");
            }
            lastElementName = qName;
        }

        @Override
        public void characters(char[] ch, int start, int length) {
                String information = new String(ch, start, length);
                information = information.replace("\n", "").trim();

            if (!information.isEmpty()) {
                switch (lastElementName) {
                    case ("Ref") -> SAXConverter.items.put("id", "CatalogObject.Документы|" + information);
                    case ("Организация") -> SAXConverter.items.put("org", information);
                    case ("ОбъектВладелец") -> {
                        TreeMap<String, String> obj = new TreeMap<>();
                        obj.put("ref", information);
                        obj.put("type", ownerAttr);
                        SAXConverter.items.put("АвторВерсии", obj);
                    }
                    default -> SAXConverter.items.put(lastElementName, information);
                }
            }
        }
    }
}
