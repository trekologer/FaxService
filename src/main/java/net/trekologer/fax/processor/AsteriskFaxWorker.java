/**
 * AsteriskFaxWorker.java
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.trekologer.fax.data.FaxJob;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AsteriskFaxWorker implements Runnable {

	@Autowired
	private AsteriskFaxProcessor processor;

	@Autowired
	private FaxQueue queue;

	@Value("${asterisk.script.path:/opt/faxservice/scripts}")
	private String asteriskScriptPath;

	@Value("${asterisk.script.channels:core_show_channels.sh}")
	private String asteriskChannelsCommand;

	@Value("${asterisk.script.licenses:fax_show_licenses.sh}")
	private String asteriskFaxLicensesCommand;

	@Value("${asterisk.script.sessions:fax_show_sessions.sh}")
	private String asteriskFaxSessionsCommand;

	@Value("${asterisk.license.wait:60000}")
	private int asteriskLicenseWaitSleepTime;

	@Value("${asterisk.submit.wait:20000}")
	private int asteriskSubmitSleepTime;

	private static Logger LOG = LoggerFactory.getLogger(AsteriskFaxWorker.class);
	
	private boolean run = true;
	
	@Override
	public void run() {
		LOG.info("Startup");
		
		while(true) {
			LOG.info("Waiting for job");
			
			FaxJob job = null;
			try {
				job = queue.takeJob();
			} catch(Exception e) {
				LOG.debug("run()", e);
				LOG.error("Exception occurred when taking job: " + e.getMessage());
			}
			
			boolean processed = false;
			while(!processed) {
				
				int faxLicenses = getFaxLicenses();
				int currentFaxSessions = getFaxSessions();
				int currentChannels = getChannels();
				
				LOG.debug("Fax licenses in use: "+currentFaxSessions+"/"+faxLicenses+", channels in use: "+currentChannels);
				
				if((faxLicenses > currentFaxSessions) && (faxLicenses > currentChannels)) {
					processor.process(job);
					
					// we've given the processor the job
					processed = true;
				} else {
					LOG.debug("All fax licenses in use, waiting for one to become free");
					
					try {
						Thread.sleep(asteriskLicenseWaitSleepTime);
					} catch (InterruptedException e) {
						LOG.debug("run()", e);
					}
					
				}
				
			}
			
			try {
				// this is to prevent submitting another job while asterisk
				// is still setting up call for current job
				Thread.sleep(asteriskSubmitSleepTime);
			} catch(InterruptedException e) {
				LOG.debug("run()", e);
			}
			
		}
	}
	
	public void stop() {
		run = false;
	}
	
	private int getFaxLicenses() {
		int count = 0;
		
		StringBuilder sb = new StringBuilder();
		sb.append(asteriskScriptPath).append("/");
		sb.append(asteriskFaxLicensesCommand);
		
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
			LOG.debug("getFaxLicenses()", e);
			LOG.error("getFaxLicenses exception occurred: " + e.getMessage());
		}
		
		LOG.debug("getFaxLicenses returning count="+count);
		return count;
	}
	
	private int getFaxSessions() {
		int count = 0;
		
		StringBuilder sb = new StringBuilder();
		sb.append(asteriskScriptPath).append("/");
		sb.append(asteriskFaxSessionsCommand);
		
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
			LOG.debug("getFaxSessions()", e);
			LOG.error("getFaxSessions exception occurred: " + e.getMessage());
		}
		
		LOG.debug("getFaxSessions returning count="+count);
		return count;
	}
	
	private int getChannels() {
		int count = 0;
		
		StringBuilder sb = new StringBuilder();
		sb.append(asteriskScriptPath).append("/");
		sb.append(asteriskChannelsCommand);
		
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
			LOG.debug("getChannels()", e);
			LOG.error("getChannels exception occurred: " + e.getMessage());
		}
		
		LOG.debug("getChannels returning count="+count);
		return count;
	}
}
