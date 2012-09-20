package com.hator.gielda;

import java.util.LinkedList;

public class Order {
	public int price;
	public int playerId;
	public int amount;
	public int companyId;
	
	public LinkedList<Order> list;
	
	public Order(int playerId, int companyId, int amount, int price) throws InvalidDataException {
		if (amount <= 0 || price <= 0 || companyId < 0) {
			throw new InvalidDataException();
		} else {
			this.playerId = playerId;
			this.companyId = companyId;
			this.amount = amount;
			this.price = price;
		}
	}

	public int buy(int _amount) {
		if(this.amount < _amount) {
			list.removeFirst();
			Server.getServer().send(this.playerId, "selldone "+this); // notify owner of the order
			return this.amount; // the full amount from this order was bought
		} else {
			this.amount -= _amount;
			return _amount;
		}
	}
	
	public int sell(int _amount) {
		if(this.amount < _amount) {
			list.removeFirst();
			Server.getServer().send(this.playerId, "buydone "+this); // notify owner of the order
			return this.amount; // the full amount was sold
		} else {
			this.amount -= _amount;
			return _amount;
		}
	}
	
	public String toString() {
		return (companyId+" "+amount+" "+price);
	}
}
