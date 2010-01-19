package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*
import static com.energizedwork.buildmonitor.ConfigurationState.*

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
            render(view:'index', model:model)
        }
    }
    
}