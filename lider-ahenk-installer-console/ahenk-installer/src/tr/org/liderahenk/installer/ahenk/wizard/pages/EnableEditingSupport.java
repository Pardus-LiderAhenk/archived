package tr.org.liderahenk.installer.ahenk.wizard.pages;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.nmap4j.data.nmaprun.Host;

import tr.org.pardus.mys.liderahenksetup.utils.network.NetworkUtils;

public class EnableEditingSupport extends EditingSupport {

	private final TableViewer viewer;

	public EnableEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new CheckboxCellEditor(viewer.getTable(), SWT.CHECK);
	}

	@Override
	protected boolean canEdit(Object element) {
		Host host = (Host) element;
		return NetworkUtils.isHostUp(host);
	}

	@Override
	protected Object getValue(Object element) {
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		viewer.update(element, null);
	}

}
