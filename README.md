FaxService
==========

A Java/Tomcat webservice to send faxes using Asterisk


## Requirements:
 -   Java 7 + log4j & Jersey REST
 -   Tomcat 7
 -   Asterisk 1.8 + Fax For Asterisk
 -   Libreoffice 3.5
 -   Ghostscript 9.0


## Overview
This is an e-fax webservice which allows one to upload many different file types and send as a fax over a Voice Over IP service. It makes use of Libreoffice and Ghostscript for file conversion and Asterisk with the Digium Fax For Asterisk (FFA) add-on for actually sending the fax. A single port FFA license can be obtained for free from Digium (http://www.digium.com/en/products/software/fax-for-asterisk).

To send a fax, perform a multipart HTTP POST to the URI FaxService/rest/fax with the following form fields:
 -   fromPhoneNumber: the phone number you want on the fax header
 -   header: the name you want on the fax header
 -   toPhoneNumber: the phone number to send to
 -   file: a document file that Libreoffice can understand

Once submitted, an XML will be returned containing an ID and status of the fax job (among other things). To check the status of a previously-submitted job, perform an HTTP GET on the URI FaxService/rest/fax/{jobId}. A job can be canceled by performing an HTTP DELETE on the same URI.

Because I have assumed that you will have a limited FFA license (such as 1 port), the FaxService will check for an available port before trying to send. 

This is currently very rough. It was tested on an Ubuntu 12.04 system but no guarantees. Use at your own risk!


## Instructions
 -   Install the requirements
 -   Configure Asterisk. See sample extensions.conf and sip.conf files in asterisk/
 -   Copy the files in script/ to somewhere that the tomcat user will be able execute them
 -   The faxservice.properties file in src/ needs to be updated with proper paths to Libreoffice, Ghostscript, and the above scrips. Update it now or you will need to do so after you build and install the WAR file and it is expanded.
 -   Build the WAR file
 -   Drop the WAR file into your Tomcat webapps directory


## Planned Future Ehnancements
 -  Front end
 -  Put configuration file somewhere other than the WAR file
 -  Proper build script
 -  Incoming faxes
