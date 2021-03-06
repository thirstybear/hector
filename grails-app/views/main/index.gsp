<%@ page import="com.energizedwork.buildmonitor.hudson.HudsonServer" %>
<html>
<head>
  <meta name="layout" content="main"/>  
  <g:javascript library="jquery-1.4.1.min" />
  <g:javascript library="jquery.cookies.2.2.0.min" />
  <g:javascript library="application" />
  <g:javascript library="audio-player"/>
  <script type="text/javascript">
    AudioPlayer.setup("/player.swf", {
      width: 0
    });
  </script>  
</head>
<body>
  <g:if test="${failedProjects}">
    <g:each in="${failedProjects}" var="failedProject">
      <div class="failedProject">
        <div class="projecttitle">${failedProject.name}</div>
        <div class="duration"><hector:timeSince value="${failedProject.published}"/></div>
        <div class="changeset">
          <g:each in="${failedProject.owners}" var="owner">
            <div class="change">
              <img src="avatars/${owner}.jpg" onError="this.src='avatars/mystery.png'" title="${owner}"/>
              <div class="owner">${owner}</div>
            </div>
          </g:each>
        </div>
      </div>
    </g:each>

    <div id="audioplayer_1">
      <script type="text/javascript">
        var playSound = $.cookies.get('playsounds');
        if (playSound == null) {
          playSound = "no";
        }

        AudioPlayer.embed("audioplayer_1", {
          soundFile: "/fail.mp3",
          autostart: playSound
        });
      </script>
    </div>
  </g:if>

  <div id="toolbar">
    <div id="playsound">
      <a href="#" onClick="toggleSound()">
        <img id="speaker_icon" border="0" width="50px" alt="Toggle Sound On/Off" title="Toggle Sound On/Off"/>
      </a>
    </div>
  </div>
</body>
</html>
