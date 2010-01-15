package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.ConfigurationState.*

class MainController {

    Configuration configuration

    def index = {
        if(configuration.state == unconfigured) {
            redirect(url:'/configure')
        } else {
            render(view:'index', model:['state':configuration.state])
        }
    }

    def configure = {
        
    }

}