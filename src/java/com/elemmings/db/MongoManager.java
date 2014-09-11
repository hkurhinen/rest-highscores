package com.elemmings.db;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author belvain
 */
public class MongoManager {
    
    //TODO: add authentication to MongoDB
    //Even not really needed in my environment
    //since mongo is accessible only in LAN anyway
    
    public static final String DBHOST = "192.168.11.6"; //TODO read mongo settings from config
    public static final int DBPORT = 27017;
    public static final String DBNAME = "games";
    public static final String AUTHDBNAME = "keys";
    
    public MongoClient mongo;
    public DB defaultdb;
    public DB authDB;
    
    public MongoManager() throws UnknownHostException{
            mongo = new MongoClient(DBHOST, DBPORT);
            defaultdb = mongo.getDB(DBNAME);
	    authDB = mongo.getDB(AUTHDBNAME);
    }
    
    public DBCursor getHighscores(String gamename, int count){
        DBCollection game = defaultdb.getCollection(gamename);
        DBCursor cursor = null;
        if(count == -1){
            cursor = game.find().sort(new BasicDBObject("score", -1));
        }else{
            cursor = game.find().sort(new BasicDBObject("score", -1)).limit(count);
        }
        return cursor;
    }
    
    public String getJsonScores(String gamename, int count){
        String jsonData ="[";
        DBCursor cursor = this.getHighscores(gamename, count);
        while (cursor.hasNext()) {
            DBObject o = cursor.next();
            jsonData += o.toString();
            jsonData += ",";
	}
        jsonData = jsonData.substring(0, jsonData.lastIndexOf(","));
        jsonData += "]";
        return jsonData;
    }
    
    public String getHtmlScores(String gamename, int count){
        DBCursor cursor = this.getHighscores(gamename, count);
        String html = "<ul>";
        while (cursor.hasNext()) {
            DBObject o = cursor.next();
	    if(o.get("verified").equals("YES")){
		html += "<li>"+o.get("name")+":"+o.get("score")+"</li>";
	    }else{
		html += "<li>"+o.get("name")+":"+o.get("score")+"(unverified)</li>";
	    }
	}
        html += "</ul>";
        return html;
    }
    
    public String getGames(){
        String games = "[";
        Set<String> collections = defaultdb.getCollectionNames();
        for(String game : collections){
            if(!game.equals("system.indexes")){ //remove some weird system.indexes collection from results
                games += "\""+game+"\",";
            }
        }
        games = games.substring(0, games.lastIndexOf(","));
        games += "]";
        return games;
    }
    
    public void addScore(String gamename, String nickname, long score, boolean verified){ //TODO think if int is enough for scores?
        DBCollection game = defaultdb.getCollection(gamename);
        BasicDBObject highscore = new BasicDBObject();
	highscore.put("name", nickname);
	highscore.put("score", score);
	highscore.put("createdDate", System.currentTimeMillis() / 1000L);
	if(verified){
	    	highscore.put("verified", "YES");
	}else{
	   	highscore.put("verified", "NO");
	}
        game.insert(highscore);
    }
    
    public boolean hasKeys(String gamename){
	boolean keyfound = false;
	DBCollection keys = authDB.getCollection("keycollection");
	DBCursor cursor = keys.find();
	while(cursor.hasNext()){
	    if(cursor.next().get("gamename").equals(gamename)){
		keyfound = true;
	    }
	}
	return keyfound;
    }
    
    public String getPublicKey(String gamename){
	DBCollection keys = authDB.getCollection("keycollection");
	BasicDBObject search = new BasicDBObject();
	search.put("gamename", gamename);
	DBObject o = keys.findOne(search);
	return o.get("pubkey").toString();
	
    }
    
    public String getPrivateKey(String gamename){
	DBCollection keys = authDB.getCollection("keycollection");
	BasicDBObject search = new BasicDBObject();
	search.put("gamename", gamename);
	DBObject o = keys.findOne(search);
	return o.get("privkey").toString();
	
    }
    
    
    public void saveKey(String gamename, String pubkey, String privkey){
	DBCollection keys = authDB.getCollection("keycollection");
	BasicDBObject key = new BasicDBObject();
	key.put("gamename", gamename);
	key.put("pubkey", pubkey);
	key.put("privkey", privkey);
	keys.insert(key);
    }
    
    
}
