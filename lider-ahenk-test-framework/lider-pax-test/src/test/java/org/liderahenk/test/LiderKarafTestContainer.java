package org.liderahenk.test;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.configureSecurity;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.karafDistributionConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.keepRuntimeFolder;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.logLevel;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.replaceConfigurationFile;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.URL;
import java.security.Principal;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

import org.apache.karaf.features.BootFinished;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.ProbeBuilder;
import org.ops4j.pax.exam.TestProbeBuilder;
import org.ops4j.pax.exam.karaf.options.LogLevelOption.LogLevel;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LiderKarafTestContainer {

    public static final String MIN_RMI_SERVER_PORT = "44444";
    public static final String MAX_RMI_SERVER_PORT = "66666";
    public static final String MIN_HTTP_PORT = "9080";
    public static final String MAX_HTTP_PORT = "9999";
    public static final String MIN_RMI_REG_PORT = "1099";
    public static final String MAX_RMI_REG_PORT = "9999";
    public static final String MIN_SSH_PORT = "8101";
    public static final String MAX_SSH_PORT = "8888";

    static final Long COMMAND_TIMEOUT = 30000L;
    static final Long SERVICE_TIMEOUT = 30000L;
    static final long BUNDLE_TIMEOUT = 30000L;

    private static Logger LOG = LoggerFactory.getLogger(LiderKarafTestContainer.class);
    private Option[] customOptions;

    @Rule
    public LiderWatcher baseTestWatcher = new LiderWatcher();

    ExecutorService executor = Executors.newCachedThreadPool();

    @Inject
    protected BundleContext bundleContext;

    @Inject
    protected FeaturesService featureService;

    @Inject
    protected SessionFactory sessionFactory;

    @Inject
    protected ConfigurationAdmin configurationAdmin;

    /**
     * To make sure the tests run only when the boot features are fully installed
     */
    @Inject
    BootFinished bootFinished;

    @ProbeBuilder
    public TestProbeBuilder probeConfiguration(TestProbeBuilder probe) {
        probe.setHeader(Constants.DYNAMICIMPORT_PACKAGE, "*,ch.vorburger.exec.*,org.liderahenk.*,tr.org.liderahenk.*,org.apache.felix.service.*;status=provisional");
        return probe;
    }
        

    public File getConfigFile(String path) {
        URL res = this.getClass().getResource(path);
        if (res == null) {
            throw new RuntimeException("Config resource " + path + " not found");
        }
    	return new File(res.getFile());
    }
    
    
    public Option[] getOptions(){
    	Option[] options;
    	if(getCustomOptions() != null && getCustomOptions().length > 0){
    		options = new Option[7 + getCustomOptions().length];
    	}else {
    		options = new Option[7];
    	}
    	
    	
    	
    	
    	 MavenArtifactUrlReference karafUrl = maven().groupId("org.apache.karaf").artifactId("apache-karaf").version("4.0.5").type("tar.gz");
         MavenUrlReference liderRepo = maven().groupId("tr.org.liderahenk").artifactId("lider-features").version("1.0.0-SNAPSHOT").classifier("features").type("xml");
         String httpPort = Integer.toString(getAvailablePort(Integer.parseInt(MIN_HTTP_PORT), Integer.parseInt(MAX_HTTP_PORT)));
         String rmiRegistryPort = Integer.toString(getAvailablePort(Integer.parseInt(MIN_RMI_REG_PORT), Integer.parseInt(MAX_RMI_REG_PORT)));
         String rmiServerPort = Integer.toString(getAvailablePort(Integer.parseInt(MIN_RMI_SERVER_PORT), Integer.parseInt(MAX_RMI_SERVER_PORT)));
         String sshPort = Integer.toString(getAvailablePort(Integer.parseInt(MIN_SSH_PORT), Integer.parseInt(MAX_SSH_PORT)));
    	
         options[0]= karafDistributionConfiguration().frameworkUrl(karafUrl).name("Apache Karaf").unpackDirectory(new File("target/exam")).useDeployFolder(false);
         options[1]= configureSecurity().disableKarafMBeanServerBuilder();
         options[2]= configureSecurity().disableKarafMBeanServerBuilder();
         options[3]= keepRuntimeFolder();
         options[4]= logLevel(LogLevel.INFO);
         options[5]= features(liderRepo, "lider");
         options[6]= replaceConfigurationFile("etc/tr.org.liderahenk.cfg", getConfigFile("/etc/tr.org.liderahenk.cfg"));
//         options[7]= replaceConfigurationFile("etc/org.ops4j.pax.url.mvn.cfg", getConfigFile("/etc/org.ops4j.pax.url.mvn.cfg"));
//         options[8]= wrappedBundle(mavenBundle().groupId("ch.vorburger.mariaDB4j").artifactId("mariaDB4j").version("2.1.3"));
//         options[9]= wrappedBundle(mavenBundle().groupId("ch.vorburger.mariaDB4j").artifactId("mariaDB4j-db-linux64").version("10.1.8"));
//         options[10]= mavenBundle().groupId("org.apache.commons").artifactId("commons-lang3").version("3.0");
//         options[11]= mavenBundle().groupId("tr.org.liderahenk").artifactId("lider-embedded-db").version("1.0.0-SNAPSHOT");
         
         if (getCustomOptions() != null){
        	 for(int i=0;i<getCustomOptions().length;i++){
            	 options[7+i] = getCustomOptions()[i];
             }
         }
        
    	return options;
    }

    @Configuration
    public Option[] config() {
       return getOptions();
    }

    private int getAvailablePort(int min, int max) {
        for (int i = min; i <= max; i++) {
            try {
                ServerSocket socket = new ServerSocket(i);
                return socket.getLocalPort();
            } catch (Exception e) {
                System.err.println("Port " + i + " not available, trying next one");
                continue; // try next port
            }
        }
        throw new IllegalStateException("Can't find available network ports");
    }

    /**
     * Executes a shell command and returns output as a String.
     * Commands have a default timeout of 10 seconds.
     *
     * @param command The command to execute
     * @param principals The principals (e.g. RolePrincipal objects) to run the command under
     * @return
     */
    protected String executeCommand(final String command, Principal ... principals) {
        return executeCommand(command, COMMAND_TIMEOUT, false, principals);
    }

    /**
     * Executes a shell command and returns output as a String.
     * Commands have a default timeout of 10 seconds.
     *
     * @param command    The command to execute.
     * @param timeout    The amount of time in millis to wait for the command to execute.
     * @param silent     Specifies if the command should be displayed in the screen.
     * @param principals The principals (e.g. RolePrincipal objects) to run the command under
     * @return
     */
    protected String executeCommand(final String command, final Long timeout, final Boolean silent, final Principal ... principals) {
        waitForCommandService(command);

        String response;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(byteArrayOutputStream);
        final SessionFactory sessionFactory = getOsgiService(SessionFactory.class);
        final Session session = sessionFactory.create(System.in, printStream, System.err);

        final Callable<String> commandCallable = new Callable<String>() {
//            @Override
            public String call() throws Exception {
                try {
                    if (!silent) {
                        System.err.println(command);
                    }
                    session.execute(command);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                printStream.flush();
                return byteArrayOutputStream.toString();
            }
        };

        FutureTask<String> commandFuture;
        if (principals.length == 0) {
            commandFuture = new FutureTask<String>(commandCallable);
        } else {
            // If principals are defined, run the command callable via Subject.doAs()
            commandFuture = new FutureTask<String>(new Callable<String>() {
//                @Override
                public String call() throws Exception {
                    Subject subject = new Subject();
                    subject.getPrincipals().addAll(Arrays.asList(principals));
                    return Subject.doAs(subject, new PrivilegedExceptionAction<String>() {
//                        @Override
                        public String run() throws Exception {
                            return commandCallable.call();
                        }
                    });
                }
            });
        }

        try {
            executor.submit(commandFuture);
            response = commandFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            e.printStackTrace(System.err);
            response = "SHELL COMMAND TIMED OUT: ";
        } catch (ExecutionException e) {
            Throwable cause = e.getCause().getCause();
            throw new RuntimeException(cause.getMessage(), cause);
	} catch (InterruptedException e) {
	    throw new RuntimeException(e.getMessage(), e);
	}
        return response;
    }


    protected <T> T getOsgiService(Class<T> type, long timeout) {
        return getOsgiService(type, null, timeout);
    }

    protected <T> T getOsgiService(Class<T> type) {
        return getOsgiService(type, null, SERVICE_TIMEOUT);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T> T getOsgiService(Class<T> type, String filter, long timeout) {
        ServiceTracker tracker = null;
        try {
            String flt;
            if (filter != null) {
                if (filter.startsWith("(")) {
                    flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")" + filter + ")";
                } else {
                    flt = "(&(" + Constants.OBJECTCLASS + "=" + type.getName() + ")(" + filter + "))";
                }
            } else {
                flt = "(" + Constants.OBJECTCLASS + "=" + type.getName() + ")";
            }
            Filter osgiFilter = FrameworkUtil.createFilter(flt);
            tracker = new ServiceTracker(bundleContext, osgiFilter, null);
            tracker.open(true);
            // Note that the tracker is not closed to keep the reference
            // This is buggy, as the service reference may change i think
            Object svc = type.cast(tracker.waitForService(timeout));
            if (svc == null) {
                Dictionary dic = bundleContext.getBundle().getHeaders();
                System.err.println("Test bundle headers: " + explode(dic));

                for (ServiceReference ref : asCollection(bundleContext.getAllServiceReferences(null, null))) {
                    System.err.println("ServiceReference: " + ref);
                }

                for (ServiceReference ref : asCollection(bundleContext.getAllServiceReferences(null, flt))) {
                    System.err.println("Filtered ServiceReference: " + ref);
                }

                throw new RuntimeException("Gave up waiting for service " + flt);
            }
            return type.cast(svc);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("Invalid filter", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void waitForCommandService(String command) {
        // the commands are represented by services. Due to the asynchronous nature of services they may not be
        // immediately available. This code waits the services to be available, in their secured form. It
        // means that the code waits for the command service to appear with the roles defined.

        if (command == null || command.length() == 0) {
            return;
        }

        int spaceIdx = command.indexOf(' ');
        if (spaceIdx > 0) {
            command = command.substring(0, spaceIdx);
        }
        int colonIndx = command.indexOf(':');
        String scope = (colonIndx > 0) ? command.substring(0, colonIndx) : "*";
        String name  = (colonIndx > 0) ? command.substring(colonIndx + 1) : command;
        try {
            long start = System.currentTimeMillis();
            long cur   = start;
            while (cur - start < SERVICE_TIMEOUT) {
                if (sessionFactory.getRegistry().getCommand(scope, name) != null) {
                    return;
                }
                Thread.sleep(100);
                cur = System.currentTimeMillis();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void waitForService(String filter, long timeout) throws InvalidSyntaxException, InterruptedException {
        ServiceTracker<Object, Object> st = new ServiceTracker<Object, Object>(bundleContext, bundleContext.createFilter(filter), null);
        try {
            st.open();
            st.waitForService(timeout);
        } finally {
            st.close();
        }
    }
    
    protected Bundle waitBundleState(String symbolicName, int state) {
        long endTime = System.currentTimeMillis() + BUNDLE_TIMEOUT;
        while (System.currentTimeMillis() < endTime) {
            Bundle bundle = findBundleByName(symbolicName);
            if (bundle != null && bundle.getState() == state) {
                return bundle;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        Assert.fail("Manadatory bundle " + symbolicName + " not found.");
        throw new IllegalStateException("Should not be reached");
    }

    /*
    * Explode the dictionary into a ,-delimited list of key=value pairs
    */
    @SuppressWarnings("rawtypes")
    private static String explode(Dictionary dictionary) {
        Enumeration keys = dictionary.keys();
        StringBuffer result = new StringBuffer();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            result.append(String.format("%s=%s", key, dictionary.get(key)));
            if (keys.hasMoreElements()) {
                result.append(", ");
            }
        }
        return result.toString();
    }

    /**
     * Provides an iterable collection of references, even if the original array is null
     */
    @SuppressWarnings("rawtypes")
    private static Collection<ServiceReference> asCollection(ServiceReference[] references) {
        return references != null ? Arrays.asList(references) : Collections.<ServiceReference>emptyList();
    }

    public JMXConnector getJMXConnector() throws Exception {
        return getJMXConnector("karaf", "karaf");
    }

    public JMXConnector getJMXConnector(String userName, String passWord) throws Exception {
        JMXServiceURL url = new JMXServiceURL(getJmxServiceUrl());
        Hashtable<String, Object> env = new Hashtable<String, Object>();
        String[] credentials = new String[]{ userName, passWord };
        env.put("jmx.remote.credentials", credentials);
        JMXConnector connector = JMXConnectorFactory.connect(url, env);
        return connector;
    }

    public String getJmxServiceUrl() throws Exception {
        org.osgi.service.cm.Configuration configuration = configurationAdmin.getConfiguration("org.apache.karaf.management", null);
        if (configuration != null) {
            return configuration.getProperties().get("serviceUrl").toString();
        }
        return "service:jmx:rmi:///jndi/rmi://localhost:" + MIN_RMI_SERVER_PORT + "/karaf-root";
    }

    public String getSshPort() throws Exception {
        org.osgi.service.cm.Configuration configuration = configurationAdmin.getConfiguration("org.apache.karaf.shell", null);
        if (configuration != null) {
            return configuration.getProperties().get("sshPort").toString();
        }
        return "8101";
    }

    public void assertFeatureInstalled(String featureName) throws Exception {
        String name;
        String version;
        if (featureName.contains("/")) {
            name = featureName.substring(0, featureName.indexOf("/"));
            version = featureName.substring(featureName.indexOf("/") + 1);
        } else {
            name = featureName;
            version = null;
        }
        assertFeatureInstalled(name, version);
    }

    public void assertFeatureInstalled(String featureName, String featureVersion) throws Exception {
        Feature featureToAssert = featureService.getFeatures(featureName, featureVersion)[0];
        Feature[] features = featureService.listInstalledFeatures();
        for (Feature feature : features) {
            if (featureToAssert.equals(feature)) {
                return;
            }
        }
        Assert.fail("Feature " + featureName + (featureVersion != null ? "/" + featureVersion : "") + " should be installed but is not");
    }

    public void assertFeaturesInstalled(String ... expectedFeatures) throws Exception {
        Set<String> expectedFeaturesSet = new HashSet<String>(Arrays.asList(expectedFeatures));
        Feature[] features = featureService.listInstalledFeatures();
        Set<String> installedFeatures = new HashSet<String>();
        for (Feature feature : features) {
            installedFeatures.add(feature.getName());
        }
        String msg = "Expecting the following features to be installed : " + expectedFeaturesSet + " but found " + installedFeatures;
        Assert.assertTrue(msg, installedFeatures.containsAll(expectedFeaturesSet));
    }

    public void assertFeatureNotInstalled(String featureName) throws Exception {
        String name;
        String version;
        if (featureName.contains("/")) {
            name = featureName.substring(0, featureName.indexOf("/"));
            version = featureName.substring(featureName.indexOf("/") + 1);
        } else {
            name = featureName;
            version = null;
        }
        assertFeatureNotInstalled(name, version);
    }

    public void assertFeatureNotInstalled(String featureName, String featureVersion) throws Exception {
        Feature featureToAssert = featureService.getFeatures(featureName, featureVersion)[0];
        Feature[] features = featureService.listInstalledFeatures();
        for (Feature feature : features) {
            if (featureToAssert.equals(feature)) {
                Assert.fail("Feature " + featureName + (featureVersion != null ? "/" + featureVersion : "") + " is installed whereas it should not be");
            }
        }
    }
    
    public void destroyEmbeddedApps(){
    	
    }

    public void assertContains(String expectedPart, String actual) {
        assertTrue("Should contain '" + expectedPart + "' but was : " + actual, actual.contains(expectedPart));
    }

    public void assertContainsNot(String expectedPart, String actual) {
        Assert.assertFalse("Should not contain '" + expectedPart + "' but was : " + actual, actual.contains(expectedPart));
    }

	protected void assertBundleInstalled(String name) {
	    Assert.assertNotNull("Bundle " + name + " should be installed", findBundleByName(name));
	}

	protected void assertBundleNotInstalled(String name) {
	    Assert.assertNull("Bundle " + name + " should not be installed", findBundleByName(name));
	}

	protected Bundle findBundleByName(String symbolicName) {
	    for (Bundle bundle : bundleContext.getBundles()) {
	        if (bundle.getSymbolicName().equals(symbolicName)) {
	            return bundle;
	        }
	    }
	    return null;
	}

    protected void installAndAssertFeature(String feature) throws Exception {
        featureService.installFeature(feature, EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
        assertFeatureInstalled(feature);
    }

    protected void installAssertAndUninstallFeature(String feature, String version) throws Exception {
        installAssertAndUninstallFeatures(feature + "/" + version);
    }

    protected void installAssertAndUninstallFeatures(String... feature) throws Exception {
        boolean success = false;
    	try {
			for (String curFeature : feature) {
				featureService.installFeature(curFeature, EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
			    assertFeatureInstalled(curFeature);
			}
            success = true;
		} finally {
            for (String curFeature : feature) {
                System.out.println("Uninstalling " + curFeature);
                try {
                    featureService.uninstallFeature(curFeature, EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
                } catch (Exception e) {
                    if (success) {
                        throw e;
                    }
                }
            }
		}
    }

    /**
     * The feature service does not uninstall feature dependencies when uninstalling a single feature.
     * So we need to make sure we uninstall all features that were newly installed.
     *
     * @param featuresBefore
     * @throws Exception
     */
	protected void uninstallNewFeatures(Set<Feature> featuresBefore)
			throws Exception {
		Feature[] features = featureService.listInstalledFeatures();
        for (Feature curFeature : features) {
			if (!featuresBefore.contains(curFeature)) {
				try {
					System.out.println("Uninstalling " + curFeature.getName());
					featureService.uninstallFeature(curFeature.getName(), curFeature.getVersion(),
                                                    EnumSet.of(FeaturesService.Option.NoAutoRefreshBundles));
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
				}
			}
		}
	}

    protected void close(Closeable closeAble) {
    	if (closeAble != null) {
    		try {
				closeAble.close();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
    	}
    }

	public Option[] getCustomOptions() {
		return customOptions;
	}

	public void setCustomOptions(Option[] customOptions) {
		this.customOptions = customOptions;
	}
	
	
	
}
