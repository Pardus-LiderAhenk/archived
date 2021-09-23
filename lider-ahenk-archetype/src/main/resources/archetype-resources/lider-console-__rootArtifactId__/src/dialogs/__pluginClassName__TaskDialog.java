#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.dialogs.DefaultTaskDialog;
import tr.org.liderahenk.liderconsole.core.exceptions.ValidationException;

/**
 * Task execution dialog for ${rootArtifactId} plugin.
 * 
 */
public class ${pluginClassName}TaskDialog extends DefaultTaskDialog {
	
	private static final Logger logger = LoggerFactory.getLogger(${pluginClassName}TaskDialog.class);
	
	// TODO do not forget to change this constructor if SingleSelectionHandler is used!
	public ${pluginClassName}TaskDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell, dnSet);
	}

	@Override
	public String createTitle() {
		// TODO dialog title
		return null;
	}

	@Override
	public Control createTaskDialogArea(Composite parent) {
		// TODO create your task-related widgets here
		return null;
	}

	@Override
	public void validateBeforeExecution() throws ValidationException {
		// TODO triggered before task execution
	}
	
	@Override
	public Map<String, Object> getParameterMap() {
		// TODO custom parameter map
		return new HashMap<String, Object>();
	}

	@Override
	public String getCommandId() {
		// TODO command id which is used to match tasks with ICommand class in the corresponding Lider plugin
		return "SAMPLE_COMMAND1";
	}

	@Override
	public String getPluginName() {
		return "${rootArtifactId}";
	}

	@Override
	public String getPluginVersion() {
		return "${version}";
	}
	
}
