package tr.org.liderahenk.screensaver.constants;

public class ScreensaverConstants {
	
	public static final String PLUGIN_NAME = "screensaver";
	
	public static final String PLUGIN_VERSION = "1.0.0";
	
	public static final int MAX_VALUE = 9999;
	public static final int MIN_VALUE = 0;
	
	
	public static final int FADING_MAX_VALUE = 10;
	
	public static final class PARAMETERS
	{
		public static final String MODES = "mode";
		public static final String BLANK_AFTER = "timeout";
		public static final String CYCLE_AFTER = "cycle";
		public static final String LOCK_SCREEN_AFTER = "lock";
		public static final String LOCK_SCREEN_AFTER_TIMEOUT = "lockTimeout";
		public static final String GRAB_DESKTOP_IMAGE = "grabDesktopImages";
		public static final String GRAB_VIDEO_FRAMES = "grabVideoFrames";
		public static final String POWER_MANAGEMENT_ENABLED = "dpmsEnabled";
		public static final String STANDBY_AFTER = "dpmsStandby";
		public static final String SUSPEND_AFTER = "dpmsSuspend";
		public static final String OFF_AFTER = "dpmsOff";
		public static final String QUICK_POWER_OFF_IN_BLACK_ONLY_MODE = "dpmsQuickOff";
		public static final String HOST_NAME_AND_TIME_TYPE = "date";
		public static final String LITERAL_TYPE = "literal";
		public static final String URL_TYPE = "url";
		public static final String TEXT_MODE = "textMode";
		public static final String TEXT = "textLiteral";
		public static final String URL = "textUrl";
		public static final String FADE_TO_BLACK_WHEN_BLANKING = "fade";
		public static final String FADE_FROM_BLACK_WHEN_UNBLANKING = "unfade";
		public static final String FADE_DURATION = "fadeSeconds";
		public static final String INSTALL_COLORMAP = "installColormap";
	}
	
	public static final class LIMITS 
	{
		public static final int MAX_STAND_VALUE 		= 1440;
		public static final int DEFAULT_STAND_VALUE 	= 120;
		public static final int DEFAULT_STAND_OFF_VALUE = 240;
	}
	
}