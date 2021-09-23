package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailContent;

public interface IMailContentDao extends IBaseDao<IMailContent> {
	
	
	IMailContent save(IMailContent mailContent);

	/**
	 * 
	 * @param profile
	 * @return
	 */
	IMailContent update(IMailContent mailContent);

	/**
	 * 
	 * @param profileId
	 */
	void delete(Long id);

	/**
	 * 
	 * @param profileId
	 * @return
	 */
	IMailContent find(Long id);

	/**
	 * 
	 * @return
	 */
	List<? extends IMailContent> findAll(Class<? extends IMailContent> obj, Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IMailContent> findByProperty(Class<? extends IMailContent> obj, String propertyName, Object propertyValue,
			Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IMailContent> findByProperties(Class<? extends IMailContent> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

}
