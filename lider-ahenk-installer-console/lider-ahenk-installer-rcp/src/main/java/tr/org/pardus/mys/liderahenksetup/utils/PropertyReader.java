package tr.org.pardus.mys.liderahenksetup.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class PropertyReader {

	private static final Logger logger = LoggerFactory.getLogger(PropertyReader.class);

	private static HashMap<String, String> propertyMap = null;
	private static final String propertiesFile = "lider-config.properties";

	public static void readProperties() {

		propertyMap = new HashMap<String, String>();

		try {

			Properties properties = new Properties();
			InputStream inputStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(propertiesFile);

			if (inputStream == null) {
				logger.error("Could not find properties file: {}", propertiesFile);
				return;
			}

			properties.load(inputStream);

			Enumeration<Object> keySet = properties.keys();
			while (keySet.hasMoreElements()) {
				Object keyObject = keySet.nextElement();
				propertyMap.put(keyObject.toString(), properties.getProperty(keyObject.toString()));
				logger.info(keyObject.toString() + "=" + properties.getProperty(keyObject.toString()));
			}

		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static String property(String key) {
		if (propertyMap == null) {
			readProperties();
		}
		return propertyMap.get(key);
	}

	public static Integer propertyInt(String key) {
		String val = property(key);
		return val != null ? Integer.valueOf(val.trim()) : null;
	}

}
