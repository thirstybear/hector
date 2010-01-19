package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.BuildState.*
import com.energizedwork.buildmonitor.hudson.HudsonServer

class BuildMonitor {

    BuildState state = failed
    HudsonServer hudsonServer
    List<Project> projects

    void update() {
        projects = hudsonServer.projects

        if(projects.find { it.state == failed }) {
            state = failed
        } else {
            state = passed
        }
    }

    List<Project> getFailedProjects() {
        projects.findAll {
            it.state == failed            
        }
    }

}
