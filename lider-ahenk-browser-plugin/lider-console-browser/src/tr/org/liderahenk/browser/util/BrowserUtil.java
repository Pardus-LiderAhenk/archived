package tr.org.liderahenk.browser.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import tr.org.liderahenk.browser.constants.BrowserConstants;
import tr.org.liderahenk.browser.model.BrowserPreference;
import tr.org.liderahenk.liderconsole.core.model.Profile;

/**
 * Utility class that can be used to manage browser preferences
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class BrowserUtil {

	/**
	 * 
	 * @param profile
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Set<BrowserPreference> getPreferences(Profile profile) {
		if (profile != null && profile.getProfileData() != null) {
			try {
				Set<BrowserPreference> preferences = new HashSet<BrowserPreference>();
				ArrayList<LinkedHashMap<String, String>> temp = (ArrayList<LinkedHashMap<String, String>>) profile
						.getProfileData().get(BrowserConstants.PREFERENCES_MAP_KEY);
				if (temp != null) {
					for (LinkedHashMap<String, String> pref : temp) {
						String value = pref.get("value");
						String preferenceName = pref.get("preferenceName");
						BrowserPreference preference = new BrowserPreference(preferenceName, value);
						preferences.add(preference);
					}
				}
				return preferences;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param preferences
	 * @param preferenceName
	 * @return
	 */
	public static String getPreferenceValue(Set<BrowserPreference> preferences, String preferenceName) {
		if (preferences != null) {
			for (BrowserPreference pref : preferences) {
				if (pref != null && pref.getPreferenceName() != null
						&& pref.getPreferenceName().equals(preferenceName)) {
					return pref.getValue();
				}
			}
		}
		return null;
	}

}
