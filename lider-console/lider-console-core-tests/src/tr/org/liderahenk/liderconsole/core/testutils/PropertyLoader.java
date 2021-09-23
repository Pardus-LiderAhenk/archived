package tr.org.liderahenk.liderconsole.core.testutils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyLoader {

	private static final Logger logger = LoggerFactory.getLogger(PropertyLoader.class);

	private static PropertyLoader instance = null;
	private static Properties prop = null;

	private PropertyLoader() {
	}

	public static PropertyLoader getInstance() {
		if (instance == null) {
			instance = new PropertyLoader();
			loadProperties();
		}
		return instance;
	}

	private static void loadProperties() {

		logger.info("Trying to load config.properties file.");

		prop = new Properties();
		InputStream inp = null;

		try {
			prop.load(PropertyLoader.class.getClassLoader().getResourceAsStream(Constant.FILES.PROPERTIES_FILE));
			logger.info("Properties loaded.");
		} catch (Exception e) {
			logger.error(e.toString(), e);
		} finally {
			if (inp != null) {
				try {
					inp.close();
				} catch (IOException e) {
				}
			}
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
