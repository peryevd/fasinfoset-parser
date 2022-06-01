package dima.camel;

import org.jvnet.fastinfoset.FastInfosetSource;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

public class FastInfosetConverter {
    private static Integer CountFile = 0;

    public static String fiStream2xmlStream(InputStream fiSourceStream)
            throws IllegalArgumentException, IllegalStateException, TransformerException {

        // sanity
        if (fiSourceStream == null)
            throw new IllegalArgumentException("parameter 'fiSourceStream' not permitted to be null");

        // Create the transformer
        try {
            Transformer tx = TransformerFactory.newInstance().newTransformer();
            tx.setOutputProperty(OutputKeys.INDENT, "yes");
            // Perform the transformation
            StringWriter writer = new StringWriter();
            tx.transform(new FastInfosetSource(fiSourceStream), new StreamResult(writer));
            CountFile++;
//            System.out.println("Количество созданных файлов: " + CountFile);
//            System.out.println(writer.toString());

            String output = writer.toString();
            return output;

        } catch (TransformerConfigurationException tce) {
            throw new IllegalStateException(tce);
        }
    }
}
