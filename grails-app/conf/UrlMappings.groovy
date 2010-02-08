class UrlMappings {
    static mappings = {

        '/'(controller: 'main')

        "/hudson/job/$projectName/$buildNumber/api/xml" (controller:'fixture', action:'projectBuildXml')

        "/$controller/$action?/$id?" {
            constraints {}
        }

        "500"(view: '/error')

    }
}
