package tr.org.liderahenk.rsyslog.constants;

public class RsyslogConstants {
	
	public static final String PLUGIN_NAME = "rsyslog";
	
	public static final String PLUGIN_VERSION = "1.0.0";

	public static final class PARAMETERS {
		public static final String ROTATION_FREQUENCY = "rotationInterval";
		public static final String OLD_LOG_COUNT = "keepBacklogs";
		public static final String LOG_FILE_SIZE = "maxSize";
		public static final String NEW_LOG_FILE_AFTER_ROTATION = "createNewLogFiles";
		public static final String COMPRESS_OLD_LOG_FILE = "compressOldLogFiles";
		public static final String PASS_AWAY_WITHOUT_ERROR_IF_FILE_NOT_EXIST = "missingOk";
		public static final String ADDRESS = "ADDRESS";
		public static final String PORT = "PORT";
		public static final String PROTOCOL = "PROTOCOL";
		public static final String LIST_ITEMS = "items";
	}
}