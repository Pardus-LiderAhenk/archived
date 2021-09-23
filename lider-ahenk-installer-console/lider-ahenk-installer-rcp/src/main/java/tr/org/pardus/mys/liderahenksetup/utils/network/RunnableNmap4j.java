package tr.org.pardus.mys.liderahenksetup.utils.network;

import java.io.IOException;
import java.util.ArrayList;

import org.nmap4j.data.nmaprun.Host;

import tr.org.pardus.mys.liderahenksetup.exception.CommandExecutionException;

/**
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * 
 */
public class RunnableNmap4j implements Runnable {

	private TableThreadHelper tableHelper;
	private NmapParameters params;

	public RunnableNmap4j(TableThreadHelper tableHelper, NmapParameters params) {
		this.tableHelper = tableHelper;
		this.params = params;
	}

	@Override
	public void run() {
		try {
			ArrayList<Host> hosts = NetworkUtils.scanNetwork(params);
			if (hosts != null && hosts.size() > 0) {
				for (Host host : hosts) {
					String ip = NetworkUtils.getIpV4(host);
					if (ip != null && !ip.isEmpty()) {
						Host tHost = tableHelper.getHosts().get(ip);
						tHost.setAddress(host.getAddresses());
						tHost.setDistance(host.getDistance());
						tHost.setEndTime(host.getEndTime());
						tHost.setHostnames(host.getHostnames());
						tHost.setIpIdSequence(host.getIpIdSequence());
						tHost.setOs(host.getOs());
						tHost.setPorts(host.getPorts());
						tHost.setStartTime(host.getStartTime());
						tHost.setStatus(host.getStatus());
						tHost.setTcpSequence(host.getTcpSequence());
						tHost.setTcpTsSequence(host.getTcpTsSequence());
						tHost.setTimes(host.getTimes());
						tHost.setUptime(host.getUptime());
					}
				}
				tableHelper.refresh();
			}
		} catch (CommandExecutionException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "RunnableNmap4j thread with params: " + params.toString();
	}

}
