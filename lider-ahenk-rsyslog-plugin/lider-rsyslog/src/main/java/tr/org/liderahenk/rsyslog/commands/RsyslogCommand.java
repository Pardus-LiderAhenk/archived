package tr.org.liderahenk.rsyslog.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.IPluginDbService;
import tr.org.liderahenk.lider.core.api.rest.requests.ITaskRequest;
import tr.org.liderahenk.lider.core.api.plugin.IPluginInfo;
import tr.org.liderahenk.lider.core.api.plugin.ICommand;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

public class RsyslogCommand implements ICommand {

	private Logger logger = LoggerFactory.getLogger(RsyslogCommand.class);
	
	private static final String DEFAUL_CONFIG = "#  /etc/rsyslog.conf\tConfiguration file for rsyslog.\n#\n#\t\t\tFor more information see\n#\t\t\t/usr/share/doc/rsyslog-doc/html/rsyslog_conf.html\n\n\n#################\n#### MODULES ####\n#################\n\n$ModLoad imuxsock # provides support for local system logging\n$ModLoad imklog   # provides kernel logging support\n#$ModLoad immark  # provides --MARK-- message capability\n\n# provides UDP syslog reception\n$ModLoad imudp\n$UDPServerRun 514\n\n# provides TCP syslog reception\n$ModLoad imtcp\n$InputTCPServerRun 514\n\n$ModLoad ommysql\n\n###########################\n#### GLOBAL DIRECTIVES ####\n###########################\n\n#\n# Use traditional timestamp format.\n# To enable high precision timestamps, comment out the following line.\n#\n$ActionFileDefaultTemplate RSYSLOG_TraditionalFileFormat\n\n#\n# Set the default permissions for all log files.\n#\n$FileOwner root\n$FileGroup adm\n$FileCreateMode 0640\n$DirCreateMode 0755\n$Umask 0022\n\n#\n# Where to place spool and state files\n#\n$WorkDirectory /var/spool/rsyslog\n\n#\n# Include all config files in /etc/rsyslog.d/\n#\n$IncludeConfig /etc/rsyslog.d/*.conf\n\n\n###############\n#### RULES ####\n###############\n\n#RULE_STR#\n\n#\n# First some standard log files.  Log by facility.\n#\n#auth,authpriv.*\t\t\t/var/log/auth.log\n#*.*;auth,authpriv.none\t\t-/var/log/syslog\n#cron.*\t\t\t\t/var/log/cron.log\n#daemon.*\t\t\t-/var/log/daemon.log\n#kern.*\t\t\t\t-/var/log/kern.log\n#lpr.*\t\t\t\t-/var/log/lpr.log\n#mail.*\t\t\t\t-/var/log/mail.log\n#user.*\t\t\t\t-/var/log/user.log\n\n#\n# Logging for the mail system.  Split it up so that\n# it is easy to write scripts to parse these files.\n#\n#mail.info\t\t\t-/var/log/mail.info\n#mail.warn\t\t\t-/var/log/mail.warn\n#mail.err\t\t\t/var/log/mail.err\n\n#\n# Logging for INN news system.\n#\n#news.crit\t\t\t/var/log/news/news.crit\n#news.err\t\t\t/var/log/news/news.err\n#news.notice\t\t\t-/var/log/news/news.notice\n\n#\n# Some \"catch-all\" log files.\n#\n*.=debug;\\\n\tauth,authpriv.none;\\\n\tnews.none;mail.none\t-/var/log/debug\n*.=info;*.=notice;*.=warn;\\\n\tauth,authpriv.none;\\\n\tcron,daemon.none;\\\n\tmail,news.none\t\t-/var/log/messages\n\n#\n# Emergencies are sent to everybody logged in.\n#\n*.emerg\t\t\t\t:omusrmsg:*\n\n#\n# I like to have messages displayed on the console, but only on a virtual\n# console I usually leave idle.\n#\n#daemon,mail.*;\\\n#\tnews.=crit;news.=err;news.=notice;\\\n#\t*.=debug;*.=info;\\\n#\t*.=notice;*.=warn\t/dev/tty8\n\n# The named pipe /dev/xconsole is for the `xconsole' utility.  To use it,\n# you must invoke `xconsole' with the `-file' option:\n# \n#    $ xconsole -file /dev/xconsole [...]\n#\n# NOTE: adjust the list below, or you'll go crazy if you have a reasonably\n#      busy site..\n#\ndaemon.*;mail.*;\\\n\tnews.err;\\\n\t*.=debug;*.=info;\\\n\t*.=notice;*.=warn\t|/dev/xconsole";
	
	private ICommandResultFactory resultFactory;
	private IPluginInfo pluginInfo;
	private IOperationLogService logService;
	private IPluginDbService pluginDbService;

	@Override
	public ICommandResult execute(ICommandContext context) {
		
		ITaskRequest req = context.getRequest();
		Map<String, Object> parameterMap = req.getParameterMap();
		
//		parameterMap.get("items");
		
//		StringBuilder rsyslogConfTmp = new StringBuilder();
//		StringBuilder remoteConfTmp = new StringBuilder();
		// TODO foreach item
//		for (RsyslogLogFileSettings log : profile.getSettings()) {
//
//			// This log file will be handled by rsyslog locally.
//			if (log.isLocal()) {
//				// Append its name and path to '/etc/rsyslog.conf' file.
//				rsyslogConfTmp.append(log.getLogName()).append("\t").append(log.getLogPath()).append("\n");
//			}
//			// This log file will be handled by rsyslog remotely.
//			// That means the log file will be backed up on a remote
//			// machine (periodically or continuously).
//			else {
//				// Backup the file continuously (as soon as something
//				// writes to it).
//					// Append its name and server address,port to
//					// '/etc/rsyslog.d/remote.conf' file.
//					remoteConfTmp.append(log.getLogName()).append(" ").append(getProtocol())
//							.append(profile.getAddress()).append(":").append(profile.getPort()).append("\n");
//
//			}
//		}
//		Finalise configuration files
//		rsyslogConf = DEFAUL_CONFIG.replace("#RULE_STR#", rsyslogConfTmp.toString());
//		remoteConf = remoteConfTmp.toString();
		
		// TODO rsyslogConf & remoteConf parameterMap'e ekle
		
		ICommandResult commandResult = resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this);
		return commandResult;
	}
	
//	private String getProtocol() {
//		String protocol = profile.getProtocol();
//		if ("TCP".equalsIgnoreCase(protocol)) {
//			return "@@";
//		} else if ("UDP".equalsIgnoreCase(protocol)) {
//			return "@";
//		} else if ("RELP".equalsIgnoreCase(protocol)) {
//			return ":omrelp:";
//		} else {
//			throw new InvalidParameterException(
//					"Protocol value is invalid. Possible values are: { 'TCP', 'UDP', 'RELP' }");
//		}
//	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this, null);
	}

	@Override
	public String getCommandId() {
		return "CONFIGURE_RSYSLOG";
	}

	@Override
	public Boolean executeOnAgent() {
		return true;
	}
	
	@Override
	public String getPluginName() {
		return pluginInfo.getPluginName();
	}

	@Override
	public String getPluginVersion() {
		return pluginInfo.getPluginVersion();
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}
	
	public void setPluginInfo(IPluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public void setLogService(IOperationLogService logService) {
		this.logService = logService;
	}

	public void setPluginDbService(IPluginDbService pluginDbService) {
		this.pluginDbService = pluginDbService;
	}
	
}
