package tr.org.liderahenk.lider.core.api.persistence.dao;

import java.util.List;
import java.util.Map;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.entities.IRegistrationTemplate;

public interface IRegistrationDao {

	IRegistrationTemplate save(IRegistrationTemplate template);

	IRegistrationTemplate update(IRegistrationTemplate template);

	void delete(Long id);

	IRegistrationTemplate find(Long id);

	List<? extends IRegistrationTemplate> findAll();

	List<? extends IRegistrationTemplate> findByProperty(Class<? extends IRegistrationTemplate> obj,
			String propertyName, Object propertyValue, Integer maxResults);

	List<? extends IRegistrationTemplate> findByProperties(Class<? extends IRegistrationTemplate> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults);

}