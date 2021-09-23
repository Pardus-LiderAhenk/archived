package tr.org.liderahenk.packagemanager.labelprovider;

import org.eclipse.jface.viewers.LabelProvider;

import tr.org.liderahenk.packagemanager.model.DesiredPackageStatus;

public class DesiredStatusLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		DesiredPackageStatus status = (DesiredPackageStatus) element;
		return status.getMessage();
	}

}
