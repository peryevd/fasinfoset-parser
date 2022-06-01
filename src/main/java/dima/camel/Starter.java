package dima.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Starter {
    public static void main(String[] args) throws Exception {
        CamelContext camel = new DefaultCamelContext();

        camel.getPropertiesComponent().setLocation("classpath:application.properties");
        String FILEPATH = "files/to/";

        camel.addRoutes(new RouteBuilder() {
            public String filename = "";
            String out = "";
            @Override
            public void configure() {
                from("file:{{from}}?noop=true")
                        .routeId("My Route")
                        .split(body().tokenize("\n"))
                        .streaming()
                        .choice()
                        .when(exchange -> ((String) exchange.getIn().getBody()).contains("_id"))
                            .process(msg -> {
                                String line = msg.getIn().getBody(String.class);
                                filename = line.substring(32, line.length() - 2);
                            })
                        .when(exchange -> ((String) exchange.getIn().getBody()).contains("ВерсияОбъекта"))
                            .process(msg -> {
                                String line = msg.getIn().getBody(String.class);

//                                String version = line.replaceAll("(\"ВерсияОбъекта\": \"|\",|\\s+|\\\\r\\\\n)", "");
                                final String regex = "\\t\\t\\s\\s\\\"(.*?)\\s\\\"|\\\",|(\\\\r\\\\n)";
                                final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                                final Matcher matcher = pattern.matcher(line);
                                final String result = matcher.replaceAll("");

                                byte[] bytesEncoded = Base64.getDecoder().decode(result);

                                InputStream inStream = new ByteArrayInputStream(bytesEncoded);

                                try {
                                    out = FastInfosetConverter.fiStream2xmlStream(inStream);
                                } catch (IllegalArgumentException | IllegalStateException | TransformerException e) {
                                    e.printStackTrace();
                                }
                                msg.getOut().setHeader("filename", filename);
                                msg.getOut().setBody(out);
                                })
                            .to("file:{{to}}?fileName=${headers.filename}.xml");
            }
        });
        camel.start();
        Thread.sleep(1_000);
        camel.stop();
    }
}
