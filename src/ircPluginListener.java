/*
 * mbaxter's irc bot plugin for hey0's minecraft mod
 * long title, amazing results?
 *
 * This is the listener, it listens.
 * 
 * NOTE 
 * This code is licensed under the GPL2, as it partly uses
 * and includes pircbot code
 * See http://www.jibble.org/licenses/gnu-license.php
 * for more information
 * 
 */

public class ircPluginListener extends PluginListener {
	private ircPlugin ircp;
	public ircPluginListener(ircPlugin daddy){
		ircp=daddy;
	}
	public boolean onChat(Player player, String message) {
		ircp.doMsg("<"+player.getName()+">"+" "+message);
    	return false;
    }

    public boolean onCommand(Player player, String[] split) {
    	if (split[0].equalsIgnoreCase("/me"))
    	{
    		String message = "";
            for(int $x=1;$x<split.length;$x++)
            {
              message+=" "+split[$x];
            }
            ircp.doMsg("* "+ player.getName()+message);
    	}
    	if(split[0].equalsIgnoreCase("/ircrefresh") && player.canUseCommand("/ircrefresh")){
    		ircp.loadAdmins();
    		player.sendMessage(Colors.Rose+"IRC admins reloaded");
    	}
    	return false;
    }
    public void onLogin(Player player){
    	ircp.doMsg(player.getName()+" has logged in");
    }
    public void onDisconnect(Player player){
    	ircp.doMsg(player.getName()+" has left the server");
    }
}
