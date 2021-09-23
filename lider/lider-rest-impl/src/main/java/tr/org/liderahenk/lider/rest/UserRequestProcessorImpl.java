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
package tr.org.liderahenk.lider.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.configuration.IConfigurationService;
import tr.org.liderahenk.lider.core.api.messaging.IMessagingService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IAgentDao;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IUserRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;
import tr.org.liderahenk.lider.rest.dto.UserAgent;

/**
 * Processor class for handling/processing agent data.
 * 
 * @author edip.yildiz@hotmail.com
 *
 */
public class UserRequestProcessorImpl implements IUserRequestProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(UserRequestProcessorImpl.class);

	
	private IAgentDao agentDao;
	private IResponseFactory responseFactory;
	
	private IMessagingService messagingService;
	private IConfigurationService configService;

	@Override
	public IRestResponse listAgents(String userName) {
		
		
		List<Object[]> resultList=agentDao.findAgentFromOnlineUsers(userName);
		List<UserAgent> agentList=null;
		
		if (resultList != null) {
			
			 agentList= new ArrayList<>();
			
			for (Object[] arr : resultList) {
				if (arr.length != 8) {
					continue;
				}
//				[351, pardus, '10.0.2.15', '192.168.56.103', cn=istemci1,ou=Ahenkler,dc=mys,dc=pardus,dc=org, pardus, 1503303768000]
				UserAgent agent= new UserAgent();
				agent.setAgentId((Long) arr[0]);
				agent.setJid((String) arr[1]);
				agent.setHostname((String) arr[2]);
				agent.setIp((String) arr[3]);
				agent.setAgentDn((String) arr[4]);
				agent.setUserName((String) arr[5]);
				agent.setUserLoginDate((Date) arr[6]);
				agent.setUserIp((String) arr[7]);
				
				agent.setIsOnline(messagingService.isRecipientOnline(getFullJid(agent.getJid())));
				
				agentList.add(agent);
			}
			
		}
		
		// Construct result map
				Map<String, Object> resultMap = new HashMap<String, Object>();
				try {
					resultMap.put("userAgents", agentList);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
		
		return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	public IAgentDao getAgentDao() {
		return agentDao;
	}

	public void setAgentDao(IAgentDao agentDao) {
		this.agentDao = agentDao;
	}

	public IResponseFactory getResponseFactory() {
		return responseFactory;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public IMessagingService getMessagingService() {
		return messagingService;
	}

	public void setMessagingService(IMessagingService messagingService) {
		this.messagingService = messagingService;
	}
	
	
	public String getFullJid(String jid) {
		String jidFinal = jid;
		if (jid.indexOf("@") < 0) {
			jidFinal = jid + "@" + configService.getXmppServiceName();
		}
		return jidFinal;
	}

	public IConfigurationService getConfigService() {
		return configService;
	}

	public void setConfigService(IConfigurationService configService) {
		this.configService = configService;
	}

}
