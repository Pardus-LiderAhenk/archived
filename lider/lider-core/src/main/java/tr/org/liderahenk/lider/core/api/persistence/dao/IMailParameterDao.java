package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.IBaseDao;
import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailAddress;
import tr.org.liderahenk.lider.core.api.persistence.entities.IMailParameter;

public interface IMailParameterDao extends IBaseDao<IMailParameter> {
	
	
	IMailParameter save(IMailParameter mailParameter);

	/**
	 * 
	 * @param profile
	 * @return
	 */
	IMailParameter update(IMailParameter mailAddress);

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
	IMailParameter find(Long id);

	/**
	 * 
	 * @return
	 */
	List<? extends IMailParameter> findAll(Class<? extends IMailParameter> obj, Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IMailParameter> findByProperty(Class<? extends IMailParameter> obj, String propertyName, Object propertyValue,
			Integer maxResults);

	/**
	 * 
	 * @return
	 */
	List<? extends IMailParameter> findByProperties(Class<? extends IMailParameter> obj, Map<String, Object> propertiesMap,
			List<PropertyOrder> orders, Integer maxResults);

}
