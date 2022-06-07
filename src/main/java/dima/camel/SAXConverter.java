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
    public static TreeMap<String,String> items = new TreeMap<>();
    private static final StringBuilder currentValue = new StringBuilder();
    private static int count;

    public static TreeMap<String, String> main(String args) throws ParserConfigurationException, SAXException, IOException {
        // Создание фабрики и образца парсера
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();

        XMLHandler handler = new XMLHandler();
        parser.parse(new InputSource(new StringReader(args)), handler);
//        System.out.println(items);

        return items;
    }

    private static class XMLHandler extends DefaultHandler {
        private String lastElementName;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            lastElementName = qName;
        }

        @Override
        public void characters(char[] ch, int start, int length) {
                String information = new String(ch, start, length);
                information = information.replace("\n", "").trim();

            if (!information.isEmpty()) {
                switch (lastElementName) {
                    case  ("Ref"):
                        SAXConverter.items.put("id", "CatalogObject.Документы|" + information);
                        break;
                    case ("Организация"):
                        SAXConverter.items.put("org", information);
                        break;
                    case ("ЗначениеN"):
                        break;
                    default:
                        SAXConverter.items.put(lastElementName, information);
                        break;
                }
            }
        }
    }
}
