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
package tr.org.liderahenk.lider.service.requests;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.lider.core.api.persistence.enums.ViewColumnType;
import tr.org.liderahenk.lider.core.api.rest.requests.IReportViewColumnRequest;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportViewColReqImpl implements IReportViewColumnRequest {

	private static final long serialVersionUID = 808547110927918705L;

	private Long id;

	private Long referencedColumnId;

	private ViewColumnType type;

	private String legend;

	private Integer width;

	private Date timestamp;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getReferencedColumnId() {
		return referencedColumnId;
	}

	public void setReferencedColumnId(Long referencedColumnId) {
		this.referencedColumnId = referencedColumnId;
	}

	@Override
	public ViewColumnType getType() {
		return type;
	}

	public void setType(ViewColumnType type) {
		this.type = type;
	}

	@Override
	public String getLegend() {
		return legend;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	@Override
	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
