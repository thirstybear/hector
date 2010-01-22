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

    def index = {
        if (configuration.state == unconfigured) {
            redirect(controller: 'configure')
        } else {
            BuildState state = buildMonitor.state
            Map model = ['state': state]
            if (state == failed) {
                model.failedProjects = buildMonitor.failedProjects
            }

            setLastModifiedHeader()

            render(view: 'index', model: model)
        }
    }

    private def setLastModifiedHeader() {
        Date lastUpdate = buildMonitor.lastUpdate

        DateFormat dateFormatter = new SimpleDateFormat('E, dd MMM yyyy HH:mm:ss z')
        String dateString = dateFormatter.format(lastUpdate)
        response.addHeader 'Last-Modified', dateString
    }
}

// TODO add Last-Modified - currently returning 'now' each time. Need to add conditionals
/*
 Last-Modified attribute needs to be set to
    1. Request and no change in state - original request time
    2. Request and has changed - this request time

    change: BuildState & failed project list
 */

    