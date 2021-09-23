package tr.org.liderahenk.packagemanager.constants;

public class PackageManagerConstants {

	public static final String PLUGIN_NAME = "package-manager";

	public static final String PLUGIN_VERSION = "1.0.0";

	public static final class PARAMETERS {
		public static final String ADDED_ITEMS = "addedItems";
		public static final String DELETED_ITEMS = "deletedItems";
	}

	public static final class PACKAGE_PARAMETERS {
		public static final String PACKAGE_NAME = "packageName";
		public static final String PACKAGE_VERSION = "packageVersion";
	}

	public static final class PACKAGES {
		public static final String PACKAGE_INFO_LIST = "packageInfoList";
	}

	public static final class CHECK_INFO_PARAMETERS {
		public static final String COMMAND = "command";
		public static final String USER = "user";
		public static final String IS_STRICT_MATCH = "isStrictMatch";
	}

	public static final class EDITORS {
		public static final String PACKAGE_MANAGER_TASK_LIST_EDITOR = "tr.org.liderahenk.packagemanager.editors.PackageManagerTaskListEditor";
	}
}