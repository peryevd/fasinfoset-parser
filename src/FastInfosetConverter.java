// package src;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import org.jvnet.fastinfoset.FastInfosetSource;
import java.util.Base64;

public class FastInfosetConverter {
	private static final String ITEMS_NAME = "rows";
	private static final String DOC = "doc";
	private static final String VERSION = "ВерсияОбъекта";
	private static final String FILENAME = "id";
	private static Integer CountFile = 0;

	public void fiStream2xmlStream(InputStream fiSourceStream, OutputStream xmlTargetStream)
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

	public void parseJson(final JsonReader jsonReader, String FILEPATH)
			throws IOException {
		// Считываем { как начало объекта.
		// Если его не считать или считать неверно, выбросится исключение -- это и есть
		// суть pull-метода.
		jsonReader.beginObject();
		// Доходим до массива объектов
		jsonReader.nextName();
		jsonReader.nextDouble();
		jsonReader.nextName();
		jsonReader.nextDouble();
		String itemsName = jsonReader.nextName();

		if (!itemsName.equals(ITEMS_NAME)) {
			// Проверка что дошли до массива объектов
			throw new MalformedJsonException(ITEMS_NAME + " expected but was " + itemsName);
		}
		// Так же теперь вычитываем [
		jsonReader.beginArray();
		// И читаем каждый элемент массива
		while (jsonReader.hasNext()) {
			jsonReader.beginObject();
			String filename = "base";

			// И читаем каждый элемент объекта
			while (jsonReader.hasNext()) {
				final String property = jsonReader.nextName();
				switch (property) {
					// Выбор имени файла
					case FILENAME:
						filename = jsonReader.nextString().substring(20) + ".xml";
						break;
					// Нашли документ
					case DOC:
						jsonReader.beginObject();
						// И читаем каждый элемент вложенного объекта DOC
						while (jsonReader.hasNext()) {
							final String prop = jsonReader.nextName();
							switch (prop) {
								// Нашли версию
								case VERSION:
									// Удаление ненужных символов
									String version = jsonReader.nextString().replaceAll("\r\n", "");
									// Декодирование из base64
									byte[] bytesEncoded = Base64.getDecoder().decode(version);

									InputStream inStream = new ByteArrayInputStream(bytesEncoded);

									FileOutputStream outStream = new FileOutputStream(
											FILEPATH + filename);

									// Декодируем в xml и записываем в файл
									try {
										this.fiStream2xmlStream(inStream, outStream);
									} catch (IllegalArgumentException | IllegalStateException
											| TransformerException e) {
										e.printStackTrace();
									}

									break;
								default:
									jsonReader.skipValue();
									break;
							}
						}

						jsonReader.endObject();
						break;
					default:
						jsonReader.skipValue();
						break;
				}
			}
			// И говорим, что с текущим элементом массива, именно объектом, покончено.
			jsonReader.endObject();
		}
		// Также закрываем последние ] и }
		jsonReader.endArray();
		jsonReader.endObject();

	}

	public static void main(String[] args) throws IOException {
		String FileInputPath = "data.json";
		String FileOutputPath = "files/";

		FastInfosetConverter fic = new FastInfosetConverter();
		try (final JsonReader jsonReader = new JsonReader(
				new BufferedReader(new InputStreamReader(new FileInputStream(FileInputPath))))) {
			fic.parseJson(jsonReader, FileOutputPath);
		}

	}
}
