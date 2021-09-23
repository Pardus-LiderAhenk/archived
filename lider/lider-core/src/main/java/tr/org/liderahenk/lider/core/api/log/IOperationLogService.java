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
package tr.org.liderahenk.lider.core.api.log;

import tr.org.liderahenk.lider.core.api.persistence.entities.IOperationLog;
import tr.org.liderahenk.lider.core.api.persistence.enums.CrudType;

public interface IOperationLogService {

	/**
	 * 
	 * @param userId
	 * @param crudType
	 * @param message
	 * @param requestData
	 * @param requestIp
	 * @return
	 * @throws Exception
	 */
	IOperationLog saveLog(String userId, CrudType crudType, String message, byte[] requestData, String requestIp)
			throws Exception;

	/**
	 * 
	 * @param userId
	 * @param crudType
	 * @param taskId
	 * @param message
	 * @param requestData
	 * @param requestIp
	 * @return
	 * @throws Exception
	 */
	IOperationLog saveTaskLog(String userId, CrudType crudType, Long taskId, String message, byte[] requestData,
			String requestIp) throws Exception;

	/**
	 * 
	 * @param userId
	 * @param crudType
	 * @param policyId
	 * @param message
	 * @param requestData
	 * @param requestIp
	 * @return
	 * @throws Exception
	 */
	IOperationLog savePolicyLog(String userId, CrudType crudType, Long policyId, String message, byte[] requestData,
			String requestIp) throws Exception;

	/**
	 * 
	 * @param userId
	 * @param crudType
	 * @param profileId
	 * @param message
	 * @param requestData
	 * @param requestIp
	 * @return
	 * @throws Exception
	 */
	IOperationLog saveProfileLog(String userId, CrudType crudType, Long profileId, String message, byte[] requestData,
			String requestIp) throws Exception;

}
