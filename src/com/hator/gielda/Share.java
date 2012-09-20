package com.hator.gielda;


public class Share {
	private OrderList buyList, sellList;
	
	public Share() {
		this.buyList  = new OrderList();
		this.sellList = new OrderList();
	}

	public int buy(Order o1) {
		Order o2;
		do {
			if((o2 = sellList.get(o1.price)) == null) {
				buyList.add(o1);
				return 1; // added the order to the list
			}
			o1.amount -= o2.buy(o1.amount);
		} while (o1.amount > 0);
		
		return 0; // finalized transaction
	}

	public int sell(Order o1) {
		Order o2;
		do {
			if((o2 = buyList.get(o1.price)) == null) {
				sellList.add(o1); // added the order to the list
				return 1;
			}
			o1.amount -= o2.sell(o1.amount);
		} while (o1.amount > 0);
		
		return 0; // finalized transaction
	}
	
}