package gielda;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

public class OrderList {
	private HashMap<Integer, LinkedList<Order>> hm;
	
	public OrderList(){
		this.hm = new HashMap<Integer, LinkedList<Order>>();
	}
	
	public boolean add(Order o) {
		if(!hm.containsKey(o.price)) {
			hm.put(o.price, new LinkedList<Order>());
		}
		hm.get(o.price).add(o);
		o.list = hm.get(o.price);
		return true;
	}
	
	public Order get(int price) {
		if(hm.containsKey(price)) {
			try {
				return hm.get(price).getFirst();
			} catch (NoSuchElementException e) {
				return null;
			}
		} else {
			return null;
		}
	}
	
	}
