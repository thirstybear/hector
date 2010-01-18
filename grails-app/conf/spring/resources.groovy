import com.energizedwork.buildmonitor.Configuration
import com.energizedwork.buildmonitor.BuildMonitor

beans = {

    buildMonitor(BuildMonitor)

    configuration(Configuration)

}

//app starts up
//request -> checks the configuration object
//says I'm configured or not
//css class="unconfigured"