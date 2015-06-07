/**
 * FaxJobStatus.java
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

package net.trekologer.fax.data;

public class FaxJobStatus {

	private String jobId;
	private String faxStatus;
	private String faxStatusString;
	private String faxError;
	
	public FaxJobStatus() {
	}
	
	public FaxJobStatus(String jobId) {
		this.jobId = jobId;
	}
	
	public String getJobId() {
		return jobId;
	}
	
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	
	public String getFaxStatus() {
		return faxStatus;
	}
	
	public void setFaxStatus(String faxStatus) {
		this.faxStatus = faxStatus;
	}
	
	public String getFaxStatusString() {
		return faxStatusString;
	}
	
	public void setFaxStatusString(String faxStatusString) {
		this.faxStatusString = faxStatusString;
	}
	
	public String getFaxError() {
		return faxError;
	}
	
	public void setFaxError(String faxError) {
		this.faxError = faxError;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("jobId=");
		if(jobId != null) sb.append(jobId);
		sb.append(", faxStatus=");
		if(faxStatus != null) sb.append(faxStatus);
		sb.append(", faxStatusString=");
		if(faxStatusString != null) sb.append(faxStatusString);
		sb.append(", faxError=");
		if(faxError != null) sb.append(faxError);
		
		return sb.toString();
	}
}
