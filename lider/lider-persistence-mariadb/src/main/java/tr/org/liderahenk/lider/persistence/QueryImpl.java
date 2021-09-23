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
package tr.org.liderahenk.lider.persistence;

import tr.org.liderahenk.lider.core.api.persistence.IQuery;

/**
 * Default implementation for {@link IQuery}. This class can be used to build
 * complex queries.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>.
 *
 */
public class QueryImpl implements IQuery {
	
	// TODO use IQuery to build complex queries
	// TODO

	private int offset;
	private int maxResults = 100;
	private QueryCriteriaImpl[] criteria;

	@Override
	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public int getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	@Override
	public QueryCriteriaImpl[] getCriteria() {
		return criteria;
	}

	public void setCriteria(QueryCriteriaImpl[] criteria) {
		this.criteria = criteria;
	}

}
