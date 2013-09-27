
package com.elemmings.security;

import com.elemmings.core.Highscores;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author Belvain
 */
public class SecurityManager {
    
    public SecurityManager(){
	
    }
    public void generateRSAKeys(String gamename){
	    try {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		kpg.initialize(512);
		KeyPair kp = kpg.genKeyPair();
		Highscores.mongo.saveKey(gamename, Base64.encodeBase64String(kp.getPublic().getEncoded()), Base64.encodeBase64String(kp.getPrivate().getEncoded()));

	    } catch (NoSuchAlgorithmException ex) {
		Logger.getLogger(SecurityManager.class.getName()).log(Level.SEVERE, null, ex);
	    }   
    }
    
    public boolean isVerified(String nickname, String score, String gamename, String codedData){
	boolean legit = false;
	String isExpected = nickname+score;
	String priv64key = Highscores.mongo.getPrivateKey(gamename);
	byte[] bytes = Base64.decodeBase64(priv64key);
	String asd = codedData.replace(" ", "+");
	byte[] msgdata = Base64.decodeBase64(asd);
	System.out.println(msgdata);
	try {	
	    PrivateKey privkey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
	    Cipher cipher = Cipher.getInstance("RSA");
	    cipher.init(Cipher.DECRYPT_MODE, privkey);
	    byte[] encodedData = cipher.doFinal(msgdata);
	    String encodedmsg = new String(encodedData);
	    if(isExpected.equals(encodedmsg)){
		legit = true;
	    }
	} catch (NoSuchAlgorithmException ex) {
	    Logger.getLogger(SecurityManager.class.getName()).log(Level.SEVERE, null, ex);
	} catch (InvalidKeySpecException ex) {
	    Logger.getLogger(SecurityManager.class.getName()).log(Level.SEVERE, null, ex);
	} catch (NoSuchPaddingException ex) {
	    Logger.getLogger(SecurityManager.class.getName()).log(Level.SEVERE, null, ex);
	} catch (InvalidKeyException ex) {
	    Logger.getLogger(SecurityManager.class.getName()).log(Level.SEVERE, null, ex);
	} catch (IllegalBlockSizeException ex) {
	    Logger.getLogger(SecurityManager.class.getName()).log(Level.SEVERE, null, ex);
	} catch (BadPaddingException ex) {
	    Logger.getLogger(SecurityManager.class.getName()).log(Level.SEVERE, null, ex);
	}
	return legit;
    }
    
    
}
