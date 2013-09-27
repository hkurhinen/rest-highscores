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
	    
	    $("#requestkey").click(function(){
		var gamename = $("#gamekeyname").val();
		$.get("game/"+gamename+"/key", function( data ) {
                   $("#keyarea").html(data);
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
            <p>To add new scores you need to send POST request to address sopulit.servebeer.com:42511/RestHighscores/game/add</p>
            <p>You need to include POST parameters "gamename", "nickname", and "sccore". If you want your results to be shown as verified
	    <br/>you need also send url parameter "codedmsg" which should contain gamename+score (like "matti1234") encrypted with your publickey.</p>
	    <p>Code examples:</p>
	    <h3>Java</h3>
	    <pre><code>
	    public String getBase64CodedMsg(String nickname, String score, String pubkey){
		String msg = nickname+score;
		byte[] bytes = Base64.decodeBase64(pubkey);
		String codedmsg = "";
		try {
		    PublicKey publickey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
		    Cipher cipher = Cipher.getInstance("RSA");
		    cipher.init(Cipher.ENCRYPT_MODE, publickey);
		    byte[] codedBytes = cipher.doFinal(msg.getBytes());
		    codedmsg = Base64.encodeBase64String(codedBytes);
		} catch (Exception ex) {
		   System.err.println("Something went wrong.");
		}
		return codedmsg;
	    }
	    </code></pre>
	    <h3>C#</h3>
	    <pre><code>
	    Coming soon!
	    </code></pre>
	    <h3>Python</h3>
	    <pre><code>
	    Coming soon!
	    </code></pre>
	    <p>If you have done verification with some other language and are willing to share, please contact me.</p>
	    
	    <p>With this form you can test sending unverified results.</p>
	    <form action="game/add" method="POST">
                <p>Game:<input type="text" name="gamename" /></p>
                <p>Nickname:<input type="text" name="nickname" /></p>
                <p>Score:<input type="text" name="score" /></p>
                <input type="submit" value="add" />
            </form>
        </div>
	<div>
	    <p>This is only for testing, once I have done page with login system, it will be used to get keys.</p>
	    <p><input id="gamekeyname" type="text" /><button id="requestkey">GET PUBKEY</button></p>
	    <pre id="keyarea">
	    </pre>
	</div>
    </body>
</html>
