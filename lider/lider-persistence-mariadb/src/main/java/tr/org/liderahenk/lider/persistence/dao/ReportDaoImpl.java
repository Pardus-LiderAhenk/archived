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
package tr.org.liderahenk.lider.persistence.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.lider.core.api.persistence.PropertyOrder;
import tr.org.liderahenk.lider.core.api.persistence.dao.IReportDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IEntity;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplate;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateColumn;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportTemplateParameter;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewParameter;
import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;
import tr.org.liderahenk.lider.core.api.persistence.enums.ParameterType;
import tr.org.liderahenk.lider.core.api.utils.LiderCoreUtils;
import tr.org.liderahenk.lider.core.api.utils.RandomStringGenerator;
import tr.org.liderahenk.lider.persistence.entities.ReportTemplateColumnImpl;
import tr.org.liderahenk.lider.persistence.entities.ReportTemplateImpl;
import tr.org.liderahenk.lider.persistence.entities.ReportTemplateParameterImpl;
import tr.org.liderahenk.lider.persistence.entities.ReportViewImpl;

/**
 * Provides database access for reports and report templates. CRUD operations
 * for reports and their related records should be handled via this service
 * only.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ReportDaoImpl implements IReportDao {

	private static Logger logger = LoggerFactory.getLogger(ReportDaoImpl.class);

	private EntityManager entityManager;
	private static final Random rand = new Random();

	public void init() {
		logger.info("Initializing report DAO.");
	}

	public void destroy() {
		logger.info("Destroying report DAO.");
	}

	@Override
	public IReportTemplate saveTemplate(IReportTemplate template) {
		ReportTemplateImpl templateImpl = new ReportTemplateImpl(template);
		templateImpl.setCreateDate(new Date());
		templateImpl.setModifyDate(null);
		return (IReportTemplate) save(ReportTemplateImpl.class, templateImpl);
	}

	@Override
	public IReportView saveView(IReportView view) {
		ReportViewImpl viewImpl = new ReportViewImpl(view);
		viewImpl.setCreateDate(new Date());
		viewImpl.setModifyDate(null);
		return (IReportView) save(ReportViewImpl.class, viewImpl);
	}

	private IEntity save(Class cls, IEntity entity) {
		entityManager.persist(cls.cast(entity));
		logger.debug("{} object persisted: {}", new Object[] { cls.getSimpleName(), entity.toString() });
		return entity;
	}

	@Override
	public IReportTemplate updateTemplate(IReportTemplate template) {
		ReportTemplateImpl templateImpl = new ReportTemplateImpl(template);
		templateImpl.setModifyDate(new Date());
		return (IReportTemplate) update(ReportTemplateImpl.class, templateImpl);
	}

	@Override
	public IReportView updateView(IReportView view) {
		ReportViewImpl viewImpl = new ReportViewImpl(view);
		viewImpl.setModifyDate(new Date());
		return (IReportView) update(ReportViewImpl.class, viewImpl);
	}

	private IEntity update(Class cls, IEntity entity) {
		IEntity e = (IEntity) entityManager.merge(cls.cast(entity));
		logger.debug("{} object merged: {}", new Object[] { cls.getSimpleName(), entity.toString() });
		return e;
	}

	@Override
	public void deleteTemplate(Long id) {
		delete(ReportTemplateImpl.class, id);
	}

	@Override
	public void deleteView(Long id) {
		delete(ReportViewImpl.class, id);
	}

	private void delete(Class cls, Long id) {
		Object object = entityManager.find(cls, id);
		entityManager.remove(object);
		logger.debug("{} object deleted: {}", new Object[] { cls.getSimpleName(), id });
	}

	@Override
	public ReportTemplateImpl findTemplate(Long id) {
		return (ReportTemplateImpl) find(ReportTemplateImpl.class, id);
	}

	@Override
	public ReportViewImpl findView(Long id) {
		return (ReportViewImpl) find(ReportViewImpl.class, id);
	}

	@Override
	public IReportTemplateColumn findTemplateColumn(Long id) {
		return (IReportTemplateColumn) find(ReportTemplateColumnImpl.class, id);
	}

	@Override
	public IReportTemplateParameter findTemplateParameter(Long id) {
		return (IReportTemplateParameter) find(ReportTemplateParameterImpl.class, id);
	}

	private IEntity find(Class cls, Long id) {
		Object entity = entityManager.find(cls, id);
		logger.debug("{} object found: {}",
				new Object[] { cls.getSimpleName(), entity != null ? entity.toString() : "null" });
		return (IEntity) entity;
	}

	@Override
	public List<? extends IReportTemplate> findTemplates(Integer maxResults) {
		return (List<? extends IReportTemplate>) findAll(ReportTemplateImpl.class, maxResults);
	}

	@Override
	public List<? extends IReportView> findViews(Integer maxResults) {
		return (List<? extends IReportView>) findAll(ReportViewImpl.class, maxResults);
	}

	private List<?> findAll(Class cls, Integer maxResults) {
		TypedQuery query = entityManager.createQuery("select t from " + cls.getSimpleName() + " t", cls);
		if (maxResults != null) {
			query.setMaxResults(maxResults.intValue());
		}
		List list = query.getResultList();
		logger.debug("{} objects found: {}", new Object[] { cls.getSimpleName(), list });
		return list;
	}

	@Override
	public List<? extends IReportTemplate> findTemplates(String propertyName, Object propertyValue,
			Integer maxResults) {
		return (List<? extends IReportTemplate>) findByProperty(ReportTemplateImpl.class, propertyName, propertyValue,
				maxResults);
	}

	@Override
	public List<? extends IReportView> findViews(String propertyName, Object propertyValue, Integer maxResults) {
		return (List<? extends IReportView>) findByProperty(ReportViewImpl.class, propertyName, propertyValue,
				maxResults);
	}

	private List<?> findByProperty(Class cls, String propertyName, Object propertyValue, Integer maxResults) {
		TypedQuery query = entityManager
				.createQuery("select t from " + cls.getSimpleName() + " t where t." + propertyName + "= :propertyValue",
						cls)
				.setParameter("propertyValue", propertyValue);
		if (maxResults > 0) {
			query.setMaxResults(maxResults);
		}
		List list = query.getResultList();
		logger.debug("{} objects found: {}", new Object[] { cls.getSimpleName(), list });
		return list;
	}

	@Override
	public List<? extends IReportTemplate> findTemplates(Map<String, Object> propertiesMap, List<PropertyOrder> orders,
			Integer maxResults) {
		return (List<? extends IReportTemplate>) findByProperties(ReportTemplateImpl.class, propertiesMap, orders,
				maxResults);
	}

	@Override
	public List<? extends IReportView> findViews(Map<String, Object> propertiesMap, List<PropertyOrder> orders,
			Integer maxResults) {
		return (List<? extends IReportView>) findByProperties(ReportViewImpl.class, propertiesMap, orders, maxResults);
	}

	private List<?> findByProperties(Class cls, Map<String, Object> propertiesMap, List<PropertyOrder> orders,
			Integer maxResults) {
		orders = new ArrayList<PropertyOrder>();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery criteria = builder.createQuery(cls);
		Metamodel metamodel = entityManager.getMetamodel();
		EntityType entityType = metamodel.entity(cls);
		Root from = (Root) criteria.from(entityType);
		criteria.select(from);
		Predicate predicate = null;

		if (propertiesMap != null) {
			Predicate pred = null;
			for (Entry<String, Object> entry : propertiesMap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().toString().isEmpty()) {
					String[] key = entry.getKey().split("\\.");
					if (key.length > 1) {
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

		TypedQuery query = entityManager.createQuery(criteria);
		if (maxResults != null) {
			query.setMaxResults(maxResults.intValue());
		}
		List list = query.getResultList();
		logger.debug("{} objects found: {}", new Object[] { cls.getSimpleName(), list });
		return list;
	}

	@Override
	public void validateTemplate(String query, Set<? extends IReportTemplateParameter> params) throws Exception {
		Query q = entityManager.createQuery(query);
		// Set query parameter with random values!
		if (params != null) {
			for (IReportTemplateParameter param : params) {
				if (LiderCoreUtils.isInteger(param.getKey())) {
					if (param.getType() == ParameterType.DATE) {
						q.setParameter(Integer.parseInt(param.getKey()), new Date(), TemporalType.DATE);
					} else if (param.getType() == ParameterType.NUMBER) {
						q.setParameter(Integer.parseInt(param.getKey()), rand.nextInt());
					} else {
						q.setParameter(Integer.parseInt(param.getKey()), new RandomStringGenerator(10).nextString());
					}
				} else {
					if (param.getType() == ParameterType.DATE) {
						q.setParameter(param.getKey(), new Date(), TemporalType.DATE);
					} else if (param.getType() == ParameterType.NUMBER) {
						q.setParameter(param.getKey(), rand.nextInt());
					} else {
						q.setParameter(param.getKey(), new RandomStringGenerator(10).nextString());
					}
				}
			}
		}
		// If query executes, we can assume it is valid!
		q.getResultList();
	}

	@Override
	public List<Object[]> generateView(IReportView view, Map<String, Object> values) throws Exception {
		Query q = entityManager.createQuery(view.getTemplate().getQuery());
		// Set query parameters!
		if (view.getTemplate().getTemplateParams() != null) {
			for (IReportTemplateParameter param : view.getTemplate().getTemplateParams()) {
				if (LiderCoreUtils.isInteger(param.getKey())) {
					if (param.getType() == ParameterType.DATE) {
						// TODO date pattern
						q.setParameter(Integer.parseInt(param.getKey()), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.parse(findParameterValue(param, view.getViewParams(), values)), TemporalType.DATE);
					} else if (param.getType() == ParameterType.NUMBER) {
						q.setParameter(Integer.parseInt(param.getKey()),
								findParameterValue(param, view.getViewParams(), values));
					} else {
						q.setParameter(Integer.parseInt(param.getKey()),
								findParameterValue(param, view.getViewParams(), values));
					}
				} else {
					if (param.getType() == ParameterType.DATE) {
						// TODO date pattern
						q.setParameter(param.getKey(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.parse(findParameterValue(param, view.getViewParams(), values)), TemporalType.DATE);
					} else if (param.getType() == ParameterType.NUMBER) {
						q.setParameter(param.getKey(), findParameterValue(param, view.getViewParams(), values));
					} else {
						q.setParameter(param.getKey(), findParameterValue(param, view.getViewParams(), values));
					}
				}
			}
		}
		// Execute query
		List<Object[]> resultList = q.getResultList();
		return resultList;
	}

	/**
	 * 
	 * @param tParam
	 * @param vParams
	 * @param values
	 * @return
	 */
	private String findParameterValue(IReportTemplateParameter tParam, Set<? extends IReportViewParameter> vParams,
			Map<String, Object> values) {
		// Use the value provided by the request!
		if (values != null && values.get(tParam.getKey()) != null) {
			return values.get(tParam.getKey()).toString();
		}
		// Try to find view parameter which references to the current tParam
		IReportViewParameter vParam = null;
		for (IReportViewParameter tmp : vParams) {
			if (tmp.getReferencedParam().getId().equals(tParam)) {
				vParam = tmp;
				break;
			}
		}
		// Found the view parameter, return its value!
		if (vParam != null && vParam.getValue() != null) {
			return vParam.getValue();
		}
		// No value has been provided, use the default value!
		return tParam.getDefaultValue();
	}

	private static final String FIND_VIEWS_WITH_ALARM = 
			"SELECT v " 
			+ "FROM ReportViewImpl v "
			+ "WHERE v.alarmMail IS NOT NULL " 
			+ "ORDER BY v.createDate, v.name";

	@Override
	public List<? extends IReportView> findViewsWithAlarm() {
		Query query = entityManager.createQuery(FIND_VIEWS_WITH_ALARM);
		List resultList = query.getResultList();
		return resultList;
	}
	
	private static final String RESET_ALARM = 
			"UPDATE ReportViewImpl as t SET "
			+ "t.alarmCheckPeriod = NULL, "
			+ "t.alarmRecordNumThreshold = NULL, "
			+ "t.alarmMail = NULL "
			+ "WHERE t.id = :id";

	@Override
	public void resetAlarmFields(IReportView view) {
		ReportViewImpl v = (ReportViewImpl) view;
		v.setAlarmCheckPeriod(null);
		v.setAlarmMail(null);
		v.setAlarmRecordNumThreshold(null);
		Query query = entityManager.createQuery(RESET_ALARM);
		query.setParameter("id", view.getId());
		query.executeUpdate();
	}

	/**
	 * 
	 * @param entityManager
	 */
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
