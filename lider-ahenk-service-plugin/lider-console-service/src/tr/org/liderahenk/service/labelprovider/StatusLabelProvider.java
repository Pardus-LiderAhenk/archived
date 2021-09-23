package tr.org.liderahenk.service.labelprovider;

import org.eclipse.jface.viewers.LabelProvider;

import tr.org.liderahenk.service.model.DesiredStatus;


public class StatusLabelProvider extends LabelProvider {

	@Override
	public String getText(Object element) {
		DesiredStatus status = (DesiredStatus) element;
		return status.getMessage();
	}

}
