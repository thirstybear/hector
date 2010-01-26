package com.energizedwork.buildmonitor

import com.energizedwork.buildmonitor.BuildMonitor
import com.energizedwork.buildmonitor.BuildState
import com.energizedwork.buildmonitor.Configuration
import com.energizedwork.buildmonitor.ConfigurationState
import java.text.DateFormat
import java.text.SimpleDateFormat
import static com.energizedwork.buildmonitor.BuildState.failed
import static com.energizedwork.buildmonitor.ConfigurationState.unconfigured

class MainController {

    BuildMonitor buildMonitor
    Configuration configuration
    static DateFormat dateFormatter = new SimpleDateFormat('E, dd MMM yyyy HH:mm:ss z')

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
            Date ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince);
            println "mod-since-date $ifModifiedSinceDate"
            return buildMonitor.lastUpdate.after(ifModifiedSinceDate)
        } else {
            return true
        }
    }
}

    