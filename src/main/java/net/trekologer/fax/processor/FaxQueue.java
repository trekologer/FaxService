/**
 * FaxQueue.java
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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.trekologer.fax.data.FaxJob;
import net.trekologer.fax.exception.FaxServiceException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class FaxQueue implements InitializingBean {

	@Value("${queue.size:10}")
	private int queueSize;

	private static final Logger LOG = LoggerFactory.getLogger(FaxQueue.class);

	private static BlockingQueue<FaxJob> workQueue;

	@Override
	public void afterPropertiesSet() throws Exception {
		if(workQueue == null) {
			LOG.info("Creating queue with size " + queueSize);
			workQueue = new LinkedBlockingQueue<FaxJob>(queueSize);
		}
	}
	
	/**
	 * Add a FaxJob to the queue
	 * @param job
	 * @return
	 * @throws FaxServiceException if the queue is full and item cannot be added
	 */
	public boolean addJob(FaxJob job) throws FaxServiceException {
		try {
			return workQueue.add(job);
		} catch(IllegalStateException e) {
			LOG.debug("addJob()", e);
			LOG.error("Exception Occurred: " + e.getMessage());
			// TODO -- make this a 503
			throw new FaxServiceException(e.getMessage());
		}
	}
	
	/**
	 * Remove a FaxJob from the queue, blocking if necessary until an item 
	 * becomes available.
	 * @return
	 * @throws FaxServiceException if interrupted
	 */
	public FaxJob takeJob() throws FaxServiceException {
		LOG.debug("takeJob() invoked");

		try {
			return workQueue.take();
		} catch(InterruptedException e) {
			// if we are interrupted, we are shutting down
			LOG.debug("takeJob()", e);
			LOG.info("Exception Occurred: " + e.getMessage());
			throw new FaxServiceException(e.getMessage());
		}
	}
	
	/**
	 * Get the number of FaxJobs waiting in the queue
	 * @return
	 */
	public int jobsWaiting() {
		return workQueue.size();
	}
	
}
