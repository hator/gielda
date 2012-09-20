package com.hator.gielda;


import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.hator.gielda.Tasks;
import com.hator.gielda.Server.ConnectionHandler;
import com.hator.gielda.Tasks.Disconnect;
import com.hator.gielda.Tasks.Error;

public class Interpreter {
	private ConnectionHandler connection;

	public Interpreter(ConnectionHandler connectionHandler) {
		this.connection = connectionHandler;
	}

	public void execute(String s) throws Tasks.Disconnect, Tasks.Error {
		StringTokenizer tokenizer = new StringTokenizer(s);
		String cmd;
		try {
			cmd = tokenizer.nextToken();
		} catch (NoSuchElementException e) {
			throw new Tasks.Error("wrongParameterNumber");
		}
		/**
		 * Commands:
		 *  c:login (id hash)
		 *  s:loginok, loginerr
		 *  
		 *  c:query (shares [company]; money; history (company))
		 *  s:shares (companyId:amount companyId:amount)/(companyId:amount)
		 *  s:money (amount_of_money)
		 *  s:price (companyId:price companyId:price)/(companyId:price)
		 *  s:history (companyId:t-99,t-98,...,t-1,t)
		 *  
		 *  c:buy (companyId amount price)
		 *  s:buyok (companyId amount price)
		 *  	s:buyerr (companyId amount price)
		 *  s:buydone (companyId amount price)
		 *  
		 *  c:sell (companyId amount price)
		 *  s:sellok (companyId amount price)
		 *  	s:sellerr (companyId amount price)
		 *  s:selldone (companyId amount price)
		 *  
		 *  c:bye
		 *  s:bye
		 *  
		 *  s:error
		 */
		try {
			if (cmd.equals("login")) {
				
				int playerId = new Integer(tokenizer.nextToken()); // client id
				String hash = tokenizer.nextToken();

				if (connection.getServer().blc.loginPlayer(playerId, hash)) {
					connection.setPlayerId(playerId);
					connection.send("loginok");
				} else {
					connection.send("loginerr");
				}
				
			} else if (cmd.equals("query")) {
				
				String what = tokenizer.nextToken();
				String response = connection.getServer().blc.query(what);
				connection.send(response);
				
			} else if (cmd.equals("buy")) {
				
				int companyId = new Integer(tokenizer.nextToken());
				int amount 	  = new Integer(tokenizer.nextToken());
				int price     = new Integer(tokenizer.nextToken());
				
				try {
					Order o = new Order(connection.id, companyId, amount, price);
					int buyResult = connection.getServer().blc.buy(o);
					if(buyResult != -1) {
						connection.send("buyok "+companyId+" "+amount+" "+price);
						if(buyResult == 0)
							connection.send("buydone "+o);
					} else {
						connection.send("buyerr "+o);
					}
				} catch (InvalidDataException e) {
					connection.send("buyerr "+companyId+" "+amount+" "+price);
				} catch (PlayerNotLoggedException e) {
					throw new Error("notLogged");
				}
				
			} else if (cmd.equals("sell")) {
				
				int companyId = new Integer(tokenizer.nextToken());
				int amount    = new Integer(tokenizer.nextToken());
				int price     = new Integer(tokenizer.nextToken());
				
				try {
					Order o = new Order(connection.id, companyId, amount, price);
					int sellResult = connection.getServer().blc.sell(o); 
					if(sellResult != -1) {
						connection.send("sellok "+companyId+" "+amount+" "+price);
						if(sellResult == 0)
							connection.send("selldone "+o);
					} else {
						connection.send("sellerr "+o);
					}
				} catch (InvalidDataException e) {
					connection.send("sellerr "+companyId+" "+amount+" "+price);
				} catch (PlayerNotLoggedException e) {
					throw new Error("notLogged");
				}
				
			} else if (cmd.equals("bye")) {
				
				connection.send("bye");
				throw new Tasks.Disconnect(); // disconnect
				
			} else {
				throw new Tasks.Error("wrongCommand");
			}
		} catch (NoSuchElementException e) {
			throw new Tasks.Error("wrongParameterNumber");
		}
	}
}
