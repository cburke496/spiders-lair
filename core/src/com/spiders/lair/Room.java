package com.spiders.lair;

public class Room {

	private int x, y;
	
	public Room(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int mapx() {
		return x;
	}
	
	public int mapy() {
		return y;
	}
	
	public String toString() {
		return "("+x+","+y+")";
	}
}
