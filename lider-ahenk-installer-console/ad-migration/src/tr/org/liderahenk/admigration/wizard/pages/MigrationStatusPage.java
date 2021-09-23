package tr.org.liderahenk.admigration.wizard.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.admigration.config.MigrationConfig;
import tr.org.liderahenk.admigration.enums.SearchFilterEnum;
import tr.org.liderahenk.admigration.i18n.Messages;
import tr.org.liderahenk.admigration.utils.LdapSearchFilterAttribute;
import tr.org.liderahenk.admigration.utils.LdapUtils;
import tr.org.liderahenk.admigration.utils.RandomStringGenerator;
import tr.org.pardus.mys.liderahenksetup.constants.NextPageEventType;
import tr.org.pardus.mys.liderahenksetup.utils.gui.GUIHelper;

/**
 * 
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 * 
 */
public class MigrationStatusPage extends WizardPage implements ControlNextEvent, InstallationStatusPage {

	private final static Logger logger = LoggerFactory.getLogger(MigrationStatusPage.class);

	private MigrationConfig config = null;

	// Widgets
	private Composite mainContainer = null;
	private ProgressBar progressBar;
	private Text txtLogConsole;
	private NextPageEventType nextPageEventType;
	boolean isInstallationFinished = false;
	boolean canGoBack = false;
	private int progressBarPercent;

	private final static RandomStringGenerator random = new RandomStringGenerator(10);

	public MigrationStatusPage(MigrationConfig config) {
		super(MigrationStatusPage.class.getName(), Messages.getString("AD_MIGRATION"), null);
		setDescription(Messages.getString("MIGRATION_STATUS"));
		this.config = config;
	}

	@Override
	public void createControl(Composite parent) {
		mainContainer = GUIHelper.createComposite(parent, 1);
		setControl(mainContainer);

		txtLogConsole = GUIHelper.createText(mainContainer, new GridData(GridData.FILL_BOTH),
				SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);

		progressBar = new ProgressBar(mainContainer, SWT.SMOOTH | SWT.HORIZONTAL);
		progressBar.setSelection(0);
		progressBar.setMaximum(100);

		GridData progressGd = new GridData(GridData.FILL_HORIZONTAL);
		progressGd.heightHint = 40;
		progressBar.setLayoutData(progressGd);
	}

