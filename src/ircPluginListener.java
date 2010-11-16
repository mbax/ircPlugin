
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
    	return false;
    }
    public void onLogin(Player player){
    	ircp.doMsg(player.getName()+" has logged in");
    }
    public void onDisconnect(Player player){
    	ircp.doMsg(player.getName()+" has left the server");
    }
}
