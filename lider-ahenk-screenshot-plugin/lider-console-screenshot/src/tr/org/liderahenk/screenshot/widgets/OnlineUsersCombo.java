package tr.org.liderahenk.screenshot.widgets;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Emre Akkaya <emre.akkaya@agem.com.tr>
 *
 */
public class OnlineUsersCombo extends Combo {

	private String dn;

	/**
	 * 
	 * @param parent
	 * @param style
	 * @param dn
	 */
	public OnlineUsersCombo(Composite parent, int style, String dn) {
		super(parent, style);
		this.dn = dn;
	}

	@Override
	protected void checkSubclass() {
		// By default, subclassing is not allowed for many of the SWT Controls
		// This empty method disables the check that prevents subclassing of
		// this class
	}

	public String getDn() {
		return dn;
	}

}
