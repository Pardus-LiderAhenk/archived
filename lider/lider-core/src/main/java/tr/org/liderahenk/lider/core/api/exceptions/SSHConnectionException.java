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
package tr.org.liderahenk.lider.core.api.exceptions;

public class SSHConnectionException extends Exception {

	private static final long serialVersionUID = -1212172905712547439L;

	/**
	 * default constructor
	 */
	public SSHConnectionException() {
		super();
	}

	/**
	 * 
	 * @param message
	 */
	public SSHConnectionException(String message) {
		super(message);
	}

	/**
	 * 
	 * @param throwable
	 */
	public SSHConnectionException(Throwable throwable) {
		super(throwable);
	}

	/**
	 * 
	 * @param message
	 * @param throwable
	 */
	public SSHConnectionException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
