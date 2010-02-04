package com.energizedwork.buildmonitor

class ConfigureController {

    BuildMonitor buildMonitor
    Configuration configuration

    def index = {
        render(view:'index', model:['config':configuration])
    }

    def save = {
        configuration.url = params.url
        buildMonitor.update()
        redirect(controller: 'main', model:[forcereload:true])
    }

}
