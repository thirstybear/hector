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
        StringBuilder buffer = new StringBuilder()

        buffer << g.render(template: 'hudsonRssHeader')
        renderHudsonRssEntries params.pass, SUCCESS, buffer
        renderHudsonRssEntries params.fail, FAILURE, buffer
        buffer << g.render(template: 'hudsonRssFooter')

        render(text: buffer.toString(), contentType:'application/atom+xml;charset=UTF-8')
    }

    def reset = {
        configuration.reset()
        render text:'Configuration RESET'
    }

    private Closure renderHudsonRssEntries = { String commaSeparatedNames, String state, StringBuilder buffer ->
        commaSeparatedNames = commaSeparatedNames ?: ''
        List<String> projectNames = commaSeparatedNames.tokenize(',')

        projectNames.each { String projectName ->
            buffer << g.render(template: 'hudsonRssEntry', model: [projectName: projectName, projectState: state])
        }                        
    }

}