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
package tr.org.liderahenk.lider.core.api.persistence.enums;

import tr.org.liderahenk.lider.core.api.persistence.IQueryCriteria;

/**
 * Provides criteria operators for {@link IQueryCriteria}
 *
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public enum CriteriaOperator {
	EQ, NE, BT, GT, GE, LT, LE, LIKE, NULL, NOT_NULL, IN, NOT_IN;

	@Override
	public String toString() {
		switch (this) {
		case EQ:
			return "=";
		case NE:
			return "!=";
		case GT:
			return ">";
		case GE:
			return ">=";
		case LT:
			return "<";
		case LE:
			return "<=";
		case BT:
			return "BETWEEN";
		case NOT_NULL:
			return "IS NOT NULL";
		case NULL:
			return "IS NULL";
		case IN:
			return "IN";
		case NOT_IN:
			return "NOT IN";
		case LIKE:
			return "LIKE";
		default:
			return null;
		}
	}

}
