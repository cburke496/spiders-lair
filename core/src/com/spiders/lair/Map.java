package com.spiders.lair;

import com.badlogic.gdx.utils.Array;
import java.util.HashMap;

public class Map {
	
//	private static final int minNumberRooms = 8;
	
	private Array<Array<Room>> data;
	private Room currentRoom;
	private int cx, cy;
	private int numRooms;
	private HashMap<Room, Array<Room>> roomConnections;
	
	public Map(int width, int height) {
		cx = width/2;
		cy = height/2;
		numRooms = 0;
		roomConnections = new HashMap<Room, Array<Room>>();
		
		data = new Array<Array<Room>>(height);
		for(int i = 0; i < height; i++) {
			data.add(new Array<Room>(width));
			for(int j = 0; j < width; j++)
				data.get(i).add(null);
		}
		
//		for(int i = 0; i < height; i++) {
//			for(int j = 0; j < width; j++) {
//				if(Math.random() < 0.2) {
//					connectToCenter(j,i);
//				}
//			}
//		}
		
//		while(numRooms < minNumberRooms) {
		for (int i = 0; i < 8; i++) {
			int randx = (int) (Math.random() * width);
			int randy = (int) (Math.random() * height);
			connectToCenter(randx, randy);
		}
		
		currentRoom = data.get(cy).get(cx);
	}
	
	//Creates a room at (x,y) and a series of rooms connecting it to (cx,cy)
	private void connectToCenter(int x, int y) {
		if(data.get(y).get(x) == null) {
			data.get(y).set(x, new Room(x,y));
			numRooms++;
		}
		Room lastChecked = data.get(y).get(x);
		while(x != cx || y != cy) {
			int newX = x, newY = y;
			if(x > cx) newX = x - 1;
			if(x < cx) newX = x + 1;
			if(y > cy) newY = y - 1;
			if(y < cy) newY = y + 1;
			if(x != newX && y != newY) {
				if(Math.random() < 0.5) {
					if(data.get(y).get(newX) == null) {
						Room newRoom = new Room(newX, y);
						data.get(y).set(newX, newRoom);
						if (Math.random() < 0.5) {
							addConnection(lastChecked, newRoom);
						}
						lastChecked = newRoom;
						numRooms++;
					}
				} else {
					if(data.get(newY).get(x) == null) {
						Room newRoom = new Room(x,newY);
						data.get(newY).set(x, newRoom);
						if (Math.random() < 0.5) {
							addConnection(lastChecked, newRoom);
						}
						lastChecked = newRoom;
						numRooms++;
					}
				}
			}
			x = newX; y = newY;
			if(data.get(y).get(x) == null) {
				//TODO: Add secondary room connection
				data.get(y).set(x, new Room(x,y));
				lastChecked = new Room(newX, newY);
				numRooms++;
			}
		}
			
	}

	private void addConnection(Room lastChecked, Room newRoom) {
		Array<Room> oldArray = roomConnections.get(lastChecked);
		if(oldArray == null) {
			oldArray = new Array<Room>();
		}
		oldArray.add(newRoom);
		roomConnections.put(lastChecked, oldArray);
		
		Array<Room> newArray = roomConnections.get(newRoom);
		if(newArray == null) {
			newArray = new Array<Room>();
		}
		newArray.add(lastChecked);
		roomConnections.put(newRoom, newArray);
	}

	public String toString() {
		String output = "";
		for(int y = 0; y < data.size; y++) {
			for (int x = 0; x < data.get(0).size; x++) {
				if (data.get(y).get(x) == null) {
					output += "     ";
				} else {
				    output += data.get(y).get(x).toString();
				}
			}
			output += "\n";
		}
		return output;
	}
	
	
	public static void main(String[] args) {
		Map tester = new Map(10, 10);
//		for(int i = 0; i < 1; i++) {
//			System.out.println(new Map(10, 10).toString());
//		}
		System.out.println(tester.toString());
		System.out.println(tester.roomConnections.toString());
	}
	
}
