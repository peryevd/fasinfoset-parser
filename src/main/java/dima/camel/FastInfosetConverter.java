package dima.camel;

import org.jvnet.fastinfoset.FastInfosetSource;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.StringWriter;
public class FastInfosetConverter {
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

            return writer.toString();

        } catch (TransformerConfigurationException tce) {
            throw new IllegalStateException(tce);
        }
    }
}
