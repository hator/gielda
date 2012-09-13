package gielda;

import java.util.HashMap;
import java.util.Map;

/**
 * Kontoler logiki biznesowej
 * -trzyma listę graczy, firm, kursy
 * @author hator
 *  
 */

public class BusinessLogicController {
	public Map<Integer, Player> players;
	
	public BusinessLogicController() {
		this.players = new HashMap<Integer, Player>();
		
	}
	
	public Player getPlayer(int id) {
		return this.players.get(id);
	}
	
	private void addPlayer(int id) {
		this.players.put(id, new Player());
	}

	public boolean buy(int playerId, int company_id, int amount) {
		return this.getPlayer(playerId).buy(company_id, amount);
	}

	public boolean sell(int playerId, int company_id, int amount) {
		return this.getPlayer(playerId).sell(company_id, amount);
	}

	public boolean authorizePlayer(int id, String hash) {
		if(true) { // TODO code by Żyła
			this.addPlayer(id);
			return true;
		} else {
			return false;
		}
	}

	public String query(String what) {
		return "responce";
		
	}
	
	
}