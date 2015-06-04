/**
 * FaxService.java
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

import java.io.InputStream;
import java.util.Collection;

import net.trekologer.fax.data.FaxJob;
import net.trekologer.fax.data.FaxJobStatus;
import net.trekologer.fax.exception.FaxServiceException;

public interface FaxService {

	/**
	 * Returns a Collection of all FaxJobs currently in progress, including
	 * the status of each one.
	 * @return
	 * @throws FaxServiceException
	 */
	public Collection<FaxJob> getAllJobs() throws FaxServiceException;
	
	/**
	 * Get a FaxJob out of the cache, identified by the provided jobId.
	 * @param jobId
	 * @return
	 * @throws FaxServiceException if the FaxJob is not found
	 */
	public FaxJob getJob(String jobId) throws FaxServiceException;
	
	/**
	 * Create a new FaxJob in the cache using the provided faxJob and 
	 * InputStream of the document file to be faxed.
	 * @param faxJob
	 * @param fileInputStream
	 * @return
	 * @throws FaxServiceException if FaxJob could not be created
	 */
	public FaxJob createJob(FaxJob faxJob, InputStream fileInputStream) throws FaxServiceException;
	
	/**
	 * Delete a FaxJob out of the cache, identified by the provided jobId.
	 * @param jobId
	 * @throws FaxServiceException if the FaxJob is not found
	 */
	public void deleteJob(String jobId) throws FaxServiceException;
	
	/**
	 * Updates the status of a FaxJob
	 * @param faxJobStatus
	 * @throws FaxServiceException if the FaxJob is not found
	 */
	public void setJobStatus(FaxJobStatus faxJobStatus) throws FaxServiceException;
	
}
