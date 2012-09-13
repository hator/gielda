package gielda;

import gielda.Server.ConnectionHandler;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class Interpreter {
	private ConnectionHandler connection;

	public Interpreter(ConnectionHandler connectionHandler) {
		this.connection = connectionHandler;
	}

	public int execute(String s) {
		StringTokenizer tokenizer = new StringTokenizer(s);
		String cmd;
		try {
			cmd = tokenizer.nextToken();
		} catch (NoSuchElementException e) {
			return 0;
		}
		/**
		 * Commands:
		 *  c:login (id hash)
		 *  s:loginok,loginerr
		 *  
		 *  c:query (shares [company]; money; price [company], history (company))
		 *  s:shares (company_id:amount company_id:amount)/(company_id:amount)
		 *  s:money (amount_of_money)
		 *  s:price (company_id:price company_id:price)/(company_id:price)
		 *  s:history (company_id:t-99,t-98,...,t-1,t)
		 *  
		 *  c:buy (company_id amount)
		 *  s:buyok (company_id amount)
		 *  
		 *  c:sell (company_id amount)
		 *  s:sellok (company_id amount)
		 *  
		 *  c:bye
		 *  s:bye
		 *  
		 *  s:error
		 */
		try {
			if (cmd.equals("login")) {
				int id = new Integer(tokenizer.nextToken()); // client id
				String hash = tokenizer.nextToken();

				if (connection.getServer().blc.authorizePlayer(id, hash)) {
					connection.playerId = id;
					connection.send("loginok");
				} else {
					connection.send("loginerr");
				}
			} else if (cmd.equals("query")) {
				String what = tokenizer.nextToken();
				String response = connection.getServer().blc.query(what);
				connection.send(response);
			} else if (cmd.equals("buy")) {
				int company_id = new Integer(tokenizer.nextToken());
				int amount = new Integer(tokenizer.nextToken());
				if(connection.getServer().blc.buy(connection.playerId, company_id, amount)) {
					connection.send("buyok "+company_id+" "+amount);
				} else {
					connection.send("buyerr "+company_id+" "+amount);
				}
			} else if (cmd.equals("sell")) {
				int company_id = new Integer(tokenizer.nextToken());
				int amount = new Integer(tokenizer.nextToken());
				if(connection.getServer().blc.sell(connection.playerId, company_id, amount)) {
					connection.send("sellok "+company_id+" "+amount);
				} else {
					connection.send("sellerr "+company_id+" "+amount);
				}
			} else if (cmd.equals("bye")) {
				connection.send("bye");
				return 1; // disconnect
			} else {
				error("wrong command");
				return -1;
			}
		} catch (NoSuchElementException e) {
			error("wrong parameter number");
			return -1;
		}
		return 0;	
	}
	
	private void error(String msg) {
		connection.send("error");
		connection.error(msg);
		
	}
}
