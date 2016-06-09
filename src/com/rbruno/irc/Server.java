package com.rbruno.irc;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.rbruno.irc.commands.Command;
import com.rbruno.irc.config.Config;
import com.rbruno.irc.logger.Logger;
import com.rbruno.irc.manage.ChannelManager;
import com.rbruno.irc.manage.ClientManager;
import com.rbruno.irc.net.Connection;
import com.rbruno.irc.reply.Reply;
import com.rbruno.irc.templates.Client;
import com.rbruno.irc.util.Utilities;

public class Server implements Runnable {

	private static final String VERSION = "v0.10-SNAPSHOT";

	private static Server server;

	private boolean running;
	private ServerSocket serverSocket;

	private Config config;
	private ClientManager clientManager;
	private ChannelManager channelManger;

	public Server() throws Exception {
		config = new Config();
		clientManager = new ClientManager();
		channelManger = new ChannelManager();
		server = this;

		Command.init();
		serverSocket = new ServerSocket(Integer.parseInt(config.getProperty("port")));
		Thread run = new Thread(this, "Running Thread");
		running = true;
		Logger.log("Started Server on port: " + serverSocket.getLocalPort());
		run.start();
	}

	public void run() {
		while (running) {
			Socket socket;
			try {
				socket = serverSocket.accept();
				Thread connection = new Thread(new Connection(socket));
				connection.run();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) throws Exception {
		new Server();
	}

	public static Server getServer() {
		return server;
	}

	public Config getConfig() {
		return config;
	}

	public ClientManager getClientManager() {
		return clientManager;
	}

	public ChannelManager getChannelManger() {
		return channelManger;
	}

	public static String getVersion() {
		return VERSION;
	}

	public void sendMOTD(Client client) throws IOException {
		client.getConnection().send(Reply.RPL_MOTDSTART, client, "Message Of the Day!");
		File motd = new File("motd.txt");
		if (!motd.exists())
			Utilities.makeFile("motd.txt");
		for (String line : Utilities.read("motd.txt"))
			client.getConnection().send(Reply.RPL_MOTD, client, ":" + line);
		client.getConnection().send(Reply.RPL_ENDOFMOTD, client, "End of /MOTD command");

	}

}

package com.journaldev.mail;
 
import java.io.UnsupportedEncodingException;
import java.util.Date;
 
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
 
public class EmailUtil {
 
    /**
     * Utility method to send simple HTML email
     * @param session
     * @param toEmail
     * @param subject
     * @param body
     */
    public static void sendEmail(Session session, String toEmail, String subject, String body){
        try
        {
          MimeMessage msg = new MimeMessage(session);
          //set message headers
          msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
          msg.addHeader("format", "flowed");
          msg.addHeader("Content-Transfer-Encoding", "8bit");
 
          msg.setFrom(new InternetAddress("no_reply@journaldev.com", "NoReply-JD"));
 
          msg.setReplyTo(InternetAddress.parse("no_reply@journaldev.com", false));
 
          msg.setSubject(subject, "UTF-8");
 
          msg.setText(body, "UTF-8");
 
          msg.setSentDate(new Date());
 
          msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
          System.out.println("Message is ready");
          Transport.send(msg);  
 
          System.out.println("EMail Sent Successfully!!");
        }
        catch (Exception e) {
          e.printStackTrace();
        }
    }
}
package com.journaldev.mail;
 
import java.util.Properties;
 
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
 
public class TLSEmail {
 
    /**
       Outgoing Mail (SMTP) Server
       requires TLS or SSL: smtp.gmail.com (use authentication)
       Use Authentication: Yes
       Port for TLS/STARTTLS: 587
     */
    public static void main(String[] args) {
        final String fromEmail = "myemailid@gmail.com"; //requires valid gmail id
        final String password = "mypassword"; // correct password for gmail id
        final String toEmail = "myemail@yahoo.com"; // can be any email id 
         
        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS
         
                //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);
         
        EmailUtil.sendEmail(session, toEmail,"TLSEmail Testing Subject", "TLSEmail Testing Body");
         
    }
 
     
}
