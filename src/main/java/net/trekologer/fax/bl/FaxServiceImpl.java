/**
 * FaxServiceImpl.java
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

package net.trekologer.fax.bl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.trekologer.fax.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.trekologer.fax.data.FaxJob;
import net.trekologer.fax.data.FaxJobStatus;
import net.trekologer.fax.data.Status;
import net.trekologer.fax.exception.FaxServiceException;
import net.trekologer.fax.processor.FaxQueue;
import net.trekologer.fax.util.Constants;
import net.trekologer.fax.util.FileConverter;
import net.trekologer.fax.util.ServiceProperties;
import org.springframework.stereotype.Component;

@Component
public class FaxServiceImpl implements FaxService {
	
	private static final Logger LOG = LoggerFactory.getLogger(FaxServiceImpl.class);
	
	private static Map<String, FaxJob> jobCache = new ConcurrentHashMap<String, FaxJob>();

	@Override
	public Collection<FaxJob> getAllJobs() throws FaxServiceException {
		LOG.debug("getAllJobs invoked");
		List<FaxJob> jobs = new LinkedList<FaxJob>();
		
		for(FaxJob job : jobCache.values()) {
			jobs.add(job);
		}
		
		if(LOG.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder();
			for(FaxJob j : jobs) {
				if(sb.length() > 0) {
					sb.append(", ");
				}
				sb.append("{").append(j).append("}");
			}
			LOG.debug("getAllJobs returning: jobs=["+sb.toString()+"]");
		}
		
		return jobs;
	}

	@Override
	public FaxJob getJob(String jobId) throws FaxServiceException {
		LOG.debug("getJob invoked: jobId="+jobId);
		
		FaxJob job = jobCache.get(jobId);
		
		if(job == null) {
			LOG.error("getJob No job exists in jobCache with jobId="+jobId);
			throw new ResourceNotFoundException("Unknown job "+jobId);
		}
		
		LOG.debug("getJob returning: job=["+job+"]");
		return job;
	}

	@Override
	public FaxJob createJob(FaxJob faxJob, InputStream fileInputStream)
			throws FaxServiceException {
		LOG.debug("createJob invoked: faxJob=["+faxJob+"]");
		
		faxJob.setJobId(UUID.randomUUID().toString());
		faxJob.setStatus(Status.CREATED);
		faxJob.setUpdatedTime(Calendar.getInstance());
		
		LOG.info("createJob Creating FaxJob jobId="+faxJob.getJobId());
		
		if(storeFile(fileInputStream, ServiceProperties.getString(Constants.WORK_FILE_PATH)+"/"+faxJob.getJobId())) {
			faxJob = FileConverter.convertFaxFile(faxJob);
			
			if(faxJob.getConvertedFileName() == null) {
				LOG.error("createJob Converting file failed for jobId="+faxJob.getJobId());
				faxJob.setStatus(Status.FAILED);
				throw new FaxServiceException("Could not convert file");
			}
			
			jobCache.put(faxJob.getJobId(), faxJob);
			
			LOG.info("createJob FaxJob created, sending to processor: jobId="+faxJob.getJobId());
			
			// put in queue instead of process right away
			// faxJob = AsteriskFaxProcessor.process(faxJob);
			
			FaxQueue.getInstance().addJob(faxJob);
			
			faxJob.setStatus(Status.QUEUED);
			jobCache.put(faxJob.getJobId(), faxJob);
			
			LOG.info("createJob FaxJob queued: jobId="+faxJob.getJobId());

		} else {
			LOG.error("createJob Storing file failed for jobId="+faxJob.getJobId());
			faxJob.setStatus(Status.FAILED);
			throw new FaxServiceException("Could not store file");
		}
		
		LOG.debug("createJob returning: faxJob=["+faxJob+"]");
		
		return faxJob;
	}

	@Override
	public void deleteJob(String jobId) throws FaxServiceException {
		LOG.debug("deleteJob invoked: jobId="+jobId);
		
		if(jobCache.get(jobId) == null) {
			LOG.error("No job exists in jobCache with jobId="+jobId);
			throw new ResourceNotFoundException("Unknown job "+jobId);
		}
		jobCache.remove(jobId);
		
		LOG.debug("deleteJob returning");
	}
	
	@Override
	public void setJobStatus(FaxJobStatus faxJobStatus) throws FaxServiceException {
		LOG.debug("setJobStatus faxJobStatus=["+faxJobStatus+"]");
		
		FaxJob faxJob = jobCache.get(faxJobStatus.getJobId());
		
		if(faxJob == null) {
			LOG.error("setJobStatus Unknown jobId="+faxJobStatus.getJobId());
			throw new ResourceNotFoundException("Unknown job "+faxJobStatus.getJobId());
		}
		
		faxJob.setUpdatedTime(Calendar.getInstance());
		
		if((faxJobStatus.getFaxStatus() != null) && (faxJobStatus.getFaxStatus().equals("SENDING"))) {
			faxJob.setStatus(Status.SENDING);
			LOG.info("setJobStatus FaxJob sending: jobId="+faxJob.getJobId());
		} else if((faxJobStatus.getFaxStatus() != null) && (faxJobStatus.getFaxStatus().equals("SUCCESS"))) {
			faxJob.setStatus(Status.SUCCESSFUL);
			LOG.info("setJobStatus FaxJob completed successfully: jobId="+faxJob.getJobId());
		} else {
			// we failed :(
			faxJob.setStatus(Status.FAILED);
			faxJob.setTryCount(faxJob.getTryCount() + 1);
			
			if(faxJob.getTryCount() <= faxJob.getMaxRetries()) {
				// we can retry. put back into queue
				LOG.info("setJobStatus FaxJob retrying: jobId="+faxJob.getJobId());
				faxJob.setStatus(Status.RETRY_WAIT);
				
				// put in queue instead of process right away
				// faxJob = AsteriskFaxProcessor.process(faxJob);
				
				FaxQueue.getInstance().addJob(faxJob);
			} else {
				// no more retries
				LOG.warn("setJobStatus FaxJob exceeded max retries: jobId="+faxJob.getJobId()+", maxRetries="+faxJob.getMaxRetries());
			}
			
		}
		
		jobCache.put(faxJob.getJobId(), faxJob);
	}
	
	private boolean storeFile(InputStream is, String location) {
		
		try {
			OutputStream os = new FileOutputStream(new File(location));
			
			int read = 0;
			byte[] bytes = new byte[1024];
			
			while((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}
			
			os.flush();
			os.close();
			
		} catch(IOException e) {
			LOG.error("storeFile()", e);
			LOG.error("Unable to store file at location: " + location + ", reason: " + e.getMessage());
			return false;
		}
		
		return true;
	}

}
