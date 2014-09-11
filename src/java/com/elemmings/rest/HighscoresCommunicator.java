
package com.elemmings.rest;

import com.elemmings.core.Highscores;
import com.sun.jersey.api.json.JSONWithPadding;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author belvain
 */
@Path("/")
public class HighscoresCommunicator {

    @Context
    private UriInfo context;

    public HighscoresCommunicator() {
    }

    @GET
    @Path("test")
    @Produces("text/html")
    public String getHtml() {
        return "<h3>Hello World!</h3>";
    }
    
    @GET
    @Path("/{gamename}/list/{count}")
    @Produces(MediaType.APPLICATION_JSON)
    public String listScores(@PathParam("gamename") String gamename, @PathParam("count") String count){
        int top = Integer.parseInt(count);
        return Highscores.mongo.getJsonScores(gamename,top);
    }
    
    @GET
    @Path("/{gamename}/list/{count}/html")
    @Produces(MediaType.TEXT_HTML)
    public String listScoresHtml(@PathParam("gamename") String gamename, @PathParam("count") String count){
        int top = Integer.parseInt(count);
        return Highscores.mongo.getHtmlScores(gamename,top);
    }
    
    @GET
    @Path("/{gamename}/key")
    @Produces(MediaType.TEXT_PLAIN)
    public String getKey(@PathParam("gamename") String gamename){
	if(!Highscores.mongo.hasKeys(gamename)){
	    Highscores.security.generateRSAKeys(gamename);
	}
	return Highscores.mongo.getPublicKey(gamename);
    }
    
    @GET
    @Path("/games")
    @Produces("application/x-javascript")
    public JSONWithPadding listGames(@QueryParam("callback") String callback){
	return new JSONWithPadding(Highscores.mongo.getGames(),callback);
    }
    
    @POST
    @Path("/add")
    public void setScore(@FormParam("gamename") String gamename, @FormParam("nickname") String nickname, @FormParam("score") String score, @FormParam("codedmsg") String codedMsg){
        System.out.println("Checking if score is legit.");
	System.out.println(gamename);
	System.out.println(nickname);
	System.out.println(score);
	System.out.println(codedMsg);
	if(codedMsg == null || codedMsg.isEmpty()){
	    Highscores.mongo.addScore(gamename, nickname, Long.parseLong(score), false);
	}else{
	    System.out.println("Verifying..");
	    if(Highscores.security.isVerified(nickname, score, gamename, codedMsg)){
		Highscores.mongo.addScore(gamename, nickname, Long.parseLong(score), true);
	    }else{
		System.out.println("Verification failed.");
		Highscores.mongo.addScore(gamename, nickname, Long.parseLong(score), false);
	    }  
	}

    }
    
   /*@GET //TODO change to POST or PUT function because exposing this as GET causes cheating to be easy.
    @Path("/{gamename}/add/{nickname}/{score}")
    @Produces("text/plain")
    public String setScore(@PathParam("gamename") String gamename, @PathParam("nickname") String nickname, @PathParam("score") String score){
        Highscores.mongo.addScore(gamename, nickname, Long.parseLong(score));
        return "done";
    }*/
    

}