	@Override
	public IWizardPage getNextPage() {
		// Start AD-LDAP migration here. To prevent triggering migration
		// again, set isInstallationFinished to true when its done.
		if (super.isCurrentPage() && !isInstallationFinished
				&& nextPageEventType == NextPageEventType.CLICK_FROM_PREV_PAGE) {

			canGoBack = false;

			// Create a thread pool
			setProgressBar(10, Display.getCurrent());
			printMessage(Messages.getString("INITIALIZING_MIGRATION"), Display.getCurrent());
			// Get display before main runnable
			final Display display = Display.getCurrent();

			// Create a main runnable and execute installations as new runnables
			// under this one. Because at the end of installation we have to
			// wait
			// until all runnables completed and this situation locks GUI.
			Runnable mainRunnable = new Runnable() {
				@Override
				public void run() {
					try {
						logger.info("Creating Active Directory connection pool.");
						printMessage(Messages.getString("CREATING_AD_CONN_POLL"), display);
						LdapUtils activeDirectory = new LdapUtils(config.getAdHost(), config.getAdPort(),
								config.getAdUsername(), config.getAdPassword(), false);

						logger.info("Creating OpenLDAP connection pool.");
						printMessage(Messages.getString("CREATING_LDAP_CONN_POLL"), display);
						LdapUtils openLdap = new LdapUtils(config.getLdapHost(), config.getLdapPort(),
								config.getLdapUsername(), config.getLdapPassword(), false);

						// Search parameters for Active Directory
						logger.info("Calculating Active Directory search parameters.");
						printMessage(Messages.getString("CALCULATING_AD_SEARCH_PARAMS"), display);
						final ArrayList<LdapSearchFilterAttribute> aUserFilterAttributes = new ArrayList<LdapSearchFilterAttribute>();
						if (config.getAdUserObjectClasses() != null && config.getAdUserObjectClasses().length > 0) {
							for (String objectClass : config.getAdUserObjectClasses()) {
								aUserFilterAttributes.add(
										new LdapSearchFilterAttribute("objectClass", objectClass, SearchFilterEnum.EQ));
							}
						}
						final ArrayList<LdapSearchFilterAttribute> aGroupFilterAttributes = new ArrayList<LdapSearchFilterAttribute>();
						if (config.getAdGroupObjectClasses() != null && config.getAdGroupObjectClasses().length > 0) {
							for (String objectClass : config.getAdGroupObjectClasses()) {
								aGroupFilterAttributes.add(
										new LdapSearchFilterAttribute("objectClass", objectClass, SearchFilterEnum.EQ));
							}
						}

						// Search parameters for OpenLDAP
						logger.info("Calculating OpenLDAP search parameters.");
						printMessage(Messages.getString("CALCULATING_LDAP_SEARCH_PARAMS"), display);
						final ArrayList<LdapSearchFilterAttribute> oUserFilterAttributes = new ArrayList<LdapSearchFilterAttribute>();
						if (config.getLdapUserObjectClasses() != null && config.getLdapUserObjectClasses().length > 0) {
							for (String objectClass : config.getLdapUserObjectClasses()) {
								oUserFilterAttributes.add(
										new LdapSearchFilterAttribute("objectClass", objectClass, SearchFilterEnum.EQ));
							}
						}
						ArrayList<LdapSearchFilterAttribute> oGroupFilterAttributes = new ArrayList<LdapSearchFilterAttribute>();
						if (config.getLdapGroupObjectClasses() != null
								&& config.getLdapGroupObjectClasses().length > 0) {
							for (String objectClass : config.getLdapGroupObjectClasses()) {
								oGroupFilterAttributes.add(
										new LdapSearchFilterAttribute("objectClass", objectClass, SearchFilterEnum.EQ));
							}
						}

						//
						// Migrate users
						//

						// Collect OpenLDAP attributes, so that we can map AD
						// attributes to
						// them.
						logger.info("Collecting user attributes in OpenLDAP...");
						printMessage(Messages.getString("COLLECTING_USER_ATTR_LDAP"), display);
						ArrayList<String> validUserAttrNames = null;
						boolean[] userAttrUsed = null;
						ArrayList<String> validUserObjClsValues = null;
						List<Entry> oUserEntries = openLdap.search(config.getLdapUserSearchBaseDn(),
								oUserFilterAttributes, null);
						if (oUserEntries != null && !oUserEntries.isEmpty()) {
							System.out.println("------------------------- 165");
							// Select first entry
							Entry entry = oUserEntries.get(0);

							validUserAttrNames = new ArrayList<String>();
							validUserObjClsValues = new ArrayList<String>();

							// Iterate over its each attribute
							Collection<Attribute> attributes = entry.getAttributes();
							if (attributes != null) {
								System.out.println("------------------------- 175");
								for (Attribute attribute : attributes) {
									// If it is an object class, store only its
									// valid object
									// class values...
									if (attribute.getId().equalsIgnoreCase("objectClass")) {
										System.out.println("------------------------- 181");
										for (Value<?> value : attribute) {
											if (value == null || value.getValue() == null) {
												continue;
											}
											System.out.println("------------------------- 186");
											validUserObjClsValues.add(value.getValue().toString());
										}
									} else {
										System.out.println("------------------------- 190");
										// Flag current attribute as valid
										validUserAttrNames.add(attribute.getId().toLowerCase(Locale.ENGLISH));
									}
								}
							}

							userAttrUsed = new boolean[validUserAttrNames.size()];
						}

						// Search user entries in Active Directory
						// For each entry in AD, we try to create a new one in
						// OpenLDAP
						List<Entry> aEntries = activeDirectory.search(config.getAdUserSearchBaseDn(),
								aUserFilterAttributes, null);
						for (Entry entry : aEntries) {
							try {
								logger.info("Copying entry {} to OpenLDAP...", entry.getDn().getName());
								printMessage(Messages.getString("COPYING_ENTRY_TO_LDAP_", entry.getDn().getName()), display);

								String newDn = null;
								Map<String, String[]> newAttributes = null;

								logger.info("Reading attributes of the entry.");
								printMessage(Messages.getString("READING_ATTR_OF_ENTRY_", entry.getDn().getName()), display);
								Collection<Attribute> attributes = entry.getAttributes();
								if (attributes != null) {
									newAttributes = new HashMap<String, String[]>();
									newAttributes.put("objectClass",
											validUserObjClsValues.toArray(new String[validUserObjClsValues.size()]));
									System.out.println("---------------------validUserObjClsValues: " + validUserObjClsValues != null ? validUserObjClsValues.size() : "null");
									for (Attribute attribute : attributes) {
										if (attribute.getId().equalsIgnoreCase("objectClass")) {
											// Ignore object class, use valid
											// OpenLDAP object
											// classes instead!
											continue;
										}
										// Determine new DN!
										if (attribute.getId().equalsIgnoreCase(config.getLdapNewUserEntryPrefixAttr())
												&& attribute.get() != null) {
											newDn = config.getLdapNewUserEntryPrefixAttr();
											newDn += "=" + attribute.get() + ",";
											newDn += config.getLdapNewUserEntrySuffix();
											logger.info("Creating new DN {} for the entry...", newDn);
											printMessage(Messages.getString("CREATING_NEW_DN_FOR_ENTRY_", newDn), display);
										}
										String log = "";
										// Copy this AD attribute only if it has
										// some value AND
										// it is a valid attribute.
										int index = -1;
										if (attribute.size() > 0
												&& (validUserAttrNames == null || (index = validUserAttrNames.indexOf(
														attribute.getId().toLowerCase(Locale.ENGLISH))) > -1)) {
											String[] attrValues = new String[attribute.size()];
											int i = 0;
											for (Value<?> value : attribute) {
												if (value == null || value.getValue() == null) {
													continue;
												}
												attrValues[i] = value.getValue().toString();
												log += value.getValue().toString() + " ";
											}
											newAttributes.put(attribute.getId(), attrValues);
											userAttrUsed[index] = true;
											logger.info("Copying new attribute {} = {} for the entry...",
													new Object[] { attribute.getUpId(), log });
											printMessage(Messages.getString("COPYING_NEW_ATTRIBUTE_", attribute.getId()), display);
										}
									}
								}

								// Check if there is any non-used attributes
								// left.
								for (int i = 0; i < userAttrUsed.length; i++) {
									if (!userAttrUsed[i]) {
										// This attribute was not used!
										String attribute = validUserAttrNames.get(i);
										newAttributes.put(attribute, new String[] { random.nextString() });
									}
								}

								// Create entry
								logger.info("Trying to add new user entry to OpenLDAP: {}", newDn);
								printMessage(Messages.getString("ADD_NEW_USER_TO_LDAP_", newDn), display);
								openLdap.addEntry(newDn, newAttributes);

							} catch (Exception e) {
								logger.error(e.getMessage(), e);
								printMessage(Messages.getString("ERROR_OCCURED_", e.getMessage()), display);
							}
						}

						//
						// Migrate groups
						//

						// Collect OpenLDAP attributes, so that we can map AD
						// attributes to
						// them.
						logger.info("Collecting group attributes in OpenLDAP...");
						printMessage(Messages.getString("COLLECTING_GROUP_ENTRIES_LDAP"), display);
						ArrayList<String> validGroupAttrNames = null;
						boolean[] attrUsed = null;
						ArrayList<String> validObjClsValues = null;
						List<Entry> oGroupEntries = openLdap.search(config.getLdapGroupSearchBaseDn(),
								oGroupFilterAttributes, null);
						if (oGroupEntries != null && !oGroupEntries.isEmpty()) {
							// Select first entry
							Entry entry = oGroupEntries.get(0);

							validGroupAttrNames = new ArrayList<String>();
							validObjClsValues = new ArrayList<String>();

							// Iterate over its each attribute
							Collection<Attribute> attributes = entry.getAttributes();
							if (attributes != null) {
								for (Attribute attribute : attributes) {
									if (attribute.getId().equalsIgnoreCase("member")) {
										// Ignore 'member' attribute for now. It
										// will be handled
										// later.
										continue;
									}
									// If it is an object class, store only its
									// valid object
									// class values...
									if (attribute.getId().equalsIgnoreCase("objectClass")) {
										for (Value<?> value : attribute) {
											if (value == null || value.getValue() == null) {
												continue;
											}
											validObjClsValues.add(value.getValue().toString());
										}
									} else {
										// Flag current attribute as valid
										validGroupAttrNames.add(attribute.getId().toLowerCase(Locale.ENGLISH));
									}
								}
							}

							attrUsed = new boolean[validGroupAttrNames.size()];
						}

						// Search group entries in Active Directory
						// For each entry in AD, we try to create a new one in
						// OpenLDAP
						List<Entry> entries = activeDirectory.search(config.getAdGroupSearchBaseDn(),
								aGroupFilterAttributes, null);
						for (Entry entry : entries) {
							try {
								logger.info("Copying group entry {} to OpenLDAP...", entry.getDn().getName());
								printMessage(Messages.getString("COPYING_GROUP_ENTRY_TO_LDAP_", entry.getDn().getName()), display);
								
								String newDn = null;
								Map<String, String[]> newAttributes = null;
								ArrayList<String> groupMembers = new ArrayList<String>();

								logger.info("Reading group attributes of the entry.");
								printMessage(Messages.getString("READING_GROUP_ATTR_OF_ENTRY_", entry.getDn().getName()), display);
								Collection<Attribute> attributes = entry.getAttributes();
								if (attributes != null) {
									newAttributes = new HashMap<String, String[]>();
									System.out.println("---------------------validUserObjClsValues: " + validUserObjClsValues != null ? validUserObjClsValues.size() : "null");
									newAttributes.put("objectClass",
											validObjClsValues.toArray(new String[validObjClsValues.size()]));
									for (Attribute attribute : attributes) {
										if (attribute.getId().equalsIgnoreCase("objectClass")) {
											// Ignore object class, use valid
											// OpenLDAP object
											// classes instead!
											continue;
										}
										// Determine new DN!
										if (attribute.getId().equalsIgnoreCase(config.getLdapNewGroupEntryPrefixAttr())
												&& attribute.get() != null) {
											newDn = config.getLdapNewGroupEntryPrefixAttr();
											newDn += "=" + attribute.get() + ",";
											newDn += config.getLdapNewGroupEntrySuffix();
											logger.info("Creating new DN {} for the group entry...", newDn);
											printMessage(Messages.getString("CREATING_NEW_DN_FOR_GROUP_ENTRY_", newDn), display);
										}
										if (attribute.size() > 0) {
											// Copy this AD attribute only if it
											// has some value
											// AND it is a valid attribute.
											String log = "";
											int index = -1;
											if (validGroupAttrNames == null || (index = validGroupAttrNames
													.indexOf(attribute.getId().toLowerCase(Locale.ENGLISH))) > -1) {
												String[] attrValues = new String[attribute.size()];
												int i = 0;
												for (Value<?> value : attribute) {
													if (value == null || value.getValue() == null) {
														continue;
													}
													attrValues[i] = value.getValue().toString();
													log += value.getValue().toString() + " ";
												}
												newAttributes.put(attribute.getId(), attrValues);
												attrUsed[index] = true;
												logger.info("Copying new attribute {} = {} for the group entry...",
														new Object[] { attribute.getUpId(), log });
												printMessage(Messages.getString("COPYING_NEW_ATTR_FOR_GROUP_ENTRY_", attribute.getUpId()), display);
											}
											// If this is a 'member' attribute,
											// save its values
											// to
											// handle later.
											if (attribute.getId().equalsIgnoreCase("member")) {
												for (Value<?> value : attribute) {
													if (value == null || value.getValue() == null) {
														continue;
													}
													groupMembers.add(value.getValue().toString());
												}
											}
										}
									}
								}

								// Check if there is any non-used attributes
								// left.
								for (int i = 0; i < attrUsed.length; i++) {
									if (!attrUsed[i]) {
										// This attribute was not used!
										String attribute = validGroupAttrNames.get(i);
										newAttributes.put(attribute, new String[] { random.nextString() });
									}
								}

								// Add its members
								if (!groupMembers.isEmpty()) {
									logger.info("Trying to find members of the group entry: {}", newDn);
									printMessage(Messages.getString("FIND_MEMBERS_OF_GROUP_ENTRY_", newDn), display);
									ArrayList<String> newGroupMembers = new ArrayList<String>();
									for (String aDn : groupMembers) {
										try {
											Entry aEntry = activeDirectory.findEntry(aDn);
											String oDn = calculateNewDn(aEntry);
											Entry oEntry = openLdap.findEntry(oDn);
											if (oEntry != null) {
												newGroupMembers.add(oDn);
											}
										} catch (Exception e) {
											logger.error(e.getMessage(), e);
											printMessage(Messages.getString("ERROR_OCCURED_", e.getMessage()), display);
										}
									}
									if (!newGroupMembers.isEmpty()) {
										newAttributes.put("member",
												newGroupMembers.toArray(new String[newGroupMembers.size()]));
									}
								}

								// Create entry
								logger.info("Trying to add new group entry to OpenLDAP: {}", newDn);
								printMessage(Messages.getString("ADD_NEW_GROUP_ENTRY_TO_LDAP", newDn), display);
								openLdap.addEntry(newDn, newAttributes);

							} catch (Exception e) {
								logger.error(e.getMessage(), e);
								printMessage(Messages.getString("ERROR_OCCURED_", e.getMessage()), display);
							}
						}

						// Close connections
						openLdap.destroy();
						activeDirectory.destroy();

						isInstallationFinished = true;
						// Set progress bar to complete
						setProgressBar(100, display);
						printMessage(Messages.getString("MIGRATION_COMPLETED"), display);
						config.setInstallationFinished(isInstallationFinished);
						// To enable finish button
						setPageCompleteAsync(isInstallationFinished, display);
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
						printMessage(Messages.getString("ERROR_OCCURED_", e.getMessage()), display);
					}
				}
			};

			Thread thread = new Thread(mainRunnable);
			thread.start();
		}

		return super.getNextPage();
	}

