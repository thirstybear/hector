package com.energizedwork.buildmonitor.job

import com.energizedwork.buildmonitor.BuildMonitor


class BuildMonitorJob {

    BuildMonitor buildMonitor

    def timeout = 5000

    def execute() {
        buildMonitor.update()
    }

}