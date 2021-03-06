package com.rbruno.irc.commands;

import com.rbruno.irc.Server;
import com.rbruno.irc.templates.Channel;
import com.rbruno.irc.templates.Request;

public class Part extends Command {

	public Part() {
		super("PART", 1);
	}
	
	@Override
	public void execute(Request request) throws Exception {
		String[] channels = request.getArgs()[0].split(",");
		for (String channelName : channels) {
			Channel channel = Server.getServer().getChannelManger().getChannel(channelName);
			channel.removeClient(request.getConnection().getClient());
			request.getConnection().getClient().removeChannel(channel);

		}

	}

}
