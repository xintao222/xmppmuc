package eu.janinko.xmppmuc;

import org.apache.log4j.Logger;

/**
 * The XMPP MUX bot.
 *
 * @author Honza Brázdil <janinko.g@gmail.com>
 */
public class Bot {
	private XmppConnection connection;
	private Commands commands;
	
	private static Logger logger = Logger.getLogger(Bot.class);

	/**
	 * Creates new Bot instance.
	 *
	 */
	public Bot() {
		commands = new Commands(this);
		connection = new XmppConnection(commands);
	}

	/**
	 * Returns {@link  XmppConnection} used for configuring connection.
	 *
	 * @return XmppConnection that handle connection to XMPP server and MUC.
	 */
	public XmppConnection getConnection() {
		return connection;
	}

	/**
	 * Starts the bot. Attempts to connect to server and MUC.
	 *
	 */
	public void start() {
		int retry = 10;
		while (!connection.connect() && retry-- > 0) {
			logger.warn("Connection failed, retry in " + (100 - retry * 10) + " seconds.");
			try {
				Thread.sleep(1000 * (100 - retry * 10));
			} catch (InterruptedException e) {
				logger.error("Recconection interrupted.", e);
			}
		}
	}

	/**
	 * Stops the bot.
	 *
	 */
	public void stop() {
		connection.stop();
		connection = new XmppConnection(connection);
		synchronized (this) {
			this.notify();
		}
	}

	/**
	 * Causes the current thread to wait until the bot is stopped.
	 *
	 */
	public void sleep() {
		if (connection.isConnected()) {
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException ex) {
					logger.warn("Waiting for connection to finish interupted.");
				}
			}
		}
	}

	/**
	 * Sets command prefix. Default is '.' (dot).
	 *
	 * @param prefix Command prefix.
	 */
	public void setPrefix(String prefix) {
		commands.setPrefix(prefix);
	}

	/**
	 * Returns current command prefix.
	 *
	 * @return Command prefix.
	 */
	public String getPrefix() {
		return commands.getPrefix();
	}
}
