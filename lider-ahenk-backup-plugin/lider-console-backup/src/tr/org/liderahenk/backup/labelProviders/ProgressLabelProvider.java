package tr.org.liderahenk.backup.labelProviders;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import tr.org.liderahenk.backup.model.MonitoringTableItem;

public class ProgressLabelProvider extends OwnerDrawLabelProvider {

	private TableViewer tableViewer;

	public ProgressLabelProvider(TableViewer tableViewer) {
		this.tableViewer = tableViewer;
	}

	@Override
	protected void measure(Event event, Object element) {
	}

	@Override
	protected void paint(Event event, Object element) {
		if (element instanceof MonitoringTableItem) {
			String percentage = ((MonitoringTableItem) element).getPercentage();
			if (percentage != null) {
				Table table = tableViewer.getTable();
				TableItem item = (TableItem) event.item;
				int index = table.indexOf(item);
				Color foreground = event.gc.getForeground();
				Color background = event.gc.getBackground();
				event.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));
				event.gc.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_GREEN));
				Rectangle bounds = ((TableItem) event.item).getBounds(event.index);
				int width = (bounds.width - 1) * Integer.parseInt(percentage) / 100;
				event.gc.fillGradientRectangle(event.x, event.y, width, event.height, true);
				Rectangle rect2 = new Rectangle(event.x, event.y, width - 1, event.height - 1);
				event.gc.drawRectangle(rect2);
				event.gc.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
				String text = percentage + "%";
				Point size = event.gc.textExtent(text);
				int offset = Math.max(0, (event.height - size.y) / 2);
				event.gc.drawText(text, event.x + 2, event.y + offset, true);
				event.gc.setForeground(background);
				event.gc.setBackground(foreground);
			}
		}
	}

}
