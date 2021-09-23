package tr.org.pardus.mys.liderahenksetup.utils.network;

import java.util.LinkedHashMap;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.nmap4j.data.nmaprun.Host;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class TableThreadHelper {

	private TableViewer tblVwrSetup;
	private LinkedHashMap<String, Host> hosts;

	public TableThreadHelper(TableViewer tblVwrSetup,
			LinkedHashMap<String, Host> hosts) {
		this.tblVwrSetup = tblVwrSetup;
		this.hosts = hosts;
	}

	public synchronized void refresh() {
		Display.getDefault().asyncExec(new Runnable() {
            public void run() {
            	tblVwrSetup.refresh();
            	Table table = tblVwrSetup.getTable();
            	for (int i = 0, n = table.getColumnCount(); i < n; i++) {
            		table.getColumn(i).pack();
            	}
            }
         });
	}

	public LinkedHashMap<String, Host> getHosts() {
		return hosts;
	}

}
