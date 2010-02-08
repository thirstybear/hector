package com.energizedwork.feed

class XmlDocumentRetriever {

    def getXml(String urlString) {
        def slurper = new XmlSlurper();
        def url = new URL(urlString)
        def xml
        
        url.withReader { reader ->
            xml = slurper.parse(reader)
        }
        xml
    }
}