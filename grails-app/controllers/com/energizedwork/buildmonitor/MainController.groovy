package com.energizedwork.buildmonitor


class MainController {

    Configuration configuration

    def index = {
        render(view:'index', model:['state':configuration.state])
    }

}