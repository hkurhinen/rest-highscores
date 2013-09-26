<%-- 
    Document   : index
    Author     : belvain
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>RestHighscores</title>
        <script type="text/javascript" src="http://code.jquery.com/jquery-1.10.2.min.js" ></script>
    </head>
    <body>
        <script>
        $(document).ready(function(){
           $.get( "game/games", function( data ) {
               for(var i = 0;i < data.length;i++){
                   $("#games").append("<option>"+data[i]+"</option>");
               }
            });
            
            $("#games").change(function() {
               $.get("game/"+$(this).val()+"/list/-1/html", function( data ) {
                   $("#scorediv").html(data);
                });
            });
            
        });
        </script>
        <div>
            <h3>RestHighscores succesfully deployed!</h3>
            <p>This service provides REST-api for game developers to save highscores and list them.</p>
            <h3>NOTE! this page is not meant to be an UI it's just for testing, you should implement your own UI.</h3>
            <p>Usage: </p>
            <p>To get highscores you need to make request to address sopulit.servebeer.com:424511/RestHighscores/game/{nameofthegame}/list/{number of records (or -1 if all)}</p>
            <p>This will give you highscores as an JSON object. Currenly there are values nickname, score and date(unixtime format) available in json object.</p>
            <p>If you want scores as an HTML list add /html to the end.</p>
        </div>
        <div>
            <select id="games">
                <option value="">Choose game..</option>
            </select>
            <div id="scorediv">  
            </div>
        </div>
        <div>
            <p>To add new scores you need to send POST call to address sopulit.servebeer.com:42511/RestHighscores/game/add</p>
            <p>and you need to include values gamename, nickname and score to the POST call. You don't need to care if your game is not listed yet as it will be done automatically.</p>
            <form action="game/add" method="POST">
                <p>Game:<input type="text" name="gamename" /></p>
                <p>Nickname:<input type="text" name="nickname" /></p>
                <p>Score:<input type="text" name="score" /></p>
                <input type="submit" value="add" />
            </form>
        </div>
    </body>
</html>
