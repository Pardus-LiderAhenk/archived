package tr.org.liderahenk.utils;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.contants.Constants;

public class PropertyReader {

	private static final Logger logger = LoggerFactory.getLogger(PropertyReader.class);

	private static PropertyReader instance = null;
	private static Properties prop = null;

	private PropertyReader() {
	}

	public static PropertyReader getInstance() {
		if (instance == null) {
			instance = new PropertyReader();
			loadProperties();
		}
		return instance;
	}

	private static void loadProperties() {

		logger.info("Trying to load config.properties file.");

		prop = new Properties();

		try {
			try {
				logger.info("Trying to read properties.");
				// Config file is in the same folder as the .jar file!
				prop.load(new FileInputStream(Constants.FILES.PROPERTIES_FILE));
			} catch (Exception ex) {
				// Config file is in the .jar file OR src/main/resources
				prop.load(PropertyReader.class.getClassLoader().getResourceAsStream(Constants.FILES.PROPERTIES_FILE));
			}
			Set<Entry<Object, Object>> entrySet = prop.entrySet();
			for (Entry<Object, Object> e : entrySet) {
				logger.info("{}: {}", new Object[] { e.getKey(), e.getValue() });
			}
			logger.info("Properties loaded.");
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
	}

	public String get(String key) {
		return prop != null && key != null ? prop.getProperty(key) : null;
	}

	public int getInt(String key) {
		return Integer.parseInt(get(key));
	}

	public double getDouble(String key) {
		return Double.parseDouble(get(key));
	}

	public long getLong(String key) {
		return Long.parseLong(get(key));
	}

	public boolean getBoolean(String key) {
		return Boolean.parseBoolean(get(key));
	}

	public String[] getStringArr(String key) {
		return get(key).split(",");
	}

	public List<String> getStringList(String key) {
		return Arrays.asList(get(key).split(","));
	}

}
