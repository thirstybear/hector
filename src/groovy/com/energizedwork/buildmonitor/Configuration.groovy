package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.ConfigurationState.*

class Configuration {

    ConfigurationState state = unconfigured
    URL url

    void setUrl(URL url) {
        this.url = url
        this.state = url ? configured : unconfigured
    }

}