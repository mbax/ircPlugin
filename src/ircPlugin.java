/*
 * mbaxter's irc bot plugin for hey0's minecraft mod
 * todo: write better name
 * 
 * An IRC bot. This is the plugin file. 
 * It knows what to do when somebody speaks on the server
 * 
 * ALSO REQUIRES:
 * irc.properties (put this in the same directory as the mod)
 * Inside that file:
 * 	irc-host=
 * 	irc-channel=
 *  irc-name=
 *  irc-port
 *	irc-usercolor=
 *	irc-separator=
 *	irc-msg-enable=
 *	irc-debug=
 *	irc-charlimit=
 *	irc-echo= 
 * 
 * NOTE
 * This code is licensed under the GPL, as it partly uses
 * and includes pircbot code
 * See http://www.jibble.org/licenses/gnu-license.php
 * for more information
 */


import java.io.*;
import java.util.Properties;


public class ircPlugin extends Plugin {
	private final ircPluginListener listener=new ircPluginListener(this);
	public boolean goBot;
	public void enable() {
		ircProperties = new Properties();
		try { 
			ircProperties.load(new FileInputStream("irc.properties"));
		} catch (Exception e) {
		}
		
		ircHost = ircProperties.getProperty("irc-host");
		ircName = ircProperties.getProperty("irc-name");
		ircChannel = ircProperties.getProperty("irc-channel");
		ircUserColor = ircProperties.getProperty("irc-usercolor","1");
	
		ircSeparator= ircProperties.getProperty("irc-separator","<,>").split(",");
		
		try {
			ircCharLim = Integer.parseInt(ircProperties.getProperty("irc-charlimit","390"));
			if(ircCharLim < 391)
				ircCharLim = 390;
		}
		catch (NumberFormatException e)
		{
			ircCharLim=390;
		}
		String msgtemp = ircProperties.getProperty("irc-msg-enable");
		if(msgtemp!= null && msgtemp.equalsIgnoreCase("true"))
			ircMsg=true;
		else
			ircMsg=false;
		
		String echotemp = ircProperties.getProperty("irc-echo");
		if(echotemp!= null && echotemp.equalsIgnoreCase("true"))
			ircEcho=true;
		else
			ircEcho=false;
		
		try {
			ircPort = Integer.parseInt(ircProperties.getProperty("irc-port"));
		}
		catch (NumberFormatException e)
		{
			ircPort = 6667;
		}
		
		String ircDebugt=ircProperties.getProperty("irc-debug", "false");
		if(ircDebugt=="true")
			ircDebug=true;
		else
			ircDebug=false;
		goBot=true;
		bot=new ircBot(ircName,ircMsg,ircCharLim,ircUserColor,ircEcho,ircSeparator,this);
		//uncomment below to debug 
		if(ircDebug)bot.setVerbose(true);
        //System.out.println("host:"+ircHost);
        //System.out.println("ircName);
        //System.out.println(ircChannel);
		iGotKilled();
		
    }

    public void disable() {
    	if(bot!=null){
    		goBot=false;
    		bot.disconnect();
    		
    	}
    	
    }

    public void iGotKilled(){
    	if(goBot){
    		System.out.println("Connecting to "+ircChannel+" on "+ircHost+":"+ircPort+" as "+ircName);
    		try {
    			bot.connect(ircHost,ircPort);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		bot.joinChannel(ircChannel);
    	    bot.sendMessage(ircChannel,"Never fear, a minecraft bot is here!");
    	}
    }
    public void doMsg(String mess){
    	bot.sendMessage(ircChannel,mess);
    }
    
    public void initialize(){
    	etc.getLoader().addListener(PluginLoader.Hook.CHAT, listener, this, ircPluginListener.Priority.MEDIUM);
    	etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, ircPluginListener.Priority.MEDIUM);
    	etc.getLoader().addListener(PluginLoader.Hook.LOGIN, listener, this, ircPluginListener.Priority.MEDIUM);
    	etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, listener, this, ircPluginListener.Priority.MEDIUM);
    }
	private ircBot bot;
	private Properties ircProperties;
	private String ircName;
	private String ircHost;
	private String ircChannel;
	private String ircUserColor;
	private boolean ircMsg;
	private boolean ircEcho;
	private boolean ircDebug;
	private int ircCharLim;
	private int ircPort;
	private String[] ircSeparator;
}
