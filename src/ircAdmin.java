/*
 * mbaxter's irc bot plugin for hey0's minecraft mod
 * long title, amazing results?
 *
 * This is a class for tracking admins
 * 
 * NOTE 
 * This code is licensed under the GPL2, as it partly uses
 * and includes pircbot code
 * See http://www.jibble.org/licenses/gnu-license.php
 * for more information
 * 
 */

public class ircAdmin {
	private String username,password,hostname;
	private int lvl;
	public ircAdmin(String name,String pass,int level){
		username=name;
		password=pass;
		hostname="";
		lvl=level;
	}
	public String getUsername(){
		return username;
	}
	public String getHostname(){
		return hostname;
	}
	public int getLevel(){
		return lvl;
	}
	public boolean auth(String pass, String host){
		if(password.equals(pass)){
			hostname=host;
			return true;
		}
		return false;
	}
}
