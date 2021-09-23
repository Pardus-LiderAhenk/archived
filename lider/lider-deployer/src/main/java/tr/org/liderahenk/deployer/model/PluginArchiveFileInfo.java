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
package tr.org.liderahenk.deployer.model;

import tr.org.liderahenk.lider.core.api.deployer.ILiderHotDeployListener;

/**
 * Default implementation for {@link ILiderHotDeployListener}
 * 
 * @author <a href="mailto:basaran.ismaill@gmail.com">İsmail BAŞARAN</a>
 * 
 */
public class PluginArchiveFileInfo {
	
	
	private String liderPluginName;
	private String ahenkPluginName;
	private String liderConsolePluginDirectory;
	private int status;
	
	public String getLiderPluginName() {
		return liderPluginName;
	}
	public void setLiderPluginName(String liderPluginName) {
		this.liderPluginName = liderPluginName;
	}
	public String getAhenkPluginName() {
		return ahenkPluginName;
	}
	public void setAhenkPluginName(String ahenkPluginName) {
		this.ahenkPluginName = ahenkPluginName;
	}
	public String getLiderConsolePluginDirectory() {
		return liderConsolePluginDirectory;
	}
	public void setLiderConsolePluginDirectory(String liderConsolePluginDirectory) {
		this.liderConsolePluginDirectory = liderConsolePluginDirectory;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

}
