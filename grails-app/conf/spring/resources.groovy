import com.energizedwork.buildmonitor.Configuration
import com.energizedwork.buildmonitor.BuildMonitor
import com.energizedwork.buildmonitor.hudson.HudsonServer
import com.energizedwork.feed.FeedRetriever
import com.energizedwork.feed.XmlDocumentRetriever

beans = {

    feedRetriever(FeedRetriever) {
        configuration = ref('configuration')
    }

    hudsonServer(HudsonServer) {
        feedRetriever = ref('feedRetriever')
        xmlDocumentRetriever = ref('xmlDocumentRetriever')
    }

    buildMonitor(BuildMonitor) {
        hudsonServer = ref('hudsonServer')
    }

    configuration(Configuration)

    xmlDocumentRetriever(XmlDocumentRetriever)

}
