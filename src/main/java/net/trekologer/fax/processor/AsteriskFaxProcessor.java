/**
 * AsteriskFaxProcessor.java
 * 
 * 
 * Copyright (c) 2013-2015 Andrew D. Bucko <adb@trekologer.net>
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
import net.trekologer.fax.data.FaxJob;
import net.trekologer.fax.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AsteriskFaxProcessor {

	@Value("${asterisk.spool.path:/var/spool/asterisk/outgoing}")
	private String asteriskSpoolPath;

	@Value("${asterisk.call.trunk:sip-trunk}")
	private String asteriskCallTrunk;

	@Value("${asterisk.call.retry.max:3}")
	private int asteriskCallMaxRetries;

	@Value("${asterisk.call.retry.wait:60}")
	private int asteriskCallRetryTime;

	@Value("${asterisk.call.wait:60}")
	private int asteriskCallWaitTime;

	@Value("${asterisk.call.archive:yes}")
	private String asteriskCallArchive;

	@Value("${asteirsk.call.context:outboundfax}")
	private String asteriskCallContext;

	@Value("${asterisk.call.extension:s}")
	private String asteriskCallExtension;

	@Value("${asterisk.call.priority:1}")
	private int asteriskCallPriority;

	private static final Logger LOG = LoggerFactory.getLogger(AsteriskFaxProcessor.class);
	
	public FaxJob process(FaxJob faxJob) {
		LOG.debug("process invoked with faxJob=["+faxJob+"]");
		
		String callFileContent = createCallFile(faxJob);
		String callFilePath = asteriskSpoolPath + "/" + faxJob.getJobId() + ".call";
		
		LOG.debug("process writing callFileContent=["+callFileContent+"] to callFilePath="+callFilePath);
		
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(callFilePath));
			br.write(callFileContent);
			br.flush();
			br.close();
			
			faxJob.setStatus(Status.SUBMITTED);
		} catch(IOException e) {
			LOG.debug("process()", e);
			LOG.error("Unable to write asterisk call file: "+e.getMessage());
			// File write failed
			faxJob.setStatus(Status.FAILED);
		}
		
		faxJob.setUpdatedTime(Calendar.getInstance());
		
		LOG.debug("process returning: faxJob=["+faxJob+"]");
		
		return faxJob;
	}
	
	private String createCallFile(FaxJob faxJob) {
		
		Calendar currentTime = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
		
		StringBuilder callFile = new StringBuilder();
		callFile.append("Channel: SIP/").append(faxJob.getToPhoneNumber()).append("@").append(asteriskCallTrunk).append("\n");
		callFile.append("MaxRetries: ").append(asteriskCallMaxRetries).append("\n");
		callFile.append("RetryTime: ").append(asteriskCallRetryTime).append("\n");
		callFile.append("WaitTime: ").append(asteriskCallWaitTime).append("\n");
		callFile.append("Archive: ").append(asteriskCallArchive).append("\n");
		callFile.append("Context: ").append(asteriskCallContext).append("\n");
		callFile.append("Extension: ").append(asteriskCallExtension).append("\n");
		callFile.append("Priority: ").append(asteriskCallPriority).append("\n");
		callFile.append("Set: FAXFILE=").append(faxJob.getConvertedFileName()).append("\n");
		callFile.append("Set: FAXHEADER=").append(faxJob.getHeader()).append("\n");
		callFile.append("Set: TIMESTAMP=").append(dateFormat.format(currentTime.getTime())).append("\n");
		callFile.append("Set: DESTINATION=").append(faxJob.getToPhoneNumber()).append("\n");
		callFile.append("Set: LOCALID=").append(faxJob.getFromPhoneNumber()).append("\n");
		callFile.append("Set: JOBID=").append(faxJob.getJobId()).append("\n");
		
		return callFile.toString();
	}
	
}
