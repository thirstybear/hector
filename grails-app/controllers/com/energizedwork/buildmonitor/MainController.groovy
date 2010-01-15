package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.ConfigurationState.*

class MainController {

    BuildMonitorService buildMonitorService
    Configuration configuration    

    def index = {
        if(configuration.state == unconfigured) {
            redirect(controller: 'configure')
        } else {
            render(view:'index', model:['state':buildMonitorService.state])
        }
    }
}