/**
 * FaxServiceServletContextListener.java
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

package net.trekologer.fax.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.trekologer.fax.processor.AsteriskFaxWorker;

public class FaxServiceServletContextListener implements ServletContextListener {

	private static final Logger LOG = LoggerFactory.getLogger(FaxServiceServletContextListener.class);
	private AsteriskFaxWorker worker;
	private Thread workerThread;
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			worker.stop();
			workerThread.interrupt();
		} catch(Exception e) {
			// if we catch an exception on shutdown, do we care?
			LOG.debug("contextDestroyed()", e);
			LOG.error("Exception Occurred: " + e.getMessage());
		}
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		LOG.info("jFaxService");
		LOG.info("Copyright (c) 2013-2014 Andrew D. Bucko <adb@trekologer.net>");
		LOG.info("");
		LOG.info("This program comes with ABSOLUTELY NO WARRANTY. This is free software, and you ");
		LOG.info("are welcome to redistribute it under certain conditions.");
		
		if(worker == null) {
			worker = new AsteriskFaxWorker();
			workerThread = new Thread(worker);
			workerThread.start();
		}
	}

}
