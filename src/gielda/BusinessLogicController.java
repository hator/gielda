package gielda;

import java.util.HashMap;
import java.util.Map;
import gielda.PlayerNotLoggedException;

/**
 * Business Logic Controller
 * - keeps players, products and prices
 * @author hator
 *  
 */

public class BusinessLogicController {
	public Map<Integer, Player> players;
	public Share products[] ;
	
	public BusinessLogicController() {
		this.players  = new HashMap<Integer, Player>();
		this.products = new Share[5]; // TODO number of products
		for(int i = 0; i < 5; i++) {
			this.products[i] = new Share(/* TODO the initial price */); // data taken from db/file
		}
	}
	
	/** player handling **/
	public Player getPlayer(int id) {
		return this.players.get(id);
	}
	
	private void addPlayer(int id) {
		this.players.put(id, new Player());
	}
	
	public boolean loginPlayer(int id, String hash) {
		// TODO authorization code by Żyła
		this.addPlayer(id);
		return true;
	}
	
	/** transactions **/
	public int buy(Order o) throws PlayerNotLoggedException {
		if(players.get(o.playerId) != null) // is the player logged in?
			return products[o.companyId].buy(o);
		else
			throw new PlayerNotLoggedException();
	}
	
	public int sell(Order o) throws PlayerNotLoggedException {
		if(players.get(o.playerId) != null) // is the player logged in?
			return products[o.companyId].sell(o);
		else
			throw new PlayerNotLoggedException();
	}

	public String query(String what) {
		return "responce"; // TODO queries' responses
	}
}