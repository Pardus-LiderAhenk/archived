package tr.org.pardus.mys.liderahenksetup.utils.gui;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @author <a href="mailto:caner.feyzullahoglu@agem.com.tr">Caner
 *         Feyzullahoglu</a>
 * 
 */
public class GUIHelper {

	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static Text createText(Composite parent) {
		return createText(parent, new GridData(GridData.FILL, GridData.FILL, true, true));
	}

	/**
	 * 
	 * @param parent
	 * @param layoutData
	 * @return
	 */
	public static Text createText(Composite parent, Object layoutData) {
		return createText(parent, layoutData, SWT.NONE | SWT.BORDER | SWT.SINGLE);
	}

	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static Text createPasswordText(Composite parent) {
		return createPasswordText(parent, new GridData(GridData.FILL, GridData.FILL, true, true));
	}

	/**
	 * 
	 * This method creates a text with given style. (e.g. for style parameter:
	 * SWT.NONE | SWT.SINGLE | SWT.PASSWORD)
	 * 
	 * @author Caner Feyzullahoğlu <caner.feyzullahoglu@agem.com.tr>
	 * @param parent
	 * @param layoutData
	 * @param style
	 * @return
	 */
	public static Text createText(Composite parent, Object layoutData, int style) {
		Text t = new Text(parent, style);
		t.setLayoutData(layoutData);
		t.setBackground(getApplicationBackground());
		t.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		// t.setFont(FontProvider.getInstance().get(FontProvider.INPUT_FONT));
		return t;
	}

	/**
	 * 
	 * @param parent
	 * @param layoutData
	 * @return
	 */
	public static Text createPasswordText(Composite parent, Object layoutData) {
		Text t = new Text(parent, SWT.NONE | SWT.BORDER | SWT.SINGLE | SWT.PASSWORD);
		t.setLayoutData(layoutData);
		t.setBackground(getApplicationBackground());
		t.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
		// t.setFont(FontProvider.getInstance().get(FontProvider.INPUT_FONT));
		return t;
	}

	/**
	 * 
	 * @param parent
	 * @param numColumns
	 * @return
	 */
	public static Composite createComposite(Composite parent, int numColumns) {
		return createComposite(parent, new GridLayout(numColumns, false),
				new GridData(GridData.FILL, GridData.FILL, true, true));
	}

	/**
	 * 
	 * @param parent
	 * @param layout
	 * @param layoutData
	 * @return
	 */
	public static Composite createComposite(Composite parent, Layout layout, Object layoutData) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayoutData(layoutData);
		c.setLayout(layout);
		c.setBackground(getApplicationBackground());
		return c;
	}

	/**
	 * 
	 * @param parent
	 * @param numColumns
	 * @return
	 */
	public static Group createGroup(Composite parent, int numColumns) {
		return createGroup(parent, new GridLayout(numColumns, false),
				new GridData(GridData.FILL, GridData.FILL, true, true));
	}

	/**
	 * 
	 * @param parent
	 * @param layout
	 * @param layoutData
	 * @return
	 */
	public static Group createGroup(Composite parent, Layout layout, Object layoutData) {
		Group g = new Group(parent, SWT.NONE);
		g.setLayoutData(layoutData);
		g.setLayout(layout);
		g.setBackground(getApplicationBackground());
		return g;
	}

	/**
	 * 
	 * @param parent
	 * @param buttonType
	 * @return
	 */
	public static Button createButton(Composite parent, int buttonType) {
		return createButton(parent, buttonType, "");
	}

	/**
	 * 
	 * @param parent
	 * @param buttonType
	 * @param text
	 * @return
	 */
	public static Button createButton(Composite parent, int buttonType, String text) {
		Button b = new Button(parent, buttonType | SWT.BORDER);
		b.setText(text);
		b.setBackground(getApplicationBackground());
		// b.setFont(FontProvider.getInstance().get(FontProvider.LABEL_FONT));
		return b;
	}

	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static Label createLabel(Composite parent) {
		return createLabel(parent, "");
	}

	/**
	 * 
	 * @param parent
	 * @param text
	 * @return
	 */
	public static Label createLabel(Composite parent, String text) {
		Label l = new Label(parent, SWT.NONE);
		l.setText(text);
		l.setBackground(getApplicationBackground());
		// l.setFont(FontProvider.getInstance().get(FontProvider.LABEL_FONT));
		return l;
	}

	/**
	 * 
	 * @param parent
	 * @param text
	 * @param style
	 * @return
	 */
	public static Label createLabel(Composite parent, String text, int style) {
		Label l = new Label(parent, style | SWT.NONE);
		l.setText(text);
		l.setBackground(getApplicationBackground());
		// l.setFont(FontProvider.getInstance().get(FontProvider.LABEL_FONT));
		return l;
	}

	public static Color getApplicationBackground() {
		return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}

	/**
	 * @param parent
	 * @param image
	 * @param mouseOverImage
	 * @param mouseListener
	 * @return Creates a label with a custom image (ImageButton) which works
	 *         like a SWT.PUSH button and changes its display when mouse over.
	 *         <strong><i>mouseOverImage</i></strong> and
	 *         <strong>mouseListener</strong> parameters can be passed as
	 *         <strong>null</strong> if handling such event is not needed.
	 */
	public static Label imageButton(Composite parent, final Image image, final Image mouseOverImage,
			MouseListener mouseListener) {

		final Label imageButton = new Label(parent, SWT.NONE);
		imageButton.setImage(image);

		if (mouseOverImage != null) {
			imageButton.addListener(SWT.MouseEnter, new Listener() {
				@Override
				public void handleEvent(Event event) {
					imageButton.setImage(mouseOverImage);
				}
			});

			imageButton.addListener(SWT.MouseExit, new Listener() {
				@Override
				public void handleEvent(Event event) {
					imageButton.setImage(image);
				}
			});
		}

		if (mouseListener != null) {
			imageButton.addMouseListener(mouseListener);
		}

		return null;
	}

	/**
	 * Creates a new wizard dialog for the given wizard. And as an extra option
	 * to standard <strong>WizardDialog</strong> constructor, size of dialog can
	 * be given with a <strong>Point</point>.
	 * 
	 * @param parentShell
	 *            - the parent shell
	 * @param newWizard
	 *            - the wizard this dialog is working on
	 * @param size
	 *            - size of the dialog (x coordinate : width, y coordinate :
	 *            height)
	 */
	public static WizardDialog createDialog(Shell parentShell, IWizard newWizard, Point size) {
		if (size == null) {
			return new WizardDialog(parentShell, newWizard);
		} else {
			WizardDialog wd = new WizardDialog(parentShell, newWizard);
			// TODO setMinimumPageSize does not work.
			wd.setMinimumPageSize(size);
			wd.setPageSize(size);
			return wd;
		}
	}

}
