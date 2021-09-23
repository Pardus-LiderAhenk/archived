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

public class MigrateUsers {

	private final static Logger logger = LoggerFactory.getLogger(MigrateUsers.class);
	private final static RandomStringGenerator random = new RandomStringGenerator(10);

	private LdapUtils activeDirectory;
	private LdapUtils openLdap;
	private String aBaseDn;
	private String[] aObjectClasses;
	private List<LdapSearchFilterAttribute> aFilterAttributes;
	private String oBaseDn;
	private String[] oObjectClasses;
	private List<LdapSearchFilterAttribute> oFilterAttributes;

	public MigrateUsers(LdapUtils activeDirectory, LdapUtils openLdap) {
		this.activeDirectory = activeDirectory;
		this.openLdap = openLdap;

		// Search parameters for Active Directory
		aBaseDn = PropertyReader.getInstance().get("active.directory.user.search.base.dn");
		aObjectClasses = PropertyReader.getInstance().getStringArr("active.directory.user.search.object.classes");
		aFilterAttributes = new ArrayList<LdapSearchFilterAttribute>();
		if (aObjectClasses != null && aObjectClasses.length > 0) {
			for (String objectClass : aObjectClasses) {
				aFilterAttributes.add(new LdapSearchFilterAttribute("objectClass", objectClass, SearchFilterEnum.EQ));
			}
		}

		// Search parameters for OpenLDAP
		oBaseDn = PropertyReader.getInstance().get("open.ldap.user.search.base.dn");
		oObjectClasses = PropertyReader.getInstance().getStringArr("open.ldap.user.search.object.classes");
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
		logger.debug("Collecting user attributes in OpenLDAP...");
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

		// Search user entries in Active Directory
		// For each entry in AD, we try to create a new one in OpenLDAP
		List<Entry> aEntries = activeDirectory.search(aBaseDn, aFilterAttributes, null);
		for (Entry entry : aEntries) {
			try {
				logger.info("Copying entry {} to OpenLDAP...", entry.getDn().getName());

				String newDn = null;
				Map<String, String[]> newAttributes = null;

				logger.debug("Reading attributes of the entry.");
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
								PropertyReader.getInstance().get("open.ldap.user.new.entry.prefix.attribute"))
								&& attribute.get() != null) {
							newDn = PropertyReader.getInstance().get("open.ldap.user.new.entry.prefix.attribute");
							newDn += "=" + attribute.get() + ",";
							newDn += PropertyReader.getInstance().get("open.ldap.user.new.entry.suffix");
							logger.debug("Creating new DN {} for the entry...", newDn);
						}
						String log = "";
						// Copy this AD attribute only if it has some value AND
						// it is a valid attribute.
						int index = -1;
						if (attribute.size() > 0 && (validAttrNames == null || (index = validAttrNames
								.indexOf(attribute.getId().toLowerCase(Locale.ENGLISH))) > -1)) {
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
							logger.debug("Copying new attribute {} = {} for the entry...",
									new String[] { attribute.getUpId(), log });
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

				// Create entry
				logger.info("Trying to add new user entry to OpenLDAP: {}", newDn);
				openLdap.addEntry(newDn, newAttributes);

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
