package tr.org.liderahenk.lider.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.dao.IMailAddressDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IMailContentDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IMailParameterDao;
import tr.org.liderahenk.lider.core.api.persistence.dao.IPluginDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailAddress;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailContent;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailParameter;
import tr.org.liderahenk.lider.core.api.persistence.entities.IPlugin;
import tr.org.liderahenk.lider.core.api.persistence.entities.IProfile;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;
import tr.org.liderahenk.lider.core.api.rest.IRequestFactory;
import tr.org.liderahenk.lider.core.api.rest.IResponseFactory;
import tr.org.liderahenk.lider.core.api.rest.enums.RestResponseStatus;
import tr.org.liderahenk.lider.core.api.rest.processors.IMailManagementRequestProcessor;
import tr.org.liderahenk.lider.core.api.rest.requests.IMailManagementRequest;
import tr.org.liderahenk.lider.core.api.rest.requests.IProfileRequest;
import tr.org.liderahenk.lider.core.api.rest.responses.IRestResponse;

public class MailManagementRequestProcessorImpl implements IMailManagementRequestProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(MailManagementRequestProcessorImpl.class);
	
	private IRequestFactory requestFactory;
	
	private IEntityFactory entityFactory;
	
	private IResponseFactory responseFactory;

	private IPluginDao pluginDao;
	
	private IMailAddressDao mailAddressDao;
	
	private IMailContentDao mailContentDao;
	
	private IMailParameterDao mailParameterDao;

	@Override
	public IRestResponse add(String json) {
		
		
		try {
			IMailManagementRequest request = requestFactory.createMailManagementRequest(json);

			List<IMailAddress> mailAddressList= (List<IMailAddress>) request.getMailAddressList();
			
			IMailContent content= request.getMailContent();
			IPlugin plugin= content.getPlugin();
				
			
			plugin= pluginDao.find(plugin.getId());
			
			
			content= entityFactory.createMailContent(plugin,content); // plugin convert
			
			
			if(content!=null)
			mailContentDao.save(content);
			
			if(mailAddressList!=null && mailAddressList.size()>0){
				
				
				for (IMailAddress mailAddress : mailAddressList) {
					if(mailAddress.getId()==null)
					{
						mailAddress= entityFactory.createMailAddress(plugin,mailAddress);
						mailAddressDao.save(mailAddress);
					}
					if(mailAddress.isDeleted() && mailAddress.getId()!=null){
						mailAddress= entityFactory.createMailAddress(plugin,mailAddress);
						mailAddressDao.delete(mailAddress.getId());
					}
				}
			}

			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("request", request);

			return responseFactory.createResponse(RestResponseStatus.OK, "Record saved.", resultMap);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
		}
		
	}

	@Override
	public IRestResponse update(String requestBodyDecoded) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRestResponse list(String pluginName, String pluginVersion) {
		IPlugin plugin = findRelatedPlugin(pluginName, pluginVersion);
		
				
				List<? extends IMailAddress> mailAddressList = mailAddressDao.findByProperty(IMailAddress.class, "plugin.id", plugin.getId(), 0);
				
				List<? extends IMailContent> mailContentList = mailContentDao.findByProperty(IMailContent.class, "plugin.id", plugin.getId(), 0);
				
				List<? extends IMailParameter> mailParameterList = mailParameterDao.findByProperty(IMailParameter.class, "plugin.id", plugin.getId(), 0);
				
						
				logger.info("Found mail list: {}", mailAddressList);

				// Construct result map
				Map<String, Object> resultMap = new HashMap<String, Object>();
				try {
					resultMap.put("mailAddressList", mailAddressList);
					resultMap.put("mailContentList", mailContentList);
					resultMap.put("mailParameterList", mailParameterList);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
					return responseFactory.createResponse(RestResponseStatus.ERROR, e.getMessage());
				}

				return responseFactory.createResponse(RestResponseStatus.OK, "Records listed.", resultMap);
	}

	@Override
	public IRestResponse get(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IRestResponse delete(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private IPlugin findRelatedPlugin(String pluginName, String pluginVersion) {
		Map<String, Object> propertiesMap = new HashMap<String, Object>();
		propertiesMap.put("name", pluginName);
		propertiesMap.put("version", pluginVersion);
		List<? extends IPlugin> plugins = pluginDao.findByProperties(IPlugin.class, propertiesMap, null, 1);
		IPlugin plugin = plugins.get(0);
		return plugin;
	}

	public IRequestFactory getRequestFactory() {
		return requestFactory;
	}

	public void setRequestFactory(IRequestFactory requestFactory) {
		this.requestFactory = requestFactory;
	}

	public IResponseFactory getResponseFactory() {
		return responseFactory;
	}

	public void setResponseFactory(IResponseFactory responseFactory) {
		this.responseFactory = responseFactory;
	}

	public IPluginDao getPluginDao() {
		return pluginDao;
	}

	public void setPluginDao(IPluginDao pluginDao) {
		this.pluginDao = pluginDao;
	}

	public IMailAddressDao getMailManagementDao() {
		return mailAddressDao;
	}

	public void setMailManagementDao(IMailAddressDao mailManagementDao) {
		this.mailAddressDao = mailManagementDao;
	}

	public IMailAddressDao getMailAddressDao() {
		return mailAddressDao;
	}

	public void setMailAddressDao(IMailAddressDao mailAddressDao) {
		this.mailAddressDao = mailAddressDao;
	}

	public IMailContentDao getMailContentDao() {
		return mailContentDao;
	}

	public void setMailContentDao(IMailContentDao mailContentDao) {
		this.mailContentDao = mailContentDao;
	}

	public IMailParameterDao getMailParameterDao() {
		return mailParameterDao;
	}

	public void setMailParameterDao(IMailParameterDao mailParameterDao) {
		this.mailParameterDao = mailParameterDao;
	}

	public IEntityFactory getEntityFactory() {
		return entityFactory;
	}

	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

}
