package tr.org.liderahenk.packagemanager.editingsupport;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import tr.org.liderahenk.packagemanager.labelprovider.DesiredStatusLabelProvider;
import tr.org.liderahenk.packagemanager.model.DesiredPackageStatus;
import tr.org.liderahenk.packagemanager.model.PackageInfo;

public class DesiredStatusEditingSupport extends EditingSupport {

	private final TableViewer viewer;
	private final CellEditor editor;

	public DesiredStatusEditingSupport(TableViewer viewer) {
		super(viewer);
		this.viewer = viewer;
		this.editor = new ComboBoxViewerCellEditor(viewer.getTable(), SWT.DROP_DOWN | SWT.READ_ONLY);
		((ComboBoxViewerCellEditor) this.editor).setContentProvider(new ArrayContentProvider());
		((ComboBoxViewerCellEditor) this.editor).setLabelProvider(new DesiredStatusLabelProvider());
		((ComboBoxViewerCellEditor) this.editor).setInput(DesiredPackageStatus.values());
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
		if (element instanceof PackageInfo) {
			return ((PackageInfo) element).getDesiredStatus();
		}
		return DesiredPackageStatus.NA;
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (element instanceof PackageInfo) {
			((PackageInfo) element).setDesiredStatus((DesiredPackageStatus) value);
			viewer.update(element, null);
		}
	}

}
