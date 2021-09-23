package tr.org.liderahenk.service.editingsupport;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.SWT;

import tr.org.liderahenk.service.labelprovider.StatusLabelProvider;
import tr.org.liderahenk.service.model.ServiceListItem;
import tr.org.liderahenk.service.model.DesiredStatus;

public class StatusEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	private final CellEditor editor;

	public StatusEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new ComboBoxViewerCellEditor(viewer.getTable(), SWT.DROP_DOWN | SWT.READ_ONLY);
		((ComboBoxViewerCellEditor) this.editor).setContentProvider(new ArrayContentProvider());
		((ComboBoxViewerCellEditor) this.editor).setLabelProvider(new StatusLabelProvider());
		((ComboBoxViewerCellEditor) this.editor).setInput(DesiredStatus.values());
		LayoutData layoutData = ((ComboBoxViewerCellEditor) this.editor).getLayoutData();
		layoutData.minimumWidth = 100;
		layoutData.grabHorizontal = true;
		layoutData.horizontalAlignment = SWT.CENTER;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {

		if (element instanceof ServiceListItem) {
			return ((ServiceListItem) element).getDesiredServiceStatus();
		}
		return DesiredStatus.NA;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof ServiceListItem) {
			((ServiceListItem) element).setDesiredServiceStatus((DesiredStatus) value);
			viewer.update(element, null);
		}
	}

}
