/*
 * mbaxter's irc bot plugin for hey0's minecraft mod
 * todo: write better name
 * 
 * An IRC bot. This is the plugin file. 
 * It does commands and doesn't answer to anybody
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
 * This code is licensed under the GPL2, as it partly uses
 * and includes pircbot code
 * See http://www.jibble.org/licenses/gnu-license.php
 * for more information
 */


import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ircPlugin extends Plugin {
	private String version="1.1";
	private final ircPluginListener listener=new ircPluginListener(this);
	private ircBot bot;
	private String ircName,ircHost,ircChannel,ircUserColor;
	private boolean ircMsg,ircEcho,ircDebug;
	private int ircCharLim,ircPort;
	private String[] ircSeparator;
	public boolean goBot;
	public Logger log;
	private Object adminsLock = new Object();
	private ArrayList<ircAdmin> admins;
	public void enable() {
		log=Logger.getLogger("Minecraft");
		try {
			PropertiesFile ircProperties = new PropertiesFile("irc.properties");
			ircHost = ircProperties.getString("irc-host","localhost");
			ircName = ircProperties.getString("irc-name","aMinecraftBot");
			ircChannel = ircProperties.getString("irc-channel","#minecraftbot");
			ircUserColor = ircProperties.getString("irc-usercolor","f");
			ircSeparator= ircProperties.getString("irc-separator","<,>").split(",");
			ircCharLim = ircProperties.getInt("irc-charlimit",390);
			ircMsg=ircProperties.getBoolean("irc-msg-enable",false);
			ircEcho = ircProperties.getBoolean("irc-echo",false);
			ircPort = ircProperties.getInt("irc-port",6667);
			ircDebug = ircProperties.getBoolean("irc-debug",false);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception while reading from irc.properties", e);
		}
		goBot=true;
		bot=new ircBot(ircName,ircMsg,ircCharLim,ircUserColor,ircEcho,ircSeparator,this); 
		if(ircDebug)bot.setVerbose(true);
		iGotKilled();//lazy name for a function.
		loadAdmins();
		log.log(Level.INFO,"ircPlugin started, version "+version);
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
	public void loadAdmins(){
		String location="ircAdmins.txt";
		if (!new File(location).exists()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(location);
                writer.write("#Add IRC admins here.\r\n");
                writer.write("#The format is:\r\n");
                writer.write("#NAME:PASSWORD:ACCESSLEVEL\r\n");
                writer.write("#Access levels: 2=kick,ban 3=everything");
                writer.write("#Example:\r\n");
                writer.write("#notch:iminurbox:3\r\n");
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception while creating " + location, e);
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Exception while closing writer for " + location, e);
                }
            }
        }
		synchronized (adminsLock) {
            admins = new ArrayList<ircAdmin>();
            try {
                Scanner scanner = new Scanner(new File(location));
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("#") || line.equals("") || line.startsWith("﻿")) {
                        continue;
                    }
                    String[] split = line.split(":");
                    if(split.length!=3)
                    	continue;
                    ircAdmin admin=new ircAdmin(split[0],split[1],Integer.parseInt(split[2]));
                    admins.add(admin);
                }
                scanner.close();
            } catch (Exception e) {
                log.log(Level.SEVERE, "Exception while reading " + location + " (Are you sure you formatted it correctly?)", e);
            }
        }
	}
	public boolean auth(String sender,String name,String pass,String host){
		boolean success=false;
		synchronized(adminsLock){
			for(ircAdmin admin:admins){
				if(admin!=null && admin.getUsername().equalsIgnoreCase(name) && admin.auth(pass, host)){
					log.log(Level.INFO,"IRC admin "+admin.getUsername()+" logged in  ("+host+")");
					success=true;
				}
				else{
					log.log(Level.INFO,"IRC admin failed login. user["+name+"] pass["+pass+"] nick["+sender+"] host["+host+"]");
				}
			}
		}
		return success;
	}
	public boolean ircCommand(String host,String[] command){
		int lvl=0;
		String adminName="";
		synchronized(adminsLock){
			for(ircAdmin admin:admins){
				if(admin!=null && admin.getHostname().equals(host)){
					lvl=admin.getLevel();
					adminName=admin.getUsername();
				}
			}
		}
		if(command[0].charAt(0)=='!'){
			command[0]=command[0].substring(1);
		}
		if(lvl==0 || (lvl==2 && !(command[0].equalsIgnoreCase("kick") || command[0].equalsIgnoreCase("ban"))  )  ){
			return false;
		}
		String commands=etc.combineSplit(0, command, " ");
		if(etc.getInstance().parseConsoleCommand(commands, etc.getMCServer())){
			return true;
		}
		etc.getServer().useConsoleCommand(commands);
		log.log(Level.INFO,"IRC admin "+adminName+"("+host+") used command: "+commands);
		return true;
	}
}
