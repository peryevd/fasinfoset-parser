package dima.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Base64;

public class Starter {
    public static void main(String[] args) throws Exception {
        CamelContext camel = new DefaultCamelContext();

        camel.getPropertiesComponent().setLocation("classpath:application.properties");
        String FILEPATH = "files/to/";

        camel.addRoutes(new RouteBuilder() {
            String filename = "";
            @Override
            public void configure() {
                from("file:{{from}}?noop=true")
                        .routeId("My Route")
                        .split(body().tokenize("\n"))
                        .streaming()
                        .process(msg -> {
                            String line = msg.getIn().getBody(String.class);
                            if(line.contains("_id")) {
                                filename = line.substring(32, line.length() - 2);
                            }
                            if(line.contains("ВерсияОбъекта")){
                                String version = line.replaceAll("(\"ВерсияОбъекта\": \"|\",|\\s+|\\\\r\\\\n)", "");

                                byte[] bytesEncoded = Base64.getDecoder().decode(version);

                                InputStream inStream = new ByteArrayInputStream(bytesEncoded);

                                FileOutputStream outStream = new FileOutputStream(FILEPATH + filename + ".xml");

                                try {
                                    FastInfosetConverter.fiStream2xmlStream(inStream, outStream);
                                } catch (IllegalArgumentException | IllegalStateException | TransformerException e) {
                                    e.printStackTrace();
                                }
                            }
                            });
            }
        });
        camel.start();
        Thread.sleep(4_000);
        camel.stop();
    }
}
