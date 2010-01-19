package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*
import com.energizedwork.buildmonitor.hudson.HudsonServer

class BuildMonitor {

    BuildState state = failed
    HudsonServer hudsonServer

    void update() {
        List<Project> projects = hudsonServer.projects

        println projects

        if(projects.find { it.state == failed }) {
            state = failed
        } else {
            state = passed
        }
    }

}
