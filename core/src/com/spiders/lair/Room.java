package com.spiders.lair;

import com.badlogic.gdx.utils.Array;

public class Room {

	private int x, y;
	
	//Stores every room this room is connected to by door
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
	
	/** If this room isn't already connected to newRoom, it connects to it
	 * 
	 * @param newRoom
	 */
	public void addConnection(Room newRoom) {
		if (!connections.contains(newRoom, true)) {
			connections.add(newRoom);
		}
	}
	
	/**
	 * 
	 * @param newRoom
	 * @return Whether this room is connected to newRoom
	 */
	public boolean connectedTo(Room newRoom) {
		return connections.contains(newRoom, true);
	}
}
