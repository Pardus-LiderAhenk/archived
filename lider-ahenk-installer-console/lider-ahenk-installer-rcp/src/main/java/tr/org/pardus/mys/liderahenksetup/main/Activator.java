package tr.org.pardus.mys.liderahenksetup.main;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import tr.org.pardus.mys.liderahenksetup.constants.InstallerConstants;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		configureLogger();
	}

	private void configureLogger() {
		try {
			Properties prop = new Properties();
			try {
				// Config file is in the same folder as the start script
				prop.load(new FileInputStream(InstallerConstants.FILES.LOG_FILE));
			} catch (Exception ex) {
				// Config file is in the .jar file
				prop.load(Activator.class.getClassLoader().getResourceAsStream(InstallerConstants.FILES.LOG_FILE));
			}
			PropertyConfigurator.configure(prop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
	}

}
