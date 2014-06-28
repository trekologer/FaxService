/**
 * FaxResource.java
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

package net.trekologer.fax.service;

import java.io.InputStream;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

import net.trekologer.fax.bl.FaxService;
import net.trekologer.fax.bl.FaxServiceImpl;
import net.trekologer.fax.data.FaxJob;
import net.trekologer.fax.data.FaxJobStatus;
import net.trekologer.fax.exception.FaxServiceException;

@Path("/fax")
public class FaxResource {

	protected static final Logger LOG = Logger.getLogger(FaxResource.class);
	protected static FaxService service;
	
	@PostConstruct
	public void init() throws ServletException {
		if(service == null) {
			service = new FaxServiceImpl();
		}
	}
	
	@GET
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Collection<FaxJob> getJobs() {
		
		LOG.debug("getJobs Request");
		
		Collection<FaxJob> jobs = null;
		
		try {
			jobs = service.getAllJobs();
		} catch(FaxServiceException e) {
			handleServiceException(e);
		}
		
		LOG.debug("getJobs Response: jobs=["+jobs+"]");
		
		return jobs;
	}
	
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public FaxJob createJob(
			@FormDataParam("file") InputStream fileIs,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@FormDataParam("toPhoneNumber") String toPhoneNumber,
			@FormDataParam("fromPhoneNumber") String fromPhoneNumber,
			@FormDataParam("header") String header) {
		
		LOG.debug("createJob Request: file=["+fileDetail+"], toPhoneNumber="+toPhoneNumber+", fromPhoneNumber="+fromPhoneNumber+", header="+header);
		
		FaxJob faxJob = new FaxJob();
		faxJob.setOriginalFileName(fileDetail.getFileName());
		faxJob.setToPhoneNumber(toPhoneNumber);
		faxJob.setFromPhoneNumber(fromPhoneNumber);
		faxJob.setHeader(header);
		
		try {
			faxJob = service.createJob(faxJob, fileIs);
		} catch(FaxServiceException e) {
			handleServiceException(e);
		}
		
		LOG.debug("createJob Response: faxJob=["+faxJob+"]");
		
		return faxJob;
	}
	
	@GET
	@Path("/{jobId}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public FaxJob getJob(@PathParam("jobId") String jobId) {
		
		LOG.debug("getJob Request: jobId="+jobId);
		
		FaxJob faxJob = null;
		
		try {
			faxJob = service.getJob(jobId);
		} catch(FaxServiceException e) {
			handleServiceException(e);
		}
		
		LOG.debug("getJob Response: faxJob=["+faxJob+"]");
		
		return faxJob;
	}
	
	@DELETE
	@Path("/{jobId}")
	public void deleteJob(@PathParam("jobId") String jobId) {
		
		LOG.debug("deleteJob Request: jobId="+jobId);
		
		try {
			service.deleteJob(jobId);
		} catch(FaxServiceException e) {
			handleServiceException(e);
		}
	}
	
	@GET
	@Path("/{jobId}/status")
	@Produces(MediaType.TEXT_PLAIN)
	public String getJobStatus(@PathParam("jobId") String jobId) {
		
		LOG.debug("setJobStatus Request: jobId="+jobId);
		
		FaxJob faxJob = null;
		
		try {
			faxJob = service.getJob(jobId);
		} catch(FaxServiceException e) {
			handleServiceException(e);
		}
		
		LOG.debug("getJobStatus Response: faxJob=["+faxJob+"]");
		
		return "Status: "+faxJob.getStatus().name();
	}
	
	@POST
	@Path("/{jobId}/status")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void setJobStatus(
			@PathParam("jobId") String jobId,
			@FormParam("faxStatus") String faxStatus,
			@FormParam("faxStatusString") String faxStatusString,
			@FormParam("faxError") String faxError) {
		
		LOG.debug("setJobStatus Request: jobId="+jobId+", faxStatus="+faxStatus+", faxStatusString="+faxStatusString+", faxError="+faxError);
		
		FaxJobStatus faxJobStatus = new FaxJobStatus();
		faxJobStatus.setJobId(jobId);
		faxJobStatus.setFaxStatus(faxStatus);
		faxJobStatus.setFaxStatusString(faxStatusString);
		faxJobStatus.setFaxError(faxError);
		
		try {
			service.setJobStatus(faxJobStatus);
		} catch(FaxServiceException e) {
			handleServiceException(e);
		}
	}
	
	
	private void handleServiceException(FaxServiceException e) {
		LOG.error("Returning Error: message="+e.getMessage()+", status="+e.getStatus());
		throw new WebApplicationException(
				Response.status(e.getStatus()).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build()
		);
	}
	
}
