package tr.org.liderahenk.password.handlers;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Display;

import tr.org.liderahenk.liderconsole.core.current.UserSettings;
import tr.org.liderahenk.liderconsole.core.editors.LiderManagementEditor;
import tr.org.liderahenk.liderconsole.core.handlers.MultipleSelectionHandler;
import tr.org.liderahenk.liderconsole.core.ldap.utils.LdapUtils;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.password.dialogs.AddPasswordPolicyDialog;
import tr.org.liderahenk.password.i18n.Messages;

public class AddPasswordPolicyHandler extends MultipleSelectionHandler {

	@Override
	public void executeWithDNSet(Set<String> dnSet) {
		if (!LdapUtils.getInstance().isAdmin(UserSettings.USER_DN)) {
		    Notifier.error(null, Messages.getString("NEED_ADMIN_PRIVILEGE"));
		    return;
		}
		
		String selectedUser= LiderManagementEditor.selectedUserDn;
		List<String> selectedUserList= LiderManagementEditor.selectedDnUserList;
		
		AddPasswordPolicyDialog dialog = new AddPasswordPolicyDialog(Display.getDefault().getActiveShell(), dnSet, selectedUser,selectedUserList);
		dialog.create();
		dialog.open();
	}
}
