package gov.usgs.cida.fastinfoset;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.jvnet.fastinfoset.FastInfosetResult;
import org.jvnet.fastinfoset.FastInfosetSource;

/**
 * A minimal reserialization class that converts XML in normal text 
 * form to Fast Infoset, or Fast Infoset to UTF-8 text form. This is intended
 * for commandline utility use (testing, debugging etc):
 * <ul>
 *	<li>No attempt is made to optimize performance for production.</li>
 *	<li>No fault tolerance is implemented.</li>
 *	<li>This class does not concern itself with higher-order XML processing
 *		such as schema validation, XPath, etc.</li>
 * </ul>
 * 
 * If you use this as a code example for a real app, it's on you to add the.
 * 
 * @author Bill Blondeau
 */
public class FastInfosetConverter 
{
	
	/**
	 * Stream-to-stream reserialization method. 
	 * @param fiSourceStream - an InputStream containing well-formed XML in Fast 
	 *		infoset serialized form
	 * @param xmlTargetStream - an OutputStream to which the normal XML
	 *		reserialization of <code>fiSourceStream</code> will be written
	 * @throws IllegalArgumentException if either parameter is <code>null</code>.
	 * @throws IllegalStateException if the internal configuration or state
	 *		is wrong
	 * @throws TransformerException if parse or serialization error occurs
	 */
	public void fiStream2xmlStream(InputStream fiSourceStream, OutputStream xmlTargetStream) 
			throws IllegalArgumentException, IllegalStateException, TransformerException
	{

		// sanity
		if (fiSourceStream == null) throw new IllegalArgumentException 
				("parameter 'fiSourceStream' not permitted to be null");	
		if (xmlTargetStream == null) throw new IllegalArgumentException 
				("parameter 'xmlTargetStream' not permitted to be null");
		
		// Create the transformer
		try
		{
			Transformer tx = TransformerFactory.newInstance().newTransformer();
			
			// Perform the transformation
			tx.transform(new FastInfosetSource(fiSourceStream), new StreamResult(xmlTargetStream));
		}
		catch (TransformerConfigurationException tce)
		{
			throw new IllegalStateException(tce);
		}
	}
	
	/**
	 * Stream-to-stream reserialization method. 
	 * @param xmlSourceStream - an InputStream containing well-formed XML in 
	 *		normal serialized form
	 * @param fiTargetStream - an OutputStream to which the Fast Infoset reserialization
	 *		of <code>xmlSourceStream</code> will be written
	 * @throws IllegalArgumentException if either parameter is <code>null</code>.
	 * @throws IllegalStateException if the internal configuration or state
	 *		is wrong
	 * @throws TransformerException if parse or serialization error occurs
	 */
	public void xmlStream2fiStream(InputStream xmlSourceStream, OutputStream fiTargetStream) 
			throws IllegalArgumentException, IllegalStateException, TransformerException
	{

		// sanity
		if (xmlSourceStream == null) throw new IllegalArgumentException 
				("parameter 'xmlSourceStream' not permitted to be null");	
		if (fiTargetStream == null) throw new IllegalArgumentException 
				("parameter 'fiTargetStream' not permitted to be null");
		
		// Create the transformer
		try
		{
			Transformer tx = TransformerFactory.newInstance().newTransformer();
			
			// Perform the transformation
			tx.transform(new StreamSource(xmlSourceStream), new FastInfosetResult(fiTargetStream));
		}
		catch (TransformerConfigurationException tce)
		{
			throw new IllegalStateException(tce);
		}
	}
	
