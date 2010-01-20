package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*
import static com.energizedwork.buildmonitor.ConfigurationState.*
import javax.servlet.http.HttpServletResponse
import java.text.DateFormat
import java.text.SimpleDateFormat

class MainController {

    BuildMonitor buildMonitor
    Configuration configuration    

    def index = {
        if(configuration.state == unconfigured) {
            redirect(controller: 'configure')
        } else {
            BuildState state = buildMonitor.state
            Map model = ['state':state]
            if(state == failed) {
                model.failedProjects = buildMonitor.failedProjects
            }

            setLastModifiedHeader()

            render(view:'index', model:model)
        }
    }

    private def setLastModifiedHeader() {
        Date now = new Date()
        DateFormat dateFormatter = new SimpleDateFormat('E, dd MMM yyyy HH:mm:ss z')
        String dateString = dateFormatter.format(now)
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

    