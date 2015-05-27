package com.spiders.lair;

import com.badlogic.gdx.utils.Array;

public class Map {
	
	private static final int minNumberRooms = 6;
	
	private Array<Array<Room>> data;
	private Room currentRoom;
	private int cx, cy;
	private int numRooms;
	
	public Map(int width, int height) {
		cx = width/2;
		cy = height/2;
		numRooms = 0;
		
		data = new Array<Array<Room>>(height);
		for(int i = 0; i < height; i++) {
			data.add(new Array<Room>(width));
			for(int j = 0; j < width; j++)
				data.get(i).add(null);
		}
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				if(Math.random() < 0.2) {
					connectToCenter(j,i);
				}
			}
		}
		
		while(numRooms < minNumberRooms) {
			int randx = (int) (Math.random() * 5);
			int randy = (int) (Math.random() * 5);
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
		while(x != cx || y != cy) {
			int newX = x, newY = y;
			if(x > cx) newX = x - 1;
			if(x < cx) newX = x + 1;
			if(y > cy) newY = y - 1;
			if(y < cy) newY = y + 1;
			if(x != newX && y != newY) {
				if(Math.random() < 0.5) {
					if(data.get(y).get(newX) == null) {
						data.get(y).set(newX, new Room(newX,y));
						numRooms++;
					}
				} else {
					if(data.get(newY).get(x) == null) {
						data.get(newY).set(x, new Room(x,newY));
						numRooms++;
					}
				}
			}
			x = newX; y = newY;
			if(data.get(y).get(x) == null) {
				data.get(y).set(x, new Room(x,y));
				numRooms++;
			}
		}
			
	}

	public String toString() {
		String output = "";
		for(int i = 0; i < data.size; i++) {
			output += data.get(i).toString() + "\n";
		}
		return output;
	}
	
	/*
	public static void main(String[] args) {
		for(int i = 0; i < 5; i++) {
			System.out.println(new Map(5,5).toString());
		}
	}
	*/
}
