package com.energizedwork.buildmonitor

import static com.energizedwork.web.support.HttpConstants.*

import static com.energizedwork.buildmonitor.BuildState.failed
import static com.energizedwork.buildmonitor.ConfigurationState.unconfigured

import com.energizedwork.buildmonitor.BuildMonitor
import com.energizedwork.buildmonitor.BuildState
import com.energizedwork.buildmonitor.Configuration
import java.text.DateFormat
import java.text.SimpleDateFormat
import org.apache.commons.lang.time.DateUtils

class MainController {

    BuildMonitor buildMonitor
    Configuration configuration
    
    DateFormat dateFormatterImpl    

    def index = {
        if (configuration.state == unconfigured) {
            redirect(controller: 'configure')
        } else {
            setLastModified(buildMonitor.lastUpdate)
            if (buildMonitor.hasChanged(ifModifiedSince)) {
                renderPage()
            } else {
                replyNotModified()
            }
        }
    }

    private void renderPage() {
        BuildState state = buildMonitor.state
        Map model = ['state': state]
        if (state == failed) {
            model.failedProjects = buildMonitor.failedProjects
        }

        render(view: 'index', model: model)
    }

    private void replyNotModified() {
        response.sendError NOT_MODIFIED
    }

    private Date getIfModifiedSince() {
        Date result
        String ifModifiedSince = request.getHeader(IF_MODIFIED_SINCE)

        if (ifModifiedSince) {
            result = dateFormatter.parse(ifModifiedSince);
        }

        return result
    }
        
    private void setLastModified(Date lastModified) {
        if(lastModified) {
            Date lastUpdate = DateUtils.round(lastModified, Calendar.SECOND)
            String dateString = dateFormatter.format(lastUpdate)
            response.addHeader LAST_MODIFIED, dateString
        }
    }

    private DateFormat getDateFormatter() {
        if(!dateFormatterImpl) {
            dateFormatterImpl = new SimpleDateFormat(ISO_DATE_FORMAT)
        }
        return dateFormatterImpl
    }

}

    