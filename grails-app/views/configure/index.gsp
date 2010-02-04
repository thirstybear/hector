<html>
    <head>
        <title>Build Monitor | Configuration</title>
		<meta name="layout" content="main" />
    </head>
    <body>
        <form id="configure-form" method="post" action="/configure/save">
            <label id="url-label" for="url">Hudson URL:</label>
            <input id="url" name="url" type="text" value="${config.url}" size="50"/>
            <input id="configure-form-save" value="Save" type="submit"/>
        </form>
    </body>
</html>