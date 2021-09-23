package tr.org.liderahenk.usb.ltsp.constants;

public class UsbLtspConstants {

	public static final String PLUGIN_NAME = "usb-ltsp";
	public static final String PLUGIN_VERSION = "1.0.0";

	public static final class PARAMETERS {
		public static final String USERNAMES = "usernames";
		public static final String STATUS_CODE = "statusCode";
	}

	public static final class EDITORS {
		public static final String USB_FUSE_GROUP_RESULT_EDITOR = "tr.org.liderahenk.usb.ltsp.editors.UsbFuseGroupResultEditor";
	}
	
	public static final class TASKS {
		public static final String LIST_USB_FUSE_GROUP_STATUS = "LIST_USB_FUSE_GROUP_STATUS";
		public static final String USB_FUSE_GROUP = "USB_FUSE_GROUP";
		public static final String GET_USERS ="GET_USERS";
	}

}