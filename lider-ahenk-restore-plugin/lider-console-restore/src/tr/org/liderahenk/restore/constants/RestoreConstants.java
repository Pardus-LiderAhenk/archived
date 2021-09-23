package tr.org.liderahenk.restore.constants;

public class RestoreConstants {

	public static final String PLUGIN_NAME = "restore";
	public static final String PLUGIN_VERSION = "1.0.0";

	public static final String DEFAULT_PORT = "22";
	public static final int DEFAULT_PORT_INT = 22;
	public static final String DEFAULT_DEST_PATH = "/ydk/{IP_ADDRESS}";
	public static final String IP_ADDRESS_EXPRESSION = "{IP_ADDRESS}";

	public static final class PARAMETERS {
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		public static final String DEST_HOST = "destHost";
		public static final String DEST_PORT = "destPort";
		public static final String DEST_PATH = "destPath";
		public static final String SOURCE_PATH = "sourcePath";
		public static final String USE_SSH_KEY = "useSsh";
		public static final String USE_LVM = "useLvmShadow";
		public static final String BACKUP_LIST_ITEMS = "directories";
		public static final String ESTIMATION = "estimation";
		public static final String PERCENTAGE = "percentage";
		public static final String NUMBER_OF_CREATED_FILES = "numberOfCreatedFiles";
		public static final String TRANSFERRED_FILE_SIZE = "transferredFileSize";
		public static final String ESTIMATED_TRANSFER_SIZE = "estimatedTransferSize";
		public static final String TOTAL_FILE_SIZE = "totalFileSize";
		public static final String NUMBER_OF_TRANSFERRED_FILES = "numberOfTransferredFiles";
		public static final String NUMBER_OF_FILES = "numberOfFiles";
		public static final String RESTORE_PATH = "restore_path";
		
	}

	public static final class EDITORS {
		public static final String BACKUP_TASK_LIST_EDITOR = "tr.org.liderahenk.backup.editors.BackupTaskListEditor";
	}
}