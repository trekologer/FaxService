/**
 * FaxJob.java
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

import java.util.Calendar;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="faxjob")
public class FaxJob {
	
	private String jobId;
	private String toPhoneNumber;
	private String fromPhoneNumber;
	private String header;
	private String originalFileName;
	private String convertedFileName;
	private Status status;
	private int maxRetries;
	private int tryCount;
	private Calendar createdTime;
	private Calendar updatedTime;
	
	public FaxJob() {
		createdTime = Calendar.getInstance();
		updatedTime = createdTime;
	}
	
	@XmlAttribute
	public String getJobId() {
		return jobId;
	}
	
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	
	@XmlElement
	public String getToPhoneNumber() {
		return toPhoneNumber;
	}
	
	public void setToPhoneNumber(String toPhoneNumber) {
		this.toPhoneNumber = toPhoneNumber;
	}
	
	@XmlElement
	public String getFromPhoneNumber() {
		return fromPhoneNumber;
	}
	
	public void setFromPhoneNumber(String fromPhoneNumber) {
		this.fromPhoneNumber = fromPhoneNumber;
	}
	
	@XmlElement
	public String getHeader() {
		return header;
	}
	
	public void setHeader(String header) {
		this.header = header;
	}
	
	public String getOriginalFileName() {
		return originalFileName;
	}
	
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	
	public String getConvertedFileName() {
		return this.convertedFileName;
	}
	
	public void setConvertedFileName(String convertedFileName) {
		this.convertedFileName = convertedFileName;
	}
	
	@XmlElement
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	@XmlElement
	public int getMaxRetries() {
		return maxRetries;
	}
	
	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}
	
	@XmlElement
	public int getTryCount() {
		return tryCount;
	}
	
	public void setTryCount(int tryCount) {
		this.tryCount = tryCount;
	}
	
	@XmlElement
	public Calendar getCreatedTime() {
		return createdTime;
	}
	
	public void setCreatedTime(Calendar createdTime) {
		this.createdTime = createdTime;
	}
	
	@XmlElement
	public Calendar getUpdatedTime() {
		return updatedTime;
	}
	
	public void setUpdatedTime(Calendar updatedTime) {
		this.updatedTime = updatedTime;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("jobId=");
		if(jobId != null) sb.append(jobId);
		sb.append(", toPhoneNumber=");
		if(toPhoneNumber != null) sb.append(toPhoneNumber);
		sb.append(", fromPhoneNumber=");
		if(fromPhoneNumber != null) sb.append(fromPhoneNumber);
		sb.append(", header=");
		if(header != null) sb.append(header);
		sb.append(", originalFileName=");
		if(originalFileName != null) sb.append(originalFileName);
		sb.append(", convertedFileName=");
		if(convertedFileName != null) sb.append(convertedFileName);
		sb.append(", status=");
		if(status != null) sb.append(status.name());
		sb.append(", maxRetries=").append(Integer.toString(maxRetries));
		sb.append(", tryCount=").append(Integer.toString(tryCount));
		sb.append(", createdTime=");
		if(createdTime != null) sb.append(createdTime);
		sb.append(", updatedTime=");
		if(updatedTime != null) sb.append(updatedTime);
		return sb.toString();
	}
	
}
