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
package tr.org.liderahenk.lider.core.api.persistence;

import tr.org.liderahenk.lider.core.api.persistence.enums.OrderType;

/**
 * Provides (ascending or descending) ordering on properties.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 * @see tr.org.liderahenk.lider.core.api.persistence.IPluginDbService
 *
 */
public class PropertyOrder {

	final private String propertyName;
	final private OrderType orderType;

	public PropertyOrder(String propertyName, OrderType orderType) {
		super();
		this.propertyName = propertyName;
		this.orderType = orderType;
	}

	public PropertyOrder(String propertyName) {
		super();
		this.propertyName = propertyName;
		this.orderType = OrderType.ASC;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public OrderType getOrderType() {
		return orderType;
	}
}
