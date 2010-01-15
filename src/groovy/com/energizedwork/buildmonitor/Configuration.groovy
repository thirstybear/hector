package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.ConfigurationState.*

class Configuration {

    ConfigurationState state = unconfigured
    String url

    void setUrl(String url) {
        this.url = url
        this.state = url ? configured : unconfigured
    }

}