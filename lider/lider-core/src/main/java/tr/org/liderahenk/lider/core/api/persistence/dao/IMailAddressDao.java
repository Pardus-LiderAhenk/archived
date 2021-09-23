package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailAddress;

public interface IMailAddressDao extends IBaseDao<IMailAddress> {
	
	
	IMailAddress save(IMailAddress mailAddress);

	/**
	 * 
	 * @param profile
	 * @return
	 */
	IMailAddress update(IMailAddress mailAddress);

	/**
	 * 
	 * @param profileId
	 */
	void delete(Long mailAddressId);

	/**
	 * 
	 * @param profileId
	 * @return
	 */
	IMailAddress find(Long mailAddressId);

	/**
	 * 
	 * @return
	 */
	List<? extends IMailAddress> findAll(Class<? extends IMailAddress> obj, Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IMailAddress> findByProperty(Class<? extends IMailAddress> obj, String propertyName, Object propertyValue,
			Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IMailAddress> findByProperties(Class<? extends IMailAddress> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

}
