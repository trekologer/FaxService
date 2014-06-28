/**
 * ServiceProperties.java
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

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ServiceProperties {

	private static final Logger LOG = Logger.getLogger(ServiceProperties.class);
	private static Properties properties;
	private static ServiceProperties self;
	
	private ServiceProperties() {
		properties = new Properties();
		try {
			properties.load(
					Thread.currentThread().getContextClassLoader().getResourceAsStream(Constants.PROPERTIES_FILE)
			);
		} catch(IOException e) {
			LOG.debug(e);
			LOG.fatal("Could not load properties file: reason="+e.getMessage());
		}
	}
	
	private static ServiceProperties getInstance() {
		if(self == null) {
			self = new ServiceProperties();
		}
		return self;
	}
	
	private Properties getProperties() {
		return properties;
	}
	
	public static String getString(String key) {
		return getInstance().getProperties().getProperty(key);
	}
	
	public static String getString(String key, String defaultValue) {
		String value = getString(key);
		
		if(value == null) {
			return defaultValue;
		}
		
		return value;
	}
	
	public static int getInt(String key, int defaultValue) {
		String value = getString(key);
		
		if(value == null) {
			return defaultValue;
		}
		
		try {
			return Integer.parseInt(value);
		} catch(NumberFormatException e) {
			LOG.debug(e);
			LOG.error("Could not parse key="+key+", value="+value+": reason="+e.getMessage());
		}
		
		return defaultValue;
	}
	
}
