/**
 * FaxServiceException.java
 * 
 * 
 * Copyright (c) 2013-2014 Andrew D. Bucko <adb@trekologer.net>
 * 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.trekologer.fax.exception;

import javax.servlet.http.HttpServletResponse;

public class FaxServiceException extends Exception {

	private static final long serialVersionUID = 5998156674609397145L;
	private int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	
	public FaxServiceException() {
		super();
	}
	
	public FaxServiceException(Throwable cause, String message) {
		super(message, cause);
	}
	
	public FaxServiceException(String message) {
		super(message);
	}
	
	public FaxServiceException(String message, int status) {
		super(message);
		setStatus(status);
	}
	
	public FaxServiceException(Exception e) {
		super(e);
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
}
