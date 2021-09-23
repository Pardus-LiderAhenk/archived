package tr.org.pardus.mys.liderahenksetup.utils.setup;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class DefaultSSHLogger implements com.jcraft.jsch.Logger {

	private static final Logger logger = LoggerFactory.getLogger(DefaultSSHLogger.class);

	private static Hashtable<Integer, String> name = null;
	static {
		name = new Hashtable<Integer, String>();
		name.put(Integer.valueOf(DEBUG), "DEBUG: ");
		name.put(Integer.valueOf(INFO), "INFO: ");
		name.put(Integer.valueOf(WARN), "WARN: ");
		name.put(Integer.valueOf(ERROR), "ERROR: ");
		name.put(Integer.valueOf(FATAL), "FATAL: ");
	}

	@Override
	public boolean isEnabled(int level) {
		return logger.isDebugEnabled() || level != DEBUG;
	}

	@Override
	public void log(int level, String message) {
		if (level == DEBUG) {
			logger.debug(message);
		} else if (level == INFO) {
			logger.info(message);
		} else if (level == WARN) {
			logger.warn(message);
		} else if (level == ERROR || level == FATAL) {
			logger.error(message);
		}
	}

}
