/**
 * Constants.java
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

public class Constants {

	public static final String PROPERTIES_FILE = "faxservice.properties";
	
	public static final String GHOSTSCRIPT_PATH = "ghostscript.path";
	public static final String LIBREOFFICE_PATH = "libreoffice.path";
	public static final String WORK_FILE_PATH = "workfile.path";
	
	public static final String PDF_FILE_EXTENSION = "file.extension.pdf";
	public static final String TIFF_FILE_EXTENSION = "file.extension.tiff";
	
	public static final String FAX_QUEUE_SIZE = "queue.size";
	
	public static final String ASTERISK_SPOOL_PATH = "asterisk.spool.path";
	public static final String ASTERISK_SCRIPT_PATH = "asterisk.script.path";
	public static final String ASTERISK_FAX_LICENSES_COMMAND = "asterisk.script.licenses";
	public static final String ASTERISK_FAX_SESSIONS_COMMAND = "asterisk.script.sessions";
	public static final String ASTERISK_CHANNELS_COMMAND = "asterisk.script.channels";
	
	public static final String ASTERISK_LICENSE_WAIT_SLEEP_TIME = "asterisk.license.wait"; // time until next check if license is free
	public static final String ASTERISK_SUBMIT_SLEEP_TIME = "asterisk.submit.wait";  // after submitting job to check for new job
	
	public static final String ASTERISK_CALL_MAX_RETRIES = "asterisk.call.retry.max";
	public static final String ASTERISK_CALL_RETRY_TIME = "asterisk.call.retry.wait";
	public static final String ASTERISK_CALL_WAIT_TIME = "asterisk.call.wait";
	public static final String ASTERISK_CALL_ARCHIVE = "asterisk.call.archive";
	public static final String ASTERISK_CALL_CONTEXT = "asteirsk.call.context";
	public static final String ASTERISK_CALL_EXTENSION = "asterisk.call.extension";
	public static final String ASTERISK_CALL_TRUNK = "asterisk.call.trunk";
	public static final String ASTERISK_CALL_PRIORITY = "asterisk.call.priority";
	
}
