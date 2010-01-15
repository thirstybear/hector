class UrlMappings {
    static mappings = {

        '/'(controller: 'main')

        "/$controller/$action?/$id?" {
            constraints {}
        }

        "500"(view: '/error')

    }
}