	/**
	 * Convenience method accepting filepathnames for source and target of 
	 *		Fast infoset -> normal XML conversion.
	 * @param fiSourceFilename an absolute or relative filepathname identifying
	 *		an existing Fast Infoset XML file.
	 * @param xmlTargetFilename an absolute or relative filepathname identifying
	 *		the desired normal XML file. If the file exists, it will be overwritten.
	 *		If the file cannot be written to, throws appropriate exception.
	 * @throws IllegalArgumentException if either parameter is <code>null</code>,
	 *		empty, or blank.
	 * @throws IllegalStateException if the internal configuration or state
	 *		is wrong
	 * @throws FileNotFoundException if <code>fiSourceFilename</code> cannot be 
	 *		read or if <code>xmlTargetFilename</code> cannot be created or
	 *		written to
	 * @throws TransformerException if parse or serialization error occurs
	 */
	public void fiFile2xmlFile(String fiSourceFilename, String xmlTargetFilename)
			throws IllegalArgumentException, IllegalStateException, 
				FileNotFoundException, TransformerException
	{
		// sanity
		if (fiSourceFilename == null || fiSourceFilename.trim().isEmpty()) 
			throw new IllegalArgumentException ("parameter 'xmlSourceStream' "
						+ "not permitted to be null, empty,	or blank");	
		if (xmlTargetFilename == null || xmlTargetFilename.trim().isEmpty()) 
			throw new IllegalArgumentException ("parameter 'fiTargetStream' "
					+ "not permitted to be null, empty, or blank");
		
		// set up streams
		FileInputStream inStream = new FileInputStream(fiSourceFilename);
		FileOutputStream outStream = new FileOutputStream(xmlTargetFilename);
		
		//perform the transformation
		this.fiStream2xmlStream(inStream, outStream);
		
	}
	
	/**
	 * Convenience method accepting filepathnames for source and target of 
	 *		normal XML -> Fast infoset conversion.
	 * @param xmlSourceFilename an absolute or relative filepathname identifying
	 *		an existing normally serialized XML file.
	 * @param fiTargetFilename an absolute or relative filepathname identifying
	 *		the desired Fast Infoset file. If the file exists, it will be overwritten.
	 *		If the file cannot be written to, throws appropriate exception.
	 * @throws IllegalArgumentException if either parameter is <code>null</code>,
	 *		empty, or blank.
	 * @throws IllegalStateException if the internal configuration or state
	 *		is wrong
	 * @throws FileNotFoundException if <code>fiSourceFilename</code> cannot be 
	 *		read or if <code>fiTargetFilename</code> cannot be created or
	 *		written to
	 * @throws TransformerException if parse or serialization error occurs
	 */
	public void xmlFile2fiFile(String xmlSourceFilename, String fiTargetFilename)
			throws IllegalArgumentException, IllegalStateException, 
				FileNotFoundException, TransformerException
	{
		// sanity
		if (xmlSourceFilename == null || xmlSourceFilename.trim().isEmpty()) 
			throw new IllegalArgumentException ("parameter 'xmlSourceFilename' "
						+ "not permitted to be null, empty,	or blank");	
		if (fiTargetFilename == null || fiTargetFilename.trim().isEmpty()) 
			throw new IllegalArgumentException ("parameter 'fiTargetStream' "
					+ "not permitted to be null, empty, or blank");
		
		// set up streams
		FileInputStream inStream = new FileInputStream(xmlSourceFilename);
		FileOutputStream outStream = new FileOutputStream(fiTargetFilename);
		
		//perform the transformation
		this.xmlStream2fiStream(inStream, outStream);
		
	}
		
	public static void main(String[] args) {
		
		String usage = "Usage: 3 arguments" +
		"\n'xml2fi' or 'fi2XML' to define the sense of the desired reserialization" +
		"\nsource file to be read" +
		"\ntarget file to be written";
		
		if (args.length != 3)
		{
			System.out.println(usage);
			return;
		}
		
		FastInfosetConverter fic = new FastInfosetConverter();
		
		try
		{
			if ("xml2fi".equals(args[0]))
			{
				System.out.println("converting XML file " + args[1] + 
						" to Fast Infoset file " + args[2]);
				fic.xmlFile2fiFile(args[1], args[2]);
			}
			else if ("fi2xml".equals(args[0]))
			{
				System.out.println("converting Fast Infoset file " + args[1] + 
						" to XML file " + args[2]);
				fic.fiFile2xmlFile(args[1], args[2]);
			}
			else
			{
				System.out.println("...conversion failed:");
				System.out.println("first argument '" + args[0] + "' incorrect: " +
						"must be 'xml2fi' or 'fi2xml'");
				return;
			}
			
			System.out.println ("...conversion complete");
		}
		catch (Exception ex)
		{
			System.out.println("...conversion failed:");
			System.out.println(ex.getClass().getName());
			System.out.println(ex.getMessage());
		}
	}
}
