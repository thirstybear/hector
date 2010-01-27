package com.energizedwork.buildmonitor

import com.energizedwork.buildmonitor.BuildMonitor
import com.energizedwork.buildmonitor.BuildState
import com.energizedwork.buildmonitor.Configuration
import com.energizedwork.buildmonitor.ConfigurationState
import java.text.DateFormat
import java.text.SimpleDateFormat
import static com.energizedwork.buildmonitor.BuildState.failed
import static com.energizedwork.buildmonitor.ConfigurationState.unconfigured
import org.apache.commons.lang.time.DateUtils

class MainController {

    BuildMonitor buildMonitor
    Configuration configuration
    static DateFormat dateFormatter = new SimpleDateFormat('E, dd MMM yyyy HH:mm:ss z')
    static DateFormat dateParser = new SimpleDateFormat('E, dd MMM yyyy HH:mm:ss')

    def index = {
        if (configuration.state == unconfigured) {
            redirect(controller: 'configure')
        } else {
            setLastModifiedHeader()
            if (modifiedSinceLastRequest()) {
                renderPage()
            } else {
                reply304()
            }
        }
    }

    private def renderPage() {
        BuildState state = buildMonitor.state
        Map model = ['state': state]
        if (state == failed) {
            model.failedProjects = buildMonitor.failedProjects
        }

        render(view: 'index', model: model)
    }

    private def setLastModifiedHeader() {
        Date lastUpdate = buildMonitor.lastUpdate

        DateFormat dateFormatter = new SimpleDateFormat('E, dd MMM yyyy HH:mm:ss z')
        String dateString = dateFormatter.format(lastUpdate)
        response.addHeader 'Last-Modified', dateString
    }

    private def reply304() {
        response.sendError 304 
    }

    private boolean modifiedSinceLastRequest() {
        String ifModifiedSince = request.getHeader('If-Modified-Since')

        if (ifModifiedSince) {
            // Note: using the original dateFormatter to parse is producing the wrong result (t + 1 hour). WTF?
            // something to do with including 'z' timezone.
            Date ifModifiedSinceDate = dateParser.parse(ifModifiedSince);

            // todo remove println!
            println "Original if-mod-since $ifModifiedSince"
            println "if-mod-since-date $ifModifiedSinceDate (${ifModifiedSinceDate.getTime()})"
            println "lastUpdate: $buildMonitor.lastUpdate (${buildMonitor.lastUpdate.getTime()})"


            Date lastMonitorUpdate = buildMonitor.lastUpdate
            Date roundedLastMonitorUpdate = DateUtils.round(lastMonitorUpdate, Calendar.SECOND)
            return roundedLastMonitorUpdate.after(ifModifiedSinceDate)
        } else {
            return true
        }
    }
}

    