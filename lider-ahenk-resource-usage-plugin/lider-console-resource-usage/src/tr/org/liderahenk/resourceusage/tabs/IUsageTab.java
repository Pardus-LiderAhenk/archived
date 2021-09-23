package tr.org.liderahenk.resourceusage.tabs;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;

public interface IUsageTab {
	public void createTab(Composite tabComposite, Set<String> dnSet, String pluginName, String pluginVersion) throws Exception;
	public void validateBeforeSave() throws ValidationException;
	public List<Object> addTableItem(Object tableItem);
	public void removeTableItems();
}
