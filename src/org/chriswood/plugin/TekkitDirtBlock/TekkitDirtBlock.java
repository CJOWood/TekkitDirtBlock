/*
 * Big thanks to the Votifier plugin for being open source.
 * Oh, and the Internet. It's pretty awesome.
 */

package org.chriswood.plugin.TekkitDirtBlock;


import java.util.logging.*;
import org.bukkit.plugin.java.JavaPlugin;

public class TekkitDirtBlock extends JavaPlugin {
	
	private static final Logger LOG = Logger.getLogger("TekkitDirtBlock");
	private TekkitDirtBlockHolder statData = new TekkitDirtBlockHolder();
	private TekkitDirtBlockTalk talker;
	
	public void onEnable(){
		TekkitDirtBlockConfig config = new TekkitDirtBlockConfig(this);
		getServer().getPluginManager().registerEvents(new TekkitDirtBlockListener(statData, config), this);
		
		//Initialize Talker...
		String host = config.getHost();
		int port = config.getPort();
		LOG.info("[TekkitDirtBlock] Starting on " + host + ":" + port + ".");
		if(host == "0.0.0.0")
			LOG.warning("[TekkitDirtBlock] Host is currently set to the default value. Please change this in the TekkitDirtBlock config.yml.");
		try {
				talker = new TekkitDirtBlockTalk(statData, host, port);
				talker.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void addData(String event, String user, String data, Double dataAmt)
	{
		statData.addData(event, user, data, dataAmt);
	}
	
	public void onDisable(){
		if(talker != null)
			talker.shutdown();
	}
}
