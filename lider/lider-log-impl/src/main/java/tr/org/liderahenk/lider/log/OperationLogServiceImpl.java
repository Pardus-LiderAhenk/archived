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
package tr.org.liderahenk.lider.log;

import tr.org.liderahenk.lider.core.api.log.IOperationLogService;
import tr.org.liderahenk.lider.core.api.persistence.dao.IOperationLogDao;
import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;
import tr.org.liderahenk.lider.core.api.persistence.factories.IEntityFactory;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class OperationLogServiceImpl implements IOperationLogService {

	private IOperationLogDao logDao;
	private IEntityFactory entityFactory;

	@Override
	public IOperationLog saveLog(String userId, CrudType crudType, String message, byte[] requestData, String requestIp)
			throws Exception {
		IOperationLog log = entityFactory.createLog(userId, crudType, null, null, null, message, requestData,
				requestIp);
		log = logDao.save(log);
		return log;
	}

	@Override
	public IOperationLog saveTaskLog(String userId, CrudType crudType, Long taskId, String message, byte[] requestData,
			String requestIp) throws Exception {
		IOperationLog log = entityFactory.createLog(userId, crudType, taskId, null, null, message, requestData,
				requestIp);
		log = logDao.save(log);
		return log;
	}

	@Override
	public IOperationLog savePolicyLog(String userId, CrudType crudType, Long policyId, String message,
			byte[] requestData, String requestIp) throws Exception {
		IOperationLog log = entityFactory.createLog(userId, crudType, null, policyId, null, message, requestData,
				requestIp);
		log = logDao.save(log);
		return log;
	}

	@Override
	public IOperationLog saveProfileLog(String userId, CrudType crudType, Long profileId, String message,
			byte[] requestData, String requestIp) throws Exception {
		IOperationLog log = entityFactory.createLog(userId, crudType, null, null, profileId, message, requestData,
				requestIp);
		log = logDao.save(log);
		return log;
	}

	/*
	 * Service setters
	 */

	/**
	 * 
	 * @param logDao
	 */
	public void setLogDao(IOperationLogDao logDao) {
		this.logDao = logDao;
	}

	/**
	 * 
	 * @param entityFactory
	 */
	public void setEntityFactory(IEntityFactory entityFactory) {
		this.entityFactory = entityFactory;
	}

}
