package com.spiders.lair;

import com.badlogic.gdx.utils.Array;

public class Room {

	private int x, y;
	private Array<Room> connections;
	
	public Room(int x, int y) {
		this.x = x;
		this.y = y;
		connections = new Array<Room>();
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
	
	public void addConnection(Room newRoom) {
		if (!connections.contains(newRoom, true)) {
			connections.add(newRoom);
		}
	}
	
	public boolean connectedTo(Room newRoom) {
		return connections.contains(newRoom, true);
	}
}
