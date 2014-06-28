/**
 * AsteriskFaxProcessor.java
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

package net.trekologer.fax.processor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import net.trekologer.fax.data.FaxJob;
import net.trekologer.fax.data.Status;
import net.trekologer.fax.util.Constants;
import net.trekologer.fax.util.ServiceProperties;

public class AsteriskFaxProcessor {
	
	private static final Logger LOG = Logger.getLogger(AsteriskFaxProcessor.class);
	
	private static final int DEFAULT_MAX_RETRIES = 3;
	private static final int DEFAULT_CALL_RETRY_TIME = 60;
	private static final int DEFAULT_CALL_WAIT_TIME = 60;
	private static final int DEFAULT_CALL_PRIORITY = 1;
	
	public static FaxJob process(FaxJob faxJob) {
		LOG.debug("process invoked with faxJob=["+faxJob+"]");
		
		String callFileContent = createCallFile(faxJob);
		String callFilePath = ServiceProperties.getString(Constants.ASTERISK_SPOOL_PATH) + "/" + faxJob.getJobId() + ".call";
		
		LOG.debug("process writing callFileContent=["+callFileContent+"] to callFilePath="+callFilePath);
		
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(callFilePath));
			br.write(callFileContent);
			br.flush();
			br.close();
			
			faxJob.setStatus(Status.SUBMITTED);
		} catch(IOException e) {
			LOG.error(e);
			LOG.error("Unable to write asterisk call file: "+e.getMessage());
			// File write failed
			faxJob.setStatus(Status.FAILED);
		}
		
		faxJob.setUpdatedTime(Calendar.getInstance());
		
		LOG.debug("process returning: faxJob=["+faxJob+"]");
		
		return faxJob;
	}
	
	private static String createCallFile(FaxJob faxJob) {
		
		Calendar currentTime = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		
		StringBuilder callFile = new StringBuilder();
		callFile.append("Channel: SIP/").append(faxJob.getToPhoneNumber()).append("@").append(ServiceProperties.getString(Constants.ASTERISK_CALL_TRUNK)).append("\n");
		callFile.append("MaxRetries: ").append(Integer.toString(ServiceProperties.getInt(Constants.ASTERISK_CALL_MAX_RETRIES, DEFAULT_MAX_RETRIES))).append("\n");
		callFile.append("RetryTime: ").append(Integer.toString(ServiceProperties.getInt(Constants.ASTERISK_CALL_RETRY_TIME, DEFAULT_CALL_RETRY_TIME))).append("\n");
		callFile.append("WaitTime: ").append(Integer.toString(ServiceProperties.getInt(Constants.ASTERISK_CALL_WAIT_TIME, DEFAULT_CALL_WAIT_TIME))).append("\n");
		callFile.append("Archive: ").append(ServiceProperties.getString(Constants.ASTERISK_CALL_ARCHIVE)).append("\n");
		callFile.append("Context: ").append(ServiceProperties.getString(Constants.ASTERISK_CALL_CONTEXT)).append("\n");
		callFile.append("Extension: ").append(ServiceProperties.getString(Constants.ASTERISK_CALL_EXTENSION)).append("\n");
		callFile.append("Priority: ").append(Integer.toString(ServiceProperties.getInt(Constants.ASTERISK_CALL_PRIORITY, DEFAULT_CALL_PRIORITY))).append("\n");
		callFile.append("Set: FAXFILE=").append(faxJob.getConvertedFileName()).append("\n");
		callFile.append("Set: FAXHEADER=").append(faxJob.getHeader()).append("\n");
		callFile.append("Set: TIMESTAMP=").append(dateFormat.format(currentTime.getTime())).append("\n");
		callFile.append("Set: DESTINATION=").append(faxJob.getToPhoneNumber()).append("\n");
		callFile.append("Set: LOCALID=").append(faxJob.getFromPhoneNumber()).append("\n");
		callFile.append("Set: JOBID=").append(faxJob.getJobId()).append("\n");
		
		return callFile.toString();
	}
	
}
