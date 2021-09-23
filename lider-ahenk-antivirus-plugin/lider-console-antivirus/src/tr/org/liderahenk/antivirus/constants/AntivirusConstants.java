package tr.org.liderahenk.antivirus.constants;

public class AntivirusConstants {
	
	public static final String PLUGIN_NAME = "antivirus";
	
	public static final String PLUGIN_VERSION = "1.0.0";

	public static final String CONFIGURATION_PARAMETER = "configurationParameter";
	
	public static final class PARAMETERS {
		public static final String IS_RUNNING = "isRunning";
		public static final String USB_SCANNING = "usbScanning";
		public static final String EXECUTION_FREQUENCY = "executionFrequency";
		public static final String UPDATING_INTERVAL = "updatingInterval";
		public static final String SCANNED_FOLDERS = "scannedFolders";
		public static final String SCAN_DOWNLOADED_FILES = "scanDownloadedFiles";
		public static final String FOLDER_FOR_DOWNLOADED_FILES = "folderForDownloadedFiles"; 
		public static final String ANTIVIRUS_VERSION = "antivirusVersion";
	}
	
	public static final class TASK_PARAMETERS {
		public static final String FOLDER_PATH = "folderPath";
	}
	
}