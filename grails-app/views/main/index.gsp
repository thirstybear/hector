<html>
<head>
  <meta name="layout" content="main"/>
  <!--<meta http-equiv="refresh" content="2"/>-->
  <g:javascript library="audio-player"/>
  <script type="text/javascript">
    AudioPlayer.setup("/player.swf", {
      autostart: "yes",
      width: 0
    });
  </script>  
</head>
<body>
<g:if test="${failedProjects}">
  <ol>
    <g:each in="${failedProjects}" var="failedProject">
      <li class="failure">${failedProject.name}</li>
    </g:each>
  </ol>

  <div id="audioplayer_1"></div>
  <script type="text/javascript">
    AudioPlayer.embed("audioplayer_1", {
      soundFile: "/alarm_1.mp3"
    });

  </script>
</g:if>

</body>
</html>