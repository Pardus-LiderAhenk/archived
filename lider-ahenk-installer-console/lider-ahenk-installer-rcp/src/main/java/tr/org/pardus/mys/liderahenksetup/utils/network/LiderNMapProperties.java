package tr.org.pardus.mys.liderahenksetup.utils.network;

import org.nmap4j.core.nmap.NMapProperties;
/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class LiderNMapProperties extends NMapProperties {

	private String sudoUser;
	private String sudoUserPassword;
	
	public LiderNMapProperties(String path) {
		super(path);
	}

	@Override
	public String getFullyFormattedCommand() {
		String command = super.getFullyFormattedCommand();
		if (sudoUser != null && sudoUserPassword != null) {
			// Execute as specified sudo user and wait for password!
			command = "sudo -u " + sudoUser + " -S " + command;
		}
		return command;
	}

	public String getSudoUser() {
		return sudoUser;
	}

	public void setSudoUser(String sudoUser) {
		this.sudoUser = sudoUser;
	}

	public String getSudoUserPassword() {
		return sudoUserPassword;
	}

	public void setSudoUserPassword(String sudoUserPassword) {
		this.sudoUserPassword = sudoUserPassword;
	}

}
