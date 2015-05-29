package com.spiders.lair;

import com.badlogic.gdx.utils.Array;
import java.util.HashMap;

public class Map {
	
	//The chance that a newly-created room will be connected to the previous room
	private static final float connectionChance = 0.5f;
	
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
		
		for (int i = 0; i < 8; i++) {
			int randx = (int) (Math.random() * width);
			int randy = (int) (Math.random() * height);
			connectToCenter(randx, randy);
		}
		
		currentRoom = data.get(cy).get(cx);
	}
	
	//Creates a room at (x,y) and a series of rooms connecting it to (cx,cy)
	private void connectToCenter(int x, int y) {
		//First the room at (x,y) is created if it doesn't already exist
		if(data.get(y).get(x) == null) {
			data.get(y).set(x, new Room(x,y));
			numRooms++;
		}
		
		//lastChecked stores the previous room in the chain connecting the room at
		//(x,y) to the center, so to begin with, it stores the room at (x,y)
		Room lastChecked = data.get(y).get(x);
		
		//This loop continually sets x and y to new coordinates closer and closer
		//to the center, until they finally reach the center coordinates
		while(x != cx || y != cy) {
			int newX = x, newY = y;
			if(x > cx) newX = x - 1;
			if(x < cx) newX = x + 1;
			if(y > cy) newY = y - 1;
			if(y < cy) newY = y + 1;
			
			//newRoom stores the next room in the chain after lastChecked
			Room newRoom = null;
			
			//If both the x and y coordinates change (i.e. the room at (newX, newY)
			//is diagonal to the one at (x,y)), then an adjacent room must be created
			//(i.e. (newX,y) or (x,newY)), so one of the two is chosen at random
			if(x != newX && y != newY) {
				if(Math.random() < 0.5) {
					if(data.get(y).get(newX) == null) {
						newRoom = new Room(newX, y);
						data.get(y).set(newX, newRoom);
						
						//There is a chance that the newly-created room will be
						//connected to the previous room
						if (Math.random() < connectionChance)
							addConnection(lastChecked, newRoom);
						
						lastChecked = newRoom;
						numRooms++;
					}
				} else {
					if(data.get(newY).get(x) == null) {
						newRoom = new Room(x,newY);
						data.get(newY).set(x, newRoom);
						
						if (Math.random() < 0.5) {
							addConnection(lastChecked, newRoom);
						}
						
						lastChecked = newRoom;
						numRooms++;
					}
				}
				
				//This if block runs if for some reason, no room was created
				//in the previous if-else, meaning that lastChecked is still
				//the room at (x,y). This chooses between the room at (newX,y)
				//and the room at (x,newY) to be the new lastChecked, making
				//sure that whichever is chosen is non-null
				if(newRoom == null) {
					Room hRoom = data.get(newY).get(x);
					Room vRoom = data.get(y).get(newX);
					
					//If this point in the code is reached, it shouldn't be
					//possible for both (newX,y) and (x,newY) to be null
					assert hRoom != null || vRoom != null;
					
					if(Math.random() < 0.5)
						lastChecked = hRoom == null ? vRoom : hRoom;
					else
						lastChecked = vRoom == null ? hRoom : vRoom;
				}
			}
			
			//The coordinates are updated
			x = newX; y = newY;
			
			//If the room at (newX,newY) hasn't been created yet, it's created
			//and has a chance of being connected to lastChecked
			newRoom = data.get(y).get(x);
			if(newRoom == null) {
				newRoom = new Room(x,y);
				data.get(y).set(x, newRoom);
				
				if(Math.random() < connectionChance)
					addConnection(lastChecked, newRoom);
					
				numRooms++;
			}
			
			//lastChecked is updated to the room at (newX,newY)
			lastChecked = newRoom;
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
