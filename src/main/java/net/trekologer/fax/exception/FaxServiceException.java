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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="Internal Error")
public class FaxServiceException extends RuntimeException {

	private static final long serialVersionUID = 5998156674609397145L;
	
	public FaxServiceException() {
		super();
	}
	
	public FaxServiceException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FaxServiceException(String message) {
		super(message);
	}

	public FaxServiceException(Exception e) {
		super(e);
	}
	
}
