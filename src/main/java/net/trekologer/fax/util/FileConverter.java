/**
 * FileConverter.java
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

package net.trekologer.fax.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.apache.log4j.Logger;

import net.trekologer.fax.data.FaxJob;
import net.trekologer.fax.data.Status;

public class FileConverter {
	
	private static Logger LOG = Logger.getLogger(FileConverter.class);
	
	public static FaxJob convertFaxFile(FaxJob faxJob) {
		faxJob.setConvertedFileName(convertToTiff(faxJob.getJobId()));
		
		if (faxJob.getConvertedFileName() != null) {
			faxJob.setStatus(Status.CONVERTED);
		}
		
		faxJob.setUpdatedTime(Calendar.getInstance());
		return faxJob;
	}
	
	private static String convertToTiff(String fileName) {
		
		File workDir = new File(ServiceProperties.getString(Constants.WORK_FILE_PATH));
		String outputFileName = fileName + "." + ServiceProperties.getString(Constants.TIFF_FILE_EXTENSION);
		
		StringBuilder pdfConvertCommand = new StringBuilder();
		pdfConvertCommand.append("/bin/sh ");
		pdfConvertCommand.append(ServiceProperties.getString(Constants.LIBREOFFICE_PATH));
		pdfConvertCommand.append(" --headless");
		pdfConvertCommand.append(" --invisible");
		pdfConvertCommand.append(" --convert-to pdf");
		pdfConvertCommand.append(" --outdir ").append(ServiceProperties.getString(Constants.WORK_FILE_PATH));
		// providing workDir parameter to exec() should mean I don't need to specify the full path for each file
		pdfConvertCommand.append(" ").append(ServiceProperties.getString(Constants.WORK_FILE_PATH)).append("/").append(fileName);
		// but it doesn't seem to work...
		// pdfConvertCommand.append(" ").append(fileName);
		
		StringBuilder tiffConvertCommand = new StringBuilder();
		tiffConvertCommand.append(ServiceProperties.getString(Constants.GHOSTSCRIPT_PATH));
		tiffConvertCommand.append(" -dBATCH");
		tiffConvertCommand.append(" -dNOPAUSE");
		tiffConvertCommand.append(" -sDEVICE=tiffg3");
		tiffConvertCommand.append(" -sPAPERSIZE=letter");
		tiffConvertCommand.append(" -r204x196");
		tiffConvertCommand.append(" -dAutoRotatePages");
		tiffConvertCommand.append(" -g1728x2156");
		// providing workDir parameter to exec() should mean I don't need to specify the full path for each file
		tiffConvertCommand.append(" -sOutputFile=");
		tiffConvertCommand.append(ServiceProperties.getString(Constants.WORK_FILE_PATH)).append("/").append(outputFileName);
		tiffConvertCommand.append(" ");
		tiffConvertCommand.append(ServiceProperties.getString(Constants.WORK_FILE_PATH)).append("/");
		tiffConvertCommand.append(fileName).append(".").append(ServiceProperties.getString(Constants.PDF_FILE_EXTENSION));
		
		try {
			LOG.debug("Running pdfConvert command: "+pdfConvertCommand.toString());
			
			Process pdfConvert = Runtime.getRuntime().exec(pdfConvertCommand.toString());
			int pdfReturn = pdfConvert.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(pdfConvert.getInputStream()));
			String line = null;
			
			while ((line = br.readLine()) != null) {
				LOG.debug("pdfConvert output => "+line);
			}
			
			br.close();
			
			br = new BufferedReader(new InputStreamReader(pdfConvert.getErrorStream()));
			line = null;
			
			while ((line = br.readLine()) != null) {
				LOG.debug("pdfConvert error => "+line);
			}
			
			br.close();
			
			LOG.debug("Completed pdfConvert with return code: "+pdfReturn);
			
			LOG.debug("Running tiffConvert command: "+tiffConvertCommand.toString());
			
			Process tiffConvert = Runtime.getRuntime().exec(tiffConvertCommand.toString());
			int tiffReturn = tiffConvert.waitFor();
			br = new BufferedReader(new InputStreamReader(tiffConvert.getInputStream()));
			line = null;
			
			while ((line = br.readLine()) != null) {
				LOG.debug("tiffConvert output => "+line);
			}
			
			br.close();
			
			br = new BufferedReader(new InputStreamReader(tiffConvert.getErrorStream()));
			line = null;
			
			while ((line = br.readLine()) != null) {
				LOG.debug("tiffConvert error => "+line);
			}
			
			br.close();
			
			LOG.debug("Completed tiffConvert with return code: "+tiffReturn);
			
		} catch(Exception e) {
			LOG.error(e);
			LOG.error("Unable to convert file, reason: "+e.getMessage());
			return null;
		}

		File outputFile = new File(workDir, outputFileName);
		
		if(!outputFile.exists()) {
			LOG.error("Output file does not exist at "+outputFile.getAbsolutePath());
			return null;
		}
		
		return outputFileName;
	}

}
