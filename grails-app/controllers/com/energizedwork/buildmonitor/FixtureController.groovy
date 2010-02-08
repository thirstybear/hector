package com.energizedwork.buildmonitor

import static com.energizedwork.buildmonitor.hudson.HudsonServer.*

class FixtureController {

    static String feedState

    Configuration configuration
    BuildMonitor buildMonitor

    def hudsonRss = {        
        StringBuilder buffer = new StringBuilder()

        buffer << g.render(template: 'hudsonRssHeader')
        renderHudsonRssEntries params.pass, SUCCESS, buffer
        renderHudsonRssEntries params.fail, FAILURE, buffer
        buffer << g.render(template: 'hudsonRssFooter')

        render(text: buffer.toString(), contentType:'application/atom+xml;charset=UTF-8')
    }

    def reset = {
        feedState = null
        configuration.reset()
        render text:'Configuration RESET'
    }

    def feed = {
        StringBuilder buffer = new StringBuilder()

        buffer << g.render(template: 'hudsonRssHeader')
        if(feedState) {
            renderHudsonRssEntries 'project1', feedState, buffer
        }
        buffer << g.render(template: 'hudsonRssFooter')

        render(text: buffer.toString(), contentType:'application/atom+xml;charset=UTF-8')
    }

    def pass = {
        feedState = SUCCESS
        render text:"/fixture/feed will now return $feedState"
    }

    def fail = {
        feedState = FAILURE
        render text:"/fixture/feed will now return $feedState"
    }

    private Closure renderHudsonRssEntries = { String commaSeparatedNames, String state, StringBuilder buffer ->
        commaSeparatedNames = commaSeparatedNames ?: ''
        List<String> projectNames = commaSeparatedNames.tokenize(',')

        projectNames.each { String projectName ->
            buffer << g.render(template: 'hudsonRssEntry', model: [projectName: projectName, projectState: state])
        }
    }


    def projectBuildXml = {
        render (view:'failingBuild', contentType:'application/xml')
    }

}