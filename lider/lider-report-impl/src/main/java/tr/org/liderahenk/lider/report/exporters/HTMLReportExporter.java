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
package tr.org.liderahenk.lider.report.exporters;

import java.util.ArrayList;
import java.util.List;

import tr.org.liderahenk.lider.core.api.persistence.entities.IReportView;
import tr.org.liderahenk.lider.core.api.persistence.entities.IReportViewColumn;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class HTMLReportExporter {

	public static String export(IReportView view, List<Object[]> resultList) {
		int tableWidth = 0;
		String html = "<html><body><table border=\"1\" style=\"width:#TABLEWIDTH#px\"><tr>";

		// Table headers
		ArrayList<IReportViewColumn> columns = new ArrayList<IReportViewColumn>(view.getViewColumns());
		int[] colWidths = new int[view.getViewColumns().size()];
		int[] colIndices = new int[view.getViewColumns().size()];
		for (int i = 0; i < columns.size(); i++) {
			IReportViewColumn column = columns.get(i);
			html += "<td style=\"width:" + column.getWidth() + "px\"><strong>" + column.getReferencedCol().getName()
					+ "</strong></td>";
			colWidths[i] = column.getWidth();
			colIndices[i] = column.getReferencedCol().getColumnOrder() - 1;
			tableWidth += column.getWidth();
		}
		html += "</tr>";

		// Table rows
		for (Object[] row : resultList) {
			html += "<tr>";
			for (int index : colIndices) {
				html += "<td>";
				html += (index >= row.length || row[index] == null) ? " " : row[index].toString();
				html += "</td>";
			}
			html += "</tr>";
		}

		// Finalise table
		html += "</body></html>";
		return html.replaceFirst("#TABLEWIDTH#", tableWidth + "");
	}

}