	private String calculateNewDn(Entry entry) {
		String newDn = null;
		Collection<Attribute> attributes = entry.getAttributes();
		if (attributes != null) {
			for (Attribute attribute : attributes) {
				if (attribute.getId().equalsIgnoreCase(config.getLdapNewUserEntryPrefixAttr())
						&& attribute.get() != null) {
					newDn = config.getLdapNewUserEntryPrefixAttr();
					newDn += "=" + attribute.get() + ",";
					newDn += config.getLdapNewUserEntrySuffix();
					logger.info("Creating new DN {} for the entry...", newDn);
				}
			}
		}
		return newDn;
	}

	/**
	 * Prints log message to the log console widget
	 * 
	 * @param message
	 */
	private void printMessage(final String message, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				txtLogConsole.setText((txtLogConsole.getText() != null && !txtLogConsole.getText().isEmpty()
						? txtLogConsole.getText() + "\n" : "") + message);
			}
		});
	}

	/**
	 * Sets progress bar selection (Increases progress bar percentage by
	 * increment value.)
	 * 
	 * @param selection
	 */
	private void setProgressBar(final int increment, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				progressBarPercent += increment;
				progressBar.setSelection(progressBarPercent);
			}
		});
	}

	/**
	 * Sets page complete status asynchronously.
	 * 
	 * @param isComplete
	 */
	private void setPageCompleteAsync(final boolean isComplete, Display display) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				setPageComplete(isComplete);
			}
		});
	}

	@Override
	public IWizardPage getPreviousPage() {
		// Do not allow to go back from this page if installation completed
		// successfully.
		if (canGoBack) {
			return super.getPreviousPage();
		} else {
			return null;
		}
	}

	@Override
	public NextPageEventType getNextPageEventType() {
		return nextPageEventType;
	}

	@Override
	public void setNextPageEventType(NextPageEventType nextPageEventType) {
		this.nextPageEventType = nextPageEventType;
	}

}
