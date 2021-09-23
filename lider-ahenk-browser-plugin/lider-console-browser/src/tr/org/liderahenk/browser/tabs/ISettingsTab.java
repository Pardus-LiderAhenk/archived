package tr.org.liderahenk.browser.tabs;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import tr.org.liderahenk.browser.model.BrowserPreference;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;
import tr.org.liderahenk.liderconsole.core.model.Profile;

public interface ISettingsTab {
	public void createInputs(Composite tabComposite, Profile browserProfile) throws Exception;

	public Set<BrowserPreference> getValues();

	public void validateBeforeSave() throws ValidationException;
}
