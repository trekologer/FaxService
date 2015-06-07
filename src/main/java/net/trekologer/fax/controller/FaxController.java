/**
 * FaxController.java
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

package net.trekologer.fax.controller;

import net.trekologer.fax.bl.FaxService;
import net.trekologer.fax.data.FaxJob;
import net.trekologer.fax.data.FaxJobStatus;

import net.trekologer.fax.exception.FaxServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;


/**
 * Created by adb on 6/2/15.
 */
@RestController
public class FaxController {

    @Autowired
    protected FaxService service;
    private static Logger LOG = LoggerFactory.getLogger(FaxController.class);

    @RequestMapping(
            value="/fax",
            method= RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public Collection<FaxJob> getJobs() {

        LOG.debug("getJobs Request");

        Collection<FaxJob> jobs = null;

        jobs = service.getAllJobs();

        LOG.debug("getJobs Response: jobs=["+jobs+"]");

        return jobs;
    }

    @RequestMapping(
            value="/fax",
            method= RequestMethod.POST,
            consumes=MediaType.MULTIPART_FORM_DATA_VALUE,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public FaxJob createJob(
            @RequestParam("toPhoneNumber") String toPhoneNumber,
            @RequestParam("fromPhoneNumber") String fromPhoneNumber,
            @RequestParam("header") String header,
            @RequestParam("file") MultipartFile file) {

        LOG.debug("createJob Request: file=["+file+"], toPhoneNumber="+toPhoneNumber+", fromPhoneNumber="+fromPhoneNumber+", header="+header);

        FaxJob faxJob = new FaxJob();
        faxJob.setOriginalFileName(file.getOriginalFilename());
        faxJob.setToPhoneNumber(toPhoneNumber);
        faxJob.setFromPhoneNumber(fromPhoneNumber);
        faxJob.setHeader(header);

        try {
            faxJob = service.createJob(faxJob, file.getInputStream());
        } catch(IOException e) {
            LOG.error("createJob", e);
            LOG.error("Could not get file input stream: "+e.getMessage());
            throw new FaxServiceException("Could not get file input stream");
        }

        LOG.debug("createJob Response: faxJob=["+faxJob+"]");

        return faxJob;
    }

    @RequestMapping(
            value="/fax/{jobId}",
            method= RequestMethod.GET,
            produces=MediaType.APPLICATION_JSON_VALUE
    )
    public FaxJob getJob(@PathVariable("jobId") String jobId) {

        LOG.debug("getJob Request: jobId="+jobId);

        FaxJob faxJob = null;

        faxJob = service.getJob(jobId);

        LOG.debug("getJob Response: faxJob=["+faxJob+"]");

        return faxJob;
    }

    @RequestMapping(
            value="/fax/{jobId}",
            method= RequestMethod.DELETE
    )
    public void deleteJob(@PathVariable("jobId") String jobId) {

        LOG.debug("deleteJob Request: jobId="+jobId);

        service.deleteJob(jobId);
    }

    @RequestMapping(
            value="/fax/{jobId}/status",
            method= RequestMethod.GET,
            produces=MediaType.TEXT_PLAIN_VALUE
    )
    public String getJobStatus(@PathVariable("jobId") String jobId) {

        LOG.debug("setJobStatus Request: jobId="+jobId);

        FaxJob faxJob = null;

        faxJob = service.getJob(jobId);

        LOG.debug("getJobStatus Response: faxJob=["+faxJob+"]");

        return "Status: "+faxJob.getStatus().name();
    }

    @RequestMapping(
            value="/fax/{jobId}/status",
            method= RequestMethod.POST,
            produces=MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public void setJobStatus(
            @PathVariable("jobId") String jobId,
            @RequestBody FaxJobStatus faxJobStatus) {
        faxJobStatus.setJobId(jobId);
        LOG.debug("setJobStatus Request: jobId="+jobId+", faxJobStatus="+faxJobStatus);

        service.setJobStatus(faxJobStatus);

    }
}
