package com.energizedwork.buildmonitor

class ConfigureController {

    Configuration configuration

    def index = { }

    def save = {
        configuration.url = params.url

        redirect(controller: 'main')
    }

}
