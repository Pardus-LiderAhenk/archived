package tr.org.liderahenk.registration.config;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegistrationConfig {

	private static Logger logger = LoggerFactory.getLogger(RegistrationConfig.class);

	private String fileProtocol;

	private String filePath;

	public String getFileProtocol() {
		return fileProtocol;
	}

	public void setFileProtocol(String fileProtocol) {
		this.fileProtocol = fileProtocol;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void refresh() {
		logger.info("Registration configuration updated using blueprint: {}", prettyPrintConfig());
	}

	@Override
	public String toString() {
		return "RegistrationConfig [fileProtocol=" + fileProtocol + ", filePath=" + filePath + "]";
	}

	public String prettyPrintConfig() {
		try {
			return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
		} catch (Exception e) {
		}
		return toString();
	}

}
