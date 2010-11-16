/*
 * mbaxter's irc bot plugin for hey0's minecraft mod
 * long title, amazing results?
 *
 * An IRC Bot. This is not the plugin, it's the other half.
 * Handles messages from the channel and has the 
 * sendMessage command used by the plugin
 * 
 * NOTE 
 * This code is licensed under the GPL, as it partly uses
 * and includes pircbot code
 * See http://www.jibble.org/licenses/gnu-license.php
 * for more information
 * 
 */

import java.util.logging.Logger;

import org.jibble.pircbot.*;

public class ircBot extends PircBot {
    ircPlugin irc;
	Logger log;
    public ircBot(String mah_name,boolean msgenabled,int charlim,String usercolor,boolean echo,String[] sep,ircPlugin ip) {
        this.setName(mah_name);
        this.setAutoNickChange(true);
        ircMsg=msgenabled;
        ircCharLim = charlim;
        ircUserColor = usercolor;
        ircEcho = echo;
        ircSeparator=sep;
        log = Logger.getLogger("Minecraft");
        irc=ip;
    }
    
    public void onDisconnect(){
    	irc.iGotKilled();
    }
    
    public void onMessage(String channel, String sender,
                       String login, String hostname, String message) {
    	String[] parts=message.split(" ");
        if (message.equalsIgnoreCase("!help")) {
            sendMessage(channel, sender + ": I am here to set you free.");
        }
        else if (message.equalsIgnoreCase("!players")) {
        	String curPlayers = "";
        	int cPlayers=0;
        	for (Player p : etc.getServer().getPlayerList()) {
      		  if (p != null) {
      			  if(curPlayers==""){
      				  curPlayers+=p.getName();
      			  }
      			  else{
      				  curPlayers+=", "+p.getName();
      			  }
      			  cPlayers++;
      		  }
            }
        	if(curPlayers=="")
        		sendMessage(channel,"No players online.");
        	else
        		sendMessage(channel,"Players ("+ cPlayers +" of "+ etc.getInstance().getPlayerLimit() + "):" + curPlayers);
        }
        /*else if (message.equalsIgnoreCase("!admins")) {
        	String curAdmins = "Admins: ";
        	for (Player p : etc.getServer().getPlayerList()) {
      		  if (p != null && (p.isInGroup("admins")||p.isInGroup("srstaff"))) {
      			  if(curAdmins=="Admins: "){
      				  curAdmins+=p.getName();
      			  }
      			  else{
      				  curAdmins+=", "+p.getName();
      			  }
      		  }
            }
        	if(curAdmins=="Admins: ")
        		sendMessage(channel,"No admins online. Find one on #joe.to or #minecraft");
        	else
        		sendMessage(channel,curAdmins);
        }*/
        else if (ircMsg && parts[0].equalsIgnoreCase("!msg")){
            String damessage = "";
            for(int $x=1;$x<parts.length;$x++)
            {
              damessage+=" "+parts[$x];
            }
            doMsg(channel,sender,damessage);
        }
        else if (!ircMsg){
        	doMsg(channel,sender,message);
        }
        
    }
    public void doMsg(String channel, String sender, String message){
    	if(addMsg(message,sender))
        {
          if(ircEcho)
        	  sendMessage(channel,"[IRC] <"+sender+">"+message);
        }
        else
        {
          sendMessage(channel,sender+": Your message was too long. The limit's " + ircCharLim + " characters");
        }
    }
    public boolean addMsg(String thenewmsg,String theuser)
    {
      String combined=ircSeparator[0]+"§"+ircUserColor+theuser+"§f"+ircSeparator[1]+thenewmsg;
      if(combined.length() > ircCharLim)
      {
        return false;
      }
      else
      {
    	  log.info("IRC:<"+theuser+"> "+thenewmsg);
    	  for (Player p : etc.getServer().getPlayerList()) {
    		  if (p != null) {
    			  p.sendMessage(combined);
    		  }
          }
    	  return true;
      }
      
    }
    private boolean ircMsg;
    private boolean ircEcho;
    private int ircCharLim;
    private String ircUserColor;
    private String[] ircSeparator;
}
