package dima.camel;

import org.jvnet.fastinfoset.FastInfosetSource;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.OutputStream;

public class FastInfosetConverter {
    private static Integer CountFile = 0;

    public static void fiStream2xmlStream(InputStream fiSourceStream, OutputStream xmlTargetStream)
            throws IllegalArgumentException, IllegalStateException, TransformerException {

        // sanity
        if (fiSourceStream == null)
            throw new IllegalArgumentException("parameter 'fiSourceStream' not permitted to be null");
        if (xmlTargetStream == null)
            throw new IllegalArgumentException("parameter 'xmlTargetStream' not permitted to be null");

        // Create the transformer
        try {
            Transformer tx = TransformerFactory.newInstance().newTransformer();
            tx.setOutputProperty(OutputKeys.INDENT, "yes");
            // Perform the transformation
            tx.transform(new FastInfosetSource(fiSourceStream), new StreamResult(xmlTargetStream));
            CountFile++;
            System.out.println("Количество созданных файлов: " + CountFile);

        } catch (TransformerConfigurationException tce) {
            throw new IllegalStateException(tce);
        }
    }
}
