<html>
<head>
  <meta name="layout" content="main"/>  
  <g:javascript library="jquery-1.4.1.min" />
  <g:javascript library="jquery.cookies.2.2.0.min" />
  <g:javascript library="application" />
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
      soundFile: "/fail.mp3"
    });

  </script>
</g:if>

  <div id="playsound">
    <a href="#" onClick="toggleSound()">
      <img id="speaker_icon" border="0" width="50px"/>
    </a>
  </div>
</body>
</html>