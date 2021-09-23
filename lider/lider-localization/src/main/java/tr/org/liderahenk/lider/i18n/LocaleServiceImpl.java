/*
*
*    Copyright © 2015-2016 Tübitak ULAKBIM
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.lider.i18n;

import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.i18n.ILocaleService;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class LocaleServiceImpl implements ILocaleService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private IConfigurationService configurationService;

	private BundleContext context;

	/**
	 * Last locale property which is used to refresh resource bundle if
	 * necessary.
	 */
	private String lastLocaleProp = null;

	/**
	 * Default bundle name
	 */
	private static final String BUNDLE_NAME = "Messages";

	public void init() {
		logger.info("Initializing locale service.");
		try {
			// To prevent TR chars during DB schema update on Pardus systems.
			Locale.setDefault(new Locale("en", "US"));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void destroy() {
		logger.info("Destroying locale service...");
	}

	@Override
	public String getString(String key) {
		try {
			String localeProp = configurationService.getLiderLocale();
			if (localeProp != null && !localeProp.equalsIgnoreCase(lastLocaleProp)) {
				PropertyResourceBundle.clearCache();
				lastLocaleProp = localeProp;
			}
			Locale targetLocale = localeProp.contains("-") || localeProp.contains("_")
					? Locale.forLanguageTag(localeProp) : new Locale(localeProp);
			ResourceBundle resourceBundle = PropertyResourceBundle.getBundle(BUNDLE_NAME, targetLocale);
			return resourceBundle.getString(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return '!' + key + '!';
		}
	}

	@Override
	public String getString(long bundleId, String key) {
		try {
			StringBuilder filename = new StringBuilder(BUNDLE_NAME).append("_")
					.append(configurationService.getLiderLocale()).append(".properties");
			URL entry = context.getBundle(bundleId).getEntry(filename.toString());
			PropertyResourceBundle pluginResourceBundle = new PropertyResourceBundle(
					new InputStreamReader(entry.openStream(), StandardCharsets.ISO_8859_1));
			return pluginResourceBundle.getString(key);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return '!' + key + '!';
		}
	}

	@Override
	public Map<String, String> getStrings(long bundleId, String key) {
		try {
			Map<String, String> strings = new HashMap<String, String>();
			Enumeration<URL> entries = context.getBundle(bundleId).findEntries("/", "Messages*.properties", false);
			if (entries != null) {
				while (entries.hasMoreElements()) {
					try {
						URL entry = entries.nextElement();
						int indexOfSeparator = entry.getPath().lastIndexOf("_");
						String language = indexOfSeparator > -1
								? entry.getPath().substring(indexOfSeparator + 1, entry.getPath().lastIndexOf("."))
								: "en";
						PropertyResourceBundle pluginResourceBundle = new PropertyResourceBundle(
								new InputStreamReader(entry.openStream(), StandardCharsets.ISO_8859_1));
						strings.put(language, pluginResourceBundle.getString(key));
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
			}
			return strings;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void setContext(BundleContext context) {
		this.context = context;
	}

}
