package tr.org.liderahenk.liderconsole.core.widgets.notifier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

import tr.org.liderahenk.liderconsole.core.widgets.Notifier;
import tr.org.liderahenk.liderconsole.core.widgets.NotifierColorsFactory.NotifierTheme;

public class NotifierTest {

	@Test
	public void notification() {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Notifier Snippet");
		shell.setSize(200, 200);
		shell.setLayout(new FillLayout(SWT.VERTICAL));

		final int[] counter = new int[1];
		counter[0] = 0;

		// Yellow theme (default)
		final Button testerInfo = new Button(shell, SWT.PUSH);
		testerInfo.setText("Push me [Info theme]!");
		testerInfo.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				Notifier.notify("New Mail message",
						"Laurent CARON (lcaron@...)<br/><br/>Test message #" + counter[0] + "...");
				counter[0]++;
			}

		});

		// Blue theme
		final Button testerSuccess = new Button(shell, SWT.PUSH);
		testerSuccess.setText("Push me [Success theme]!");
		testerSuccess.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(final Event event) {
				Notifier.notify("New Mail message",
						"Laurent CARON (lcaron@...)<br/><br/>Test message #" + counter[0] + "...",
						NotifierTheme.SUCCESS_THEME);
				counter[0]++;
			}

		});

		// Grey theme
		final Button testerError = new Button(shell, SWT.PUSH);
		testerError.setText("Push me [Error theme]!");
		testerError.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				Notifier.notify("New Mail message",
						"Laurent CARON (lcaron@...)<br/><br/>Test message #" + counter[0] + "...",
						NotifierTheme.ERROR_THEME);
				counter[0]++;
			}
		});

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

}
