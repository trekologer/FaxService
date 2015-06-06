/**
 * ServiceLauncher.java
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

package net.trekologer.fax.bl;

import net.trekologer.fax.processor.AsteriskFaxWorker;
import net.trekologer.fax.processor.FaxQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Created by adb on 6/5/15.
 */
@Configuration
public class ServiceLauncher {

    @Autowired
    private AsteriskFaxWorker worker;

    private Thread workerThread;

    @PostConstruct
    public void init() {
        workerThread = new Thread(worker);
        workerThread.start();
    }

    @PreDestroy
    public void shutdown() {

    }

}
