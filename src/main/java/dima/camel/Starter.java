package dima.camel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class Starter {
    public static void main(String[] args) throws Exception {
        try (CamelContext camel = new DefaultCamelContext()) {
            // Инизиализируем переменные с входными и выходными путями
            camel.getPropertiesComponent().setLocation("classpath:application.properties");
            camel.addRoutes(new RouteBuilder() {
                public String filename = "";
                String out = "";

                @Override
                public void configure() {
                    from("file:{{from}}?noop=true") // Берем файлы из папки без изменений
                            .routeId("My Route")// Даем имя роутеру
                            .split(body().tokenize("\n"))// Читаем построчно
                            .streaming()
                            .choice() // Ставим условие
                            .when(exchange -> ((String) exchange.getIn().getBody()).contains("_id")) // Если увидели id - генерируем название файла
                                .process(msg -> {
                                    String line = msg.getIn().getBody(String.class);
                                    filename = line.substring(32, line.length() - 2);
                                })
                            .when(exchange -> ((String) exchange.getIn().getBody()).contains("ВерсияОбъекта")) // Нашли нужную нам строку для работы
                                .process(msg -> { // Начинаем обработку
                                    String line = msg.getIn().getBody(String.class);

    //                                String version = line.replaceAll("(\"ВерсияОбъекта\": \"|\",|\\s+|\\\\r\\\\n)", "");
                                    final String regex = "\\t\\t\\s\\s\"(.*?)\\s\"|\",|(\\\\r\\\\n)"; // Удаляем лишние табы, пробелы, символы и переносы строки
                                    final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
                                    final Matcher matcher = pattern.matcher(line);
                                    final String result = matcher.replaceAll("");

                                    byte[] bytesEncoded = Base64.getDecoder().decode(result); // Декодируем

                                    InputStream inStream = new ByteArrayInputStream(bytesEncoded); // Создаем стрим для преобразования в xml

                                    try {
                                        out = FastInfosetConverter.fiStream2xmlStream(inStream);// Преобразовываем в xml
                                    } catch (IllegalArgumentException | IllegalStateException | TransformerException e) {
                                        e.printStackTrace();
                                    }


                                    Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create(); //Инициализируем json конструктор
                                    String json = gson.toJson(SAXConverter.main(out)); // Получаем измененный объект из xml и конвертируем его в json
                                    msg.getMessage().setHeader("filename", filename); // Засовываем в хедер имя файла
                                    msg.getMessage().setBody(json); // Засовываем в тело json
                                })
                                .to("file:{{to}}?fileName=${headers.filename}.json"); //сохраняем файлы
                }
            });
            camel.start();
            Thread.sleep(1_000);
            camel.stop();
        }
    }
}
