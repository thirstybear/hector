package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.hudson.HudsonServer.*

class FixtureController {

    Configuration configuration

    private final String rssFailureTemplate =

'''<?xml version="1.0" encoding="UTF-8"?>
<feed xmlns="http://www.w3.org/2005/Atom">
    <title>All last builds only</title>
    <link type="text/html" rel="alternate" href="http://some.build.server/hudson/"></link>
    <updated>2010-01-07T22:00:52Z</updated>
    <author>
        <name>Hudson Server</name>
    </author>
    <id>urn:uuid:903deee0-7bfa-11db-9fe1-0800200c9a66</id>
    <entry>
        <title>ProjectName #1 (SUCCESS)</title>
        <link type="text/html" rel="alternate"
              href="http://some.build.server/hudson/job/ProjectName/1/"></link>
        <id>tag:hudson.dev.java.net,2008:http://some.build.server/hudson/job/ProjectName/</id>
        <published>2010-01-07T22:00:52Z</published>
        <updated>2010-01-07T22:00:52Z</updated>
    </entry>
</feed>'''


    def hudsonRss = {
        String projectState

        if (params.result == 'success') {
            projectState = SUCCESS
        } else {
            projectState = FAILURE
        }
        render(view: 'hudsonRss', model: [projectName: 'myProject', projectState: projectState], contentType:'application/atom+xml;charset=UTF-8')
    }

    def reset = {
        configuration.reset()
        render text:'Configuration RESET'
    }

}