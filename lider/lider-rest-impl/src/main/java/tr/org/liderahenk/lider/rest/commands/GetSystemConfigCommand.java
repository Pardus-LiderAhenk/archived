/*
*
*    Copyright © 2015-2016 Tübitak ULAKBIM
*
*    This file is part of Lider Ahenk.
*
*    Lider Ahenk is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    Lider Ahenk is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with Lider Ahenk.  If not, see <http://www.gnu.org/licenses/>.
*/
package tr.org.liderahenk.lider.rest.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.service.ICommandContext;
import tr.org.liderahenk.lider.core.api.service.ICommandResult;
import tr.org.liderahenk.lider.core.api.service.ICommandResultFactory;
import tr.org.liderahenk.lider.core.api.service.enums.CommandResultStatus;

/**
 * This ICommand implementation provides system configuration (such as LDAP
 * connection parameters and XMPP connection parameters) to Lider Console.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class GetSystemConfigCommand extends BaseCommand {

	private static Logger logger = LoggerFactory.getLogger(GetSystemConfigCommand.class);

	private ICommandResultFactory resultFactory;
	private IConfigurationService configurationService;

	@Override
	public ICommandResult execute(ICommandContext context) throws Exception {

		Map<String, Object> resultMap = new HashMap<String, Object>();
		// XMPP configuration
		resultMap.put("xmppHost", configurationService.getXmppHost());
		resultMap.put("xmppPort", configurationService.getXmppPort() + "");
		resultMap.put("xmppServiceName", configurationService.getXmppServiceName());
		resultMap.put("xmppMaxRetryConnectionCount", configurationService.getXmppMaxRetryConnectionCount() + "");
		resultMap.put("xmppPacketReplayTimeout", configurationService.getXmppPacketReplayTimeout() + "");
		resultMap.put("xmppPingTimeout", configurationService.getXmppPingTimeout() + "");
		resultMap.put("xmppUseSsl", configurationService.getXmppUseSsl());
		resultMap.put("xmppUsername", "TODO"); // TODO
		resultMap.put("xmppPassword", "TODO"); // TODO
		// LDAP configuration
		resultMap.put("ldapServer", configurationService.getLdapServer());
		resultMap.put("ldapPort", configurationService.getLdapPort());
		resultMap.put("ldapUsername", "TODO"); // TODO
		resultMap.put("ldapPassword", "TODO"); // TODO
		resultMap.put("ldapRootDn", configurationService.getLdapRootDn());
		resultMap.put("ldapUseSsl", configurationService.getLdapUseSsl());
		logger.debug("System config: {}", resultMap);

		return resultFactory.create(CommandResultStatus.OK, new ArrayList<String>(), this, resultMap);
	}

	@Override
	public ICommandResult validate(ICommandContext context) {
		return resultFactory.create(CommandResultStatus.OK, null, this);
	}

	@Override
	public String getCommandId() {
		return "GET-SYSTEM-CONFIG";
	}

	@Override
	public Boolean executeOnAgent() {
		return false;
	}

	public void setResultFactory(ICommandResultFactory resultFactory) {
		this.resultFactory = resultFactory;
	}

	public void setConfigurationService(IConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
