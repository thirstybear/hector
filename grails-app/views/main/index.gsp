<html>
    <head>
		<meta name="layout" content="main" />
    </head>
    <body>
        <g:if test="${failedProjects}">
            <ol>
            <g:each in="${failedProjects}" var="failedProject">
                <li class="failure">${failedProject.name}</li>
            </g:each>
            </ol>
        </g:if>
    </body>
</html>