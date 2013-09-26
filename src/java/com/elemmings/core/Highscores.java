
package com.elemmings.core;

import com.elemmings.db.MongoManager;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author belvain
 */
public class Highscores implements ServletContextListener{

    public static MongoManager mongo;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Connecting to MongoDB...");
        try {
            mongo = new MongoManager();
        } catch (UnknownHostException ex) {
            System.err.println("Could not connect to MongoDB");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Closing RestHighscores");
    }
    
}
