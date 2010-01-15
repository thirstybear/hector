class UrlMappings {
    static mappings = {
      '/'(controller:'main', action:'index')
      "/$action" {
          controller = 'main'
          constraints {
              action(inList:['configure'])
          }
      }        
      "/$controller/$action?/$id?"{
	      constraints {
			 // apply constraints here
		  }
	  }
	  "500"(view:'/error')
	}
}
