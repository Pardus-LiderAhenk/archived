package test.liderahenk.ldap.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.schema.SchemaPartition;
import org.apache.directory.server.core.factory.DefaultDirectoryServiceFactory;
import org.apache.directory.server.core.partition.impl.avl.AvlPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.ldap.schemaextractor.SchemaLdifExtractor;
import org.apache.directory.shared.ldap.schemaextractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.shared.util.exception.Exceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedLdapServer {

	public static final String EXAMPLE_DATA = "/example-data.ldif";
	public static final String LIDERAHENK_LDAP_SCHEMA = "/liderahenk_apacheds.ldif";
	public static Boolean keepRun = true;

	private static final String INSTANCE_NAME = "LiderAhenkTest";
	private static final String INSTANCE_PATH = "/tmp/ldapServer";
	private static final String BASE_DN = "dc=liderahenk,dc=org,dc=tr";
	private static final String BANNER_LDAP = "           _                     _          ____  ____   \n"
			+ "          / \\   _ __    ___  ___| |__   ___|  _ \\/ ___|  \n"
			+ "         / _ \\ | '_ \\ / _` |/ __| '_ \\ / _ \\ | | \\___ \\  \n"
			+ "        / ___ \\| |_) | (_| | (__| | | |  __/ |_| |___) | \n"
			+ "       /_/   \\_\\ .__/ \\__,_|\\___|_| |_|\\___|____/|____/  \n"
			+ "               |_|                                       \n";
	private final Logger log = LoggerFactory.getLogger(EmbeddedLdapServer.class);

	private DirectoryService directoryService;
	private LdapServer ldapService;

	private final String host = "localhost";
	private final Integer port = 10389;
	private FileOutputStream outputStream;

	public void execute() {
		try {
			init();
			start();

			InputStream ldapSchemaIS = getClass().getResourceAsStream(LIDERAHENK_LDAP_SCHEMA);

			outputStream = new FileOutputStream(new File("/tmp/liderahenk_apacheds.ldif"));

			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = ldapSchemaIS.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}

			//File dir = new File(this.getClass().getResource(LIDERAHENK_LDAP_SCHEMA).getFile());
			File dir = new File("/tmp/liderahenk_apacheds.ldif");
			applyLdif(dir);

			getDirectoryService().getSchemaManager().loadAllEnabled();
			
			
			InputStream exampleDataIS = getClass().getResourceAsStream(EXAMPLE_DATA);

			outputStream = new FileOutputStream(new File("/tmp/example-data.ldif"));

			

			while ((read = exampleDataIS.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			
			

			//dir = new File(this.getClass().getResource(EXAMPLE_DATA).getFile());
			dir = new File("/tmp/example-data.ldif");
			applyLdif(dir);

			//System.out.println(getDirectoryService().getAdminSession().exists("uid=test,dc=liderahenk,dc=org,dc=tr"));

			while (keepRun) {
				Thread.sleep(1000);
			}

		} catch (IOException e) {
			log.error("IOException while initializing EmbeddedLdapServer", e);
		} catch (LdapException e) {
			e.printStackTrace();
			log.error("LdapException while initializing EmbeddedLdapServer", e);
		} catch (NamingException e) {
			log.error("NamingException while initializing EmbeddedLdapServer", e);
		} catch (Exception e) {
			log.error("Exception while initializing EmbeddedLdapServer", e);
		} finally {
			try {
				// stop server
				stop();
				System.out.println("Embedded LDAP server stopped");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void init() throws Exception, IOException, LdapException, NamingException {

		DefaultDirectoryServiceFactory factory = new DefaultDirectoryServiceFactory();
		factory.init(INSTANCE_NAME);

		directoryService = factory.getDirectoryService();
		directoryService.getChangeLog().setEnabled(false);
		directoryService.setShutdownHookEnabled(true);

		InstanceLayout il = new InstanceLayout(INSTANCE_PATH);
		directoryService.setInstanceLayout(il);

		System.out.println("###########");
		System.out.println("init");
		System.out.println("init");
		System.out.println("###########");

		System.out.println(il.getInstanceDirectory());
		SchemaManager schemaManager = directoryService.getSchemaManager();

		File schemaPartitionDirectory = new File(il.getPartitionsDirectory(), "schema");

		// Extract the schema on disk (a brand new one) and load the registries
		if (schemaPartitionDirectory.exists()) {
			System.out.println("schema partition already exists, skipping schema extraction");
		} else {
			System.out.println("Yoktur");
			SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(il.getPartitionsDirectory());
			extractor.extractOrCopy();
		}

		// SchemaLoader loader = new LdifSchemaLoader( schemaPartitionDirectory
		// );
		//
		// SchemaManager sc = new DefaultSchemaManager( loader );

		// We have to load the schema now, otherwise we won't be able
		// to initialize the Partitions, as we won't be able to parse
		// and normalize their suffix Dn
		schemaManager.loadAllEnabled();

		List<Throwable> errors = schemaManager.getErrors();

		if (errors.size() != 0) {
			throw new Exception(I18n.err(I18n.ERR_317, Exceptions.printErrors(errors)));
		}

		directoryService.setSchemaManager(schemaManager);

		// Init the LdifPartition with schema
		LdifPartition schemaLdifPartition = new LdifPartition(schemaManager);
		schemaLdifPartition.setPartitionPath(schemaPartitionDirectory.toURI());

		// The schema partition
		SchemaPartition schemaPartition = new SchemaPartition(schemaManager);
		schemaPartition.setWrappedPartition(schemaLdifPartition);
		directoryService.setSchemaPartition(schemaPartition);

		// Partition eklemek icin 1. yontem
		AvlPartition partition = new AvlPartition(directoryService.getSchemaManager());
		partition.setId(INSTANCE_NAME);
		partition.setSuffixDn(new Dn(schemaManager, BASE_DN));
		partition.initialize();
		directoryService.addPartition(partition);

		ldapService = new LdapServer();

		System.out.println("STRATING LDAP SERVER HOST : " + host + "PORT " + port);

		ldapService.setTransports(new TcpTransport(host, port));
		ldapService.setDirectoryService(directoryService);

	}

	public void start() throws Exception {

		if (ldapService.isStarted()) {
			// throw new IllegalStateException("Service already running");
			System.out.println("SERVICE ALREADY STARTED LDAP");
			return;
		}

		directoryService.startup();
		ldapService.start();
		System.out.println(BANNER_LDAP);
	}

	public void stop() throws Exception {
		
		System.out.println("=============");
		System.out.println("STOPPING LDAP");
		System.out.println("=============");

		if (!ldapService.isStarted()) {
			throw new IllegalStateException("Service is not running");
		}

		ldapService.stop();
		directoryService.shutdown();
	}

	public void applyLdif(final File ldifFile) throws Exception {
		new LdifFileLoader(directoryService.getAdminSession(), ldifFile, null).execute();
	}

	public void createEntry(final String id, final Map<String, String[]> attributes)
			throws LdapException, LdapInvalidDnException {

		if (!ldapService.isStarted()) {
			throw new IllegalStateException("Service is not running");
		}

		Dn dn = new Dn(directoryService.getSchemaManager(), id);
		if (!directoryService.getAdminSession().exists(dn)) {
			Entry entry = directoryService.newEntry(dn);
			for (String attributeId : attributes.keySet()) {
				entry.add(attributeId, attributes.get(attributeId));
			}
			directoryService.getAdminSession().add(entry);
		}
	}

	public DirectoryService getDirectoryService() {
		return directoryService;
	}
}