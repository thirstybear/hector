<html>
    <head>
        <meta http-equiv="pragma" content="no-cache">
        <title><g:layoutTitle default="Build Monitor" /></title>
        <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <g:layoutHead />
    </head>
    <body class="${state?.name()}">
        <div id="body">
            <div id="content">
                <g:layoutBody />
            </div>
        </div>
    </body>
</html>