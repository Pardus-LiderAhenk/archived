package tr.org.liderahenk.lider.persistence.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IRegistrationDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IRegistrationTemplate;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.persistence.entities.RegistrationTemplateImpl;

@SuppressWarnings("unchecked")
public class RegistrationDaoImpl implements IRegistrationDao {

	
	private static Logger logger = LoggerFactory.getLogger(RegistrationDaoImpl.class);
	
	private EntityManager entityManager;

	
	public void init() {
		logger.info("Initializing Registration DAO.");
	}

	public void destroy() {
		logger.info("Destroying Registration DAO.");
	}
	
	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.persistence.dao.IRegistrationDao#save(tr.org.liderahenk.lider.core.api.persistence.entities.IRegistrationTemplate)
	 */
	@Override
	public IRegistrationTemplate save(IRegistrationTemplate template) {
		RegistrationTemplateImpl impl = new RegistrationTemplateImpl((IRegistrationTemplate) template);
		entityManager.persist(impl);
		logger.debug("IRegistrationTemplate object persisted: {}", impl.toString());
		return impl;
	}
	
	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.persistence.dao.IRegistrationDao#update(tr.org.liderahenk.lider.core.api.persistence.entities.IRegistrationTemplate)
	 */
	@Override
	public RegistrationTemplateImpl update(IRegistrationTemplate template) {
		RegistrationTemplateImpl impl = new RegistrationTemplateImpl(template);
		impl = entityManager.merge(impl);
		logger.debug("IRegistrationTemplate object merged: {}", impl.toString());
		return impl;
	}
	
	
	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.persistence.dao.IRegistrationDao#delete(java.lang.Long)
	 */
	@Override
	public void delete(Long id) {
		RegistrationTemplateImpl impl = entityManager.find(RegistrationTemplateImpl.class, id);
		entityManager.remove(impl);
		logger.debug("RegistrationTemplateImpl object marked as deleted");
	}
	
	
	
	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.persistence.dao.IRegistrationDao#find(java.lang.Long)
	 */
	@Override
	public RegistrationTemplateImpl find(Long id) {
		RegistrationTemplateImpl impl = entityManager.find(RegistrationTemplateImpl.class, id);
		logger.debug("IRegistrationTemplate object found: {}", impl.toString());
		return impl;
	}
	
	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.persistence.dao.IRegistrationDao#findAll(java.lang.Class, java.lang.Integer)
	 */
	@Override
	public List<? extends IRegistrationTemplate> findAll() {
		List<RegistrationTemplateImpl> implList = entityManager
				.createQuery("select t from " + RegistrationTemplateImpl.class.getSimpleName() + " t", RegistrationTemplateImpl.class)
				.getResultList();
		logger.debug("IRegistrationTemplate objects found: {}", implList);
		return implList;
	}

	
	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.persistence.dao.IRegistrationDao#findByProperty(java.lang.Class, java.lang.String, java.lang.Object, java.lang.Integer)
	 */
	@Override
	public List<? extends IRegistrationTemplate> findByProperty(Class<? extends IRegistrationTemplate> obj, String propertyName,
			Object propertyValue, Integer maxResults) {
		
		
		TypedQuery<RegistrationTemplateImpl> query = entityManager.createQuery("select t from " + RegistrationTemplateImpl.class.getSimpleName()
				+ " t where t." + propertyName + "= :propertyValue" , RegistrationTemplateImpl.class)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query = query.setMaxResults(maxResults);
		}
		List<RegistrationTemplateImpl> list = query.getResultList();

		logger.debug("RegistrationTemplateImpl objects found: {}", list);
		return list;
		
		
	}

	/* (non-Javadoc)
	 * @see tr.org.liderahenk.lider.persistence.dao.IRegistrationDao#findByProperties(java.lang.Class, java.util.Map, java.util.List, java.lang.Integer)
	 */
	@Override
	public List<? extends IRegistrationTemplate> findByProperties(Class<? extends IRegistrationTemplate> obj,
			Map<String, Object> propertiesMap, List<PropertyOrder> orders, Integer maxResults) {
		
		orders = new ArrayList<PropertyOrder>();
		
		// PropertyOrder ord = new PropertyOrder("name", OrderType.ASC);
		// orders.add(ord);
		
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		
		CriteriaQuery<RegistrationTemplateImpl> criteria = (CriteriaQuery<RegistrationTemplateImpl>) builder.createQuery(RegistrationTemplateImpl.class);
		
		Root<RegistrationTemplateImpl> from = (Root<RegistrationTemplateImpl>) criteria.from(RegistrationTemplateImpl.class);
		
		criteria.select(from);
		
		Predicate predicate = null;

		if (propertiesMap != null) {
			Predicate pred = null;
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					String[] key = entry.getKey() != null ? entry.getKey().split("\\.") : null;
					if (key != null && key.length > 1) {
						Join<Object, Object> join = null;
						for (int i = 0; i < key.length - 1; i++) {
							join = join != null ? join.join(key[i]) : from.join(key[i]);
						}
						pred = builder.equal(join.get(key[key.length - 1]), entry.getValue());
					} else {
						pred = builder.equal(from.get(entry.getKey()), entry.getValue());
					}
					predicate = predicate == null ? pred : builder.and(predicate, pred);
				}
			}
			if (predicate != null) {
				criteria.where(predicate);
			}
		}

		if (orders != null && !orders.isEmpty()) {
			List<Order> orderList = new ArrayList<Order>();
			for (PropertyOrder order : orders) {
				orderList.add(order.getOrderType() == OrderType.ASC ? builder.asc(from.get(order.getPropertyName()))
						: builder.desc(from.get(order.getPropertyName())));
			}
			criteria.orderBy(orderList);
		}

		List<RegistrationTemplateImpl> list = null;
		
		
		if (null != maxResults) {
			list = entityManager.createQuery(criteria).setMaxResults(maxResults).getResultList();
		} else {
			list = entityManager.createQuery(criteria).getResultList();
		}
		
		return list;

	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	
	
	
	
}
