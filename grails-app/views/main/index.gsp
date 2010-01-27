<html>
    <head>
		<meta name="layout" content="main" />
        <meta http-equiv="refresh" content="2" />
    </head>
    <body>
        <g:if test="${failedProjects}">
            <ol>
            <g:each in="${failedProjects}" var="failedProject">
                <li class="failure">${failedProject.name}</li>
            </g:each>
            </ol>

          <embed src='fail.wav' hidden=true autostart=true loop=false>
        </g:if>

    </body>
</html>