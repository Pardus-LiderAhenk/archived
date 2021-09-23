package tr.org.liderahenk.migrate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.directory.api.ldap.model.entry.Attribute;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.entry.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.enums.SearchFilterEnum;
import tr.org.liderahenk.utils.LdapSearchFilterAttribute;
import tr.org.liderahenk.utils.LdapUtils;
import tr.org.liderahenk.utils.PropertyReader;
import tr.org.liderahenk.utils.RandomStringGenerator;

public class MigrateGroups {

	private final static Logger logger = LoggerFactory.getLogger(MigrateGroups.class);
	private final static RandomStringGenerator random = new RandomStringGenerator(10);

	private LdapUtils activeDirectory;
	private LdapUtils openLdap;
	private String aBaseDn;
	private String[] aObjectClasses;
	private List<LdapSearchFilterAttribute> aFilterAttributes;
	private String oBaseDn;
	private String[] oObjectClasses;
	private List<LdapSearchFilterAttribute> oFilterAttributes;

	public MigrateGroups(LdapUtils activeDirectory, LdapUtils openLdap) {
		this.activeDirectory = activeDirectory;
		this.openLdap = openLdap;

		// Search parameters for Active Directory
		aBaseDn = PropertyReader.getInstance().get("active.directory.group.search.base.dn");
		aObjectClasses = PropertyReader.getInstance().getStringArr("active.directory.group.search.object.classes");
		aFilterAttributes = new ArrayList<LdapSearchFilterAttribute>();
		if (aObjectClasses != null && aObjectClasses.length > 0) {
			for (String objectClass : aObjectClasses) {
				aFilterAttributes.add(new LdapSearchFilterAttribute("objectClass", objectClass, SearchFilterEnum.EQ));
			}
		}

		// Search parameters for OpenLDAP
		oBaseDn = PropertyReader.getInstance().get("open.ldap.group.search.base.dn");
		oObjectClasses = PropertyReader.getInstance().getStringArr("open.ldap.group.search.object.classes");
		oFilterAttributes = new ArrayList<LdapSearchFilterAttribute>();
		if (oObjectClasses != null && oObjectClasses.length > 0) {
			for (String objectClass : oObjectClasses) {
				oFilterAttributes.add(new LdapSearchFilterAttribute("objectClass", objectClass, SearchFilterEnum.EQ));
			}
		}
	}

	public void migrate() throws Exception {

		// Collect OpenLDAP attributes, so that we can map AD attributes to
		// them.
		logger.info("Collecting group attributes in OpenLDAP...");
		ArrayList<String> validAttrNames = null;
		boolean[] attrUsed = null;
		ArrayList<String> validObjClsValues = null;
		List<Entry> oEntries = openLdap.search(oBaseDn, oFilterAttributes, null);
		if (oEntries != null && !oEntries.isEmpty()) {
			// Select first entry
			Entry entry = oEntries.get(0);

			validAttrNames = new ArrayList<String>();
			validObjClsValues = new ArrayList<String>();

			// Iterate over its each attribute
			Collection<Attribute> attributes = entry.getAttributes();
			if (attributes != null) {
				for (Attribute attribute : attributes) {
					if (attribute.getId().equalsIgnoreCase("member")) {
						// Ignore 'member' attribute for now. It will be handled
						// later.
						continue;
					}
					// If it is an object class, store only its valid object
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
						validAttrNames.add(attribute.getId().toLowerCase(Locale.ENGLISH));
					}
				}
			}

			attrUsed = new boolean[validAttrNames.size()];
		}

		// Search group entries in Active Directory
		// For each entry in AD, we try to create a new one in OpenLDAP
		List<Entry> entries = activeDirectory.search(aBaseDn, aFilterAttributes, null);
		for (Entry entry : entries) {
			try {
				logger.info("Copying group entry {} to OpenLDAP...", entry.getDn().getName());

				String newDn = null;
				Map<String, String[]> newAttributes = null;
				ArrayList<String> groupMembers = new ArrayList<String>();

				logger.info("Reading group attributes of the entry.");
				Collection<Attribute> attributes = entry.getAttributes();
				if (attributes != null) {
					newAttributes = new HashMap<String, String[]>();
					newAttributes.put("objectClass", validObjClsValues.toArray(new String[validObjClsValues.size()]));
					for (Attribute attribute : attributes) {
						if (attribute.getId().equalsIgnoreCase("objectClass")) {
							// Ignore object class, use valid OpenLDAP object
							// classes instead!
							continue;
						}
						// Determine new DN!
						if (attribute.getId().equalsIgnoreCase(
								PropertyReader.getInstance().get("open.ldap.group.new.entry.prefix.attribute"))
								&& attribute.get() != null) {
							newDn = PropertyReader.getInstance().get("open.ldap.group.new.entry.prefix.attribute");
							newDn += "=" + attribute.get() + ",";
							newDn += PropertyReader.getInstance().get("open.ldap.group.new.entry.suffix");
							logger.info("Creating new DN {} for the group entry...", newDn);
						}
						if (attribute.size() > 0) {
							// Copy this AD attribute only if it has some value
							// AND it is a valid attribute.
							String log = "";
							int index = -1;
							if (validAttrNames == null || (index = validAttrNames
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
										new String[] { attribute.getUpId(), log });
							}
							// If this is a 'member' attribute, save its values
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

				// Check if there is any non-used attributes left.
				for (int i = 0; i < attrUsed.length; i++) {
					if (!attrUsed[i]) {
						// This attribute was not used!
						String attribute = validAttrNames.get(i);
						newAttributes.put(attribute, new String[] { random.nextString() });
					}
				}

				// Add its members
				if (!groupMembers.isEmpty()) {
					logger.info("Trying to find members of the group entry: {}", newDn);
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
						}
					}
					if (!newGroupMembers.isEmpty()) {
						newAttributes.put("member", newGroupMembers.toArray(new String[newGroupMembers.size()]));
					}
				}

				// Create entry
				logger.info("Trying to add new group entry to OpenLDAP: {}", newDn);
				openLdap.addEntry(newDn, newAttributes);

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private String calculateNewDn(Entry entry) {
		String newDn = null;
		Collection<Attribute> attributes = entry.getAttributes();
		if (attributes != null) {
			for (Attribute attribute : attributes) {
				if (attribute.getId()
						.equalsIgnoreCase(PropertyReader.getInstance().get("open.ldap.user.new.entry.prefix.attribute"))
						&& attribute.get() != null) {
					newDn = PropertyReader.getInstance().get("open.ldap.user.new.entry.prefix.attribute");
					newDn += "=" + attribute.get() + ",";
					newDn += PropertyReader.getInstance().get("open.ldap.user.new.entry.suffix");
					logger.debug("Creating new DN {} for the entry...", newDn);
				}
			}
		}
		return newDn;
	}

}
