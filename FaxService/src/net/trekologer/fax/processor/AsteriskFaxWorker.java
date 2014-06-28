/**
 * AsteriskFaxWorker.java
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.trekologer.fax.data.FaxJob;
import net.trekologer.fax.util.Constants;
import net.trekologer.fax.util.ServiceProperties;

import org.apache.log4j.Logger;

public class AsteriskFaxWorker implements Runnable {

	private static Logger LOG = Logger.getLogger(AsteriskFaxWorker.class);
	
	private boolean run = true;
	
	private static final int DEEFAULT_LICENSE_WAIT_SLEEP_TIME = 60000;
	private static final int DEFAULT_SUMBIT_SLEEP_TIME = 20000;
	
	@Override
	public void run() {
		LOG.info("Startup");
		
		while(run) {
			LOG.debug("Waiting for job");
			
			FaxJob job = null;
			try {
				job = FaxQueue.getInstance().takeJob();
			} catch(Exception e) {
				LOG.error(e);
				LOG.error("Exception occurred when taking job: "+e.getMessage());
			}
			
			boolean processed = false;
			while(!processed) {
				
				int faxLicenses = getFaxLicenses();
				int currentFaxSessions = getFaxSessions();
				int currentChannels = getChannels();
				
				LOG.debug("Fax licenses in use: "+currentFaxSessions+"/"+faxLicenses+", channels in use: "+currentChannels);
				
				if((faxLicenses > currentFaxSessions) && (faxLicenses > currentChannels)) {
					AsteriskFaxProcessor.process(job);
					
					// we've given the processor the job
					processed = true;
				} else {
					LOG.debug("All fax licenses in use, waiting for one to become free");
					
					try {
						Thread.sleep(ServiceProperties.getInt(Constants.ASTERISK_LICENSE_WAIT_SLEEP_TIME, DEEFAULT_LICENSE_WAIT_SLEEP_TIME));
					} catch (InterruptedException e) {
						LOG.debug(e);
					}
					
				}
				
			}
			
			try {
				// this is to prevent submitting another job while asterisk
				// is still setting up call for current job
				Thread.sleep(ServiceProperties.getInt(Constants.ASTERISK_SUBMIT_SLEEP_TIME, DEFAULT_SUMBIT_SLEEP_TIME));
			} catch(InterruptedException e) {
				LOG.debug(e);
			}
			
		}
	}
	
	public void stop() {
		run = false;
	}
	
	private int getFaxLicenses() {
		int count = 0;
		
		StringBuilder sb = new StringBuilder();
		sb.append(ServiceProperties.getString(Constants.ASTERISK_SCRIPT_PATH)).append("/");
		sb.append(ServiceProperties.getString(Constants.ASTERISK_FAX_LICENSES_COMMAND));
		
		Pattern pattern = Pattern.compile("\\d+");
		
		try {
			LOG.debug("getFaxLicenses running command=["+sb.toString()+"]");
			Process astCommand = Runtime.getRuntime().exec(sb.toString());
			
			int astReturn = astCommand.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(astCommand.getInputStream()));
			String line = null;
			
			LOG.debug("command exited with status "+astReturn);
			
			while ((line = br.readLine()) != null) {
				LOG.debug("command output => "+line);
				Matcher matcher = pattern.matcher(line);
				
				if(matcher.find()) {
					count = Integer.parseInt(matcher.group());
				}
			}
			
			br.close();
		} catch(Exception e) {
			LOG.error(e);
			LOG.error("getFaxLicenses exception occurred: "+e.getMessage());
		}
		
		LOG.debug("getFaxLicenses returning count="+count);
		return count;
	}
	
	private int getFaxSessions() {
		int count = 0;
		
		StringBuilder sb = new StringBuilder();
		sb.append(ServiceProperties.getString(Constants.ASTERISK_SCRIPT_PATH)).append("/");
		sb.append(ServiceProperties.getString(Constants.ASTERISK_FAX_SESSIONS_COMMAND));
		
		Pattern pattern = Pattern.compile("\\d+");
		
		try {
			LOG.debug("getFaxSessions running command=["+sb.toString()+"]");
			Process astCommand = Runtime.getRuntime().exec(sb.toString());
			
			int astReturn = astCommand.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(astCommand.getInputStream()));
			String line = null;

			LOG.debug("command exited with status "+astReturn);
			
			while ((line = br.readLine()) != null) {
				LOG.debug("command output => "+line);
				Matcher matcher = pattern.matcher(line);
				
				if(matcher.find()) {
					count = Integer.parseInt(matcher.group());
				}
			}
			
			br.close();
		} catch(Exception e) {
			LOG.error(e);
			LOG.error("getFaxSessions exception occurred: "+e.getMessage());
		}
		
		LOG.debug("getFaxSessions returning count="+count);
		return count;
	}
	
	private int getChannels() {
		int count = 0;
		
		StringBuilder sb = new StringBuilder();
		sb.append(ServiceProperties.getString(Constants.ASTERISK_SCRIPT_PATH)).append("/");
		sb.append(ServiceProperties.getString(Constants.ASTERISK_CHANNELS_COMMAND));
		
		Pattern pattern = Pattern.compile("\\d+");
		
		try {
			LOG.debug("getChannels running command=["+sb.toString()+"]");
			Process astCommand = Runtime.getRuntime().exec(sb.toString());
			
			int astReturn = astCommand.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(astCommand.getInputStream()));
			String line = null;

			LOG.debug("command exited with status "+astReturn);
			
			while ((line = br.readLine()) != null) {
				LOG.debug("command output => "+line);
				Matcher matcher = pattern.matcher(line);
				
				if(matcher.find()) {
					count = Integer.parseInt(matcher.group());
				}
			}
			
			br.close();
		} catch(Exception e) {
			LOG.error(e);
			LOG.error("getChannels exception occurred: "+e.getMessage());
		}
		
		LOG.debug("getChannels returning count="+count);
		return count;
	}
}
