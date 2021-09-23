package tr.org.liderahenk.installer.lider.utils;

import java.util.LinkedList;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;

import tr.org.liderahenk.installer.lider.config.LiderSetupConfig;
import tr.org.liderahenk.installer.lider.wizard.LiderSetupWizard;
import tr.org.liderahenk.installer.lider.wizard.pages.IDatabasePage;
import tr.org.liderahenk.installer.lider.wizard.pages.ILdapPage;
import tr.org.liderahenk.installer.lider.wizard.pages.ILiderPage;
import tr.org.liderahenk.installer.lider.wizard.pages.IXmppPage;
import tr.org.liderahenk.installer.lider.wizard.pages.InstallationStatusPage;
import tr.org.liderahenk.installer.lider.wizard.pages.LiderAccessPage;
import tr.org.liderahenk.installer.lider.wizard.pages.LiderClusterConfPage;
import tr.org.liderahenk.installer.lider.wizard.pages.XmppAccessPage;
import tr.org.liderahenk.installer.lider.wizard.pages.XmppClusterConfPage;

/**
 * This contains some helpful methods to control the page flow of wizard.
 * 
 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
 * 
 */
public class PageFlowHelper {

	/**
	 * Tries to find the first instance of the provided class in the linked
	 * list.
	 * 
	 * @param pagesList
	 * @param cls
	 * @return
	 */
	private static IWizardPage findFirstInstance(LinkedList<IWizardPage> pagesList, Class<?> cls) {
		if (pagesList != null) {
			for (IWizardPage page : pagesList) {
				if (cls.isInstance(page)) {
					return page;
				}
			}
		}
		return null;
	}

	/**
	 * This method decides next page according to user's component choices
	 * 
	 * @return
	 */
	public static IWizardPage selectNextPage(LiderSetupConfig config, WizardPage page) {
		LinkedList<IWizardPage> pagesList = ((LiderSetupWizard) page.getWizard()).getPagesList();
		if (page instanceof IDatabasePage) {
			if (config.isInstallLdap()) {
				return findFirstInstance(pagesList, ILdapPage.class);
			} else if (config.isInstallXmpp()) {
				if (config.isXmppCluster()) {
					return page.getWizard().getPage(XmppClusterConfPage.class.getName());
				} else {
					return page.getWizard().getPage(XmppAccessPage.class.getName());
				}
			} else if (config.isInstallLider()) {
				if (config.isLiderCluster()) {
					return page.getWizard().getPage(LiderClusterConfPage.class.getName());
				} else {
					return page.getWizard().getPage(LiderAccessPage.class.getName());
				}
			}
		} else if (page instanceof ILdapPage) {
			if (config.isInstallXmpp()) {
				if (config.isXmppCluster()) {
					return page.getWizard().getPage(XmppClusterConfPage.class.getName());
				} else {
					return page.getWizard().getPage(XmppAccessPage.class.getName());
				}
			} else if (config.isInstallLider()) {
				if (config.isLiderCluster()) {
					return page.getWizard().getPage(LiderClusterConfPage.class.getName());
				} else {
					return page.getWizard().getPage(LiderAccessPage.class.getName());
				}
			}
		} else if (page instanceof IXmppPage) {
			if (config.isLiderCluster()) {
				return page.getWizard().getPage(LiderClusterConfPage.class.getName());
			} else {
				return page.getWizard().getPage(LiderAccessPage.class.getName());
			}
		}
		return null;
	}

	/**
	 * This method returns true if given page parameter is the last page of
	 * wizard.
	 * 
	 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
	 * 
	 * @param config
	 * @param page
	 * @return true if given page parameter is the last page of
	 * wizard.
	 */
	public static boolean isLastPage(LiderSetupConfig config, WizardPage page) {
		if (page instanceof InstallationStatusPage && isLastComponent(config, page)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * There are four components in this wizard (Database, Ldap, Xmpp and
	 * Lider). And this method returns true if given page parameter belongs to
	 * last component selected to be installed.
	 * 
	 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
	 * 
	 * @param config
	 * @param page
	 * @return true if given page parameter belongs to last component of wizard.
	 */
	public static boolean isLastComponent(LiderSetupConfig config, WizardPage page) {
		if (page instanceof ILiderPage) {
			return true;
		} else if (page instanceof IXmppPage && !config.isInstallLider()) {
			return true;
		} else if (page instanceof ILdapPage && !config.isInstallLider() && !config.isInstallXmpp()) {
			return true;
		} else if (page instanceof IDatabasePage && !config.isInstallLider() && !config.isInstallXmpp()
				&& !config.isInstallLdap()) {
			return true;
		} else {
			return false;
		}
	}
}
