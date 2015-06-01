package com.spiders.lair;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class Map {
	
	//The chance that a newly-created room will be connected to the previous room
	private static final float ENLARGEN_CHANCE = 0.3f;
	
	//The chance that the path will go away from the center
	private static final float STRAY_CHANCE = 0.2f;
	
	//Colors given as RGB values between 0 and 1
	private static final float[] WALL_COLOR = {0.35f, 0.35f, 0.35f};
	private static final float[] FLOOR_COLOR = {0.6f, 0.85f, 0.92f};
	
	//The size of any wall given as a percentage of the height of the whole room
	private static final float WALL_SIZE = 0.2f;
	
	//The size of any doorway given as a percentage of the height of the whole room
	private static final float DOOR_SIZE = 0.3f;
	
	private Array<Array<Room>> data;
	private Room currentRoom;
	private int cx, cy;
	private int numRooms;
	private HashMap<Room, Array<Room>> largeRooms;
	
	public Map(int width, int height) {
		cx = width/2;
		cy = height/2;
		numRooms = 0;
		largeRooms = new HashMap<Room, Array<Room>>();
		
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
			if(x > cx) {
				if (Math.random() > STRAY_CHANCE || x == 9) {
					newX = x - 1;
				} else {
					newX = x + 1;
				}
			}
			if(x < cx) {
				if (Math.random() > STRAY_CHANCE || x == 0) {
					newX = x + 1;
				} else {
					newX = x - 1;
				}
			}
			if(y > cy) {
				if (Math.random() > STRAY_CHANCE || y == 9) {
					newY = y - 1;
				} else {
					newY = y + 1;
				}
			}
			if(y < cy) {
				if (Math.random() > STRAY_CHANCE || y == 0) {
					newY = y + 1;
				} else {
					newY = y - 1;
				}
			}

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
						if (Math.random() < ENLARGEN_CHANCE) {
							enlargen(lastChecked, newRoom);
						} else {
							lastChecked.addConnection(newRoom);
							newRoom.addConnection(lastChecked);
						}
						lastChecked = newRoom;
						numRooms++;
					}
				} else {
					if(data.get(newY).get(x) == null) {
						newRoom = new Room(x,newY);
						data.get(newY).set(x, newRoom);
						
						if (Math.random() < 0.5) {
							enlargen(lastChecked, newRoom);
						} else {
							lastChecked.addConnection(newRoom);
							newRoom.addConnection(lastChecked);
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
					
					if(Math.random() < 0.5) {
						lastChecked = hRoom == null ? vRoom : hRoom;
						data.get(y).get(x).addConnection(lastChecked);
						lastChecked.addConnection(data.get(y).get(x));
						
					}
					else {
						lastChecked = vRoom == null ? hRoom : vRoom; 
					}
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
				
				if (Math.random() < ENLARGEN_CHANCE) {
					enlargen(lastChecked, newRoom);
				} else {
					lastChecked.addConnection(newRoom);
					newRoom.addConnection(lastChecked);
				}
				numRooms++;
			} else {
				lastChecked.addConnection(newRoom);
				newRoom.addConnection(lastChecked);
			}
			
			//lastChecked is updated to the room at (newX,newY)
			lastChecked = newRoom;
		}
		
		
					
	}

	private void enlargen(Room lastChecked, Room newRoom) {
		Array<Room> oldArray = largeRooms.get(lastChecked);
		if(oldArray == null) {
			oldArray = new Array<Room>();
		}
		oldArray.add(newRoom);
		largeRooms.put(lastChecked, oldArray);
		
		Array<Room> newArray = largeRooms.get(newRoom);
		if(newArray == null) {
			newArray = new Array<Room>();
		}
		newArray.add(lastChecked);
		largeRooms.put(newRoom, newArray);
	}
	
	public void drawMap(ShapeRenderer sr) {
		int height = data.size;
		int width = data.get(0).size;
		int roomW = SpidersLair.width / width;
		int roomH = SpidersLair.height / height;
		int wallOffset = (int) (WALL_SIZE / 2 * roomH);
		//For all existing rooms, this draws the walls and then the floors
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				if(data.get(y).get(x) != null) {
					sr.begin(ShapeType.Filled);
					sr.setColor(WALL_COLOR[0],WALL_COLOR[1],WALL_COLOR[2],1f);
					sr.rect(x*roomW-wallOffset,y*roomH-wallOffset,roomW+2*wallOffset,roomH+2*wallOffset);
					sr.setColor(FLOOR_COLOR[0],FLOOR_COLOR[1],FLOOR_COLOR[2],1f);
					sr.rect(x*roomW+wallOffset,y*roomH+wallOffset,roomW-2*wallOffset,roomH-2*wallOffset);
					sr.end();
				}
			}
		}
		
		int doorOffset = (int) (DOOR_SIZE / 2 * roomH);
		
		//For all rooms that are horizontally adjacent, this checks whether or not
		//they should be connected into one room, and then either connects them
		//or draws a doorway between them
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width - 1; x++) {
				Room current = data.get(y).get(x);
				if(current != null) {
					Room right = data.get(y).get(x+1);
					if(right != null) {
						sr.begin(ShapeType.Filled);
						sr.setColor(FLOOR_COLOR[0],FLOOR_COLOR[1],FLOOR_COLOR[2],1f);
						
						Array<Room> roomSize = largeRooms.get(current);
						if(roomSize != null && roomSize.contains(right, true))
							sr.rect((x+1)*roomW-wallOffset,y*roomH+wallOffset,2*wallOffset,roomH-2*wallOffset);
						else if(current.connectedTo(right))
							sr.rect((x+1)*roomW-wallOffset,(y+0.5f)*roomH-doorOffset,2*wallOffset,2*doorOffset);
						sr.end();
					}
				}
			}
		}
		//This does the same as the previous loop for vertically adjacent rooms
		for(int y = 0; y < height - 1; y++) {
			for(int x = 0; x < width; x++) {
				Room current = data.get(y).get(x);
				if(current != null) {
					Room up = data.get(y+1).get(x);
					if(up != null) {
						sr.begin(ShapeType.Filled);
						sr.setColor(FLOOR_COLOR[0],FLOOR_COLOR[1],FLOOR_COLOR[2],1f);
						
						Array<Room> roomSize = largeRooms.get(current);
						if(roomSize != null && roomSize.contains(up,true))
							sr.rect(x*roomW+wallOffset,(y+1)*roomH-wallOffset,roomW-2*wallOffset,wallOffset*2);
						else if(current.connectedTo(up))
							sr.rect((x+0.5f)*roomW-doorOffset,(y+1)*roomH-wallOffset,2*doorOffset,2*wallOffset);
						sr.end();
					}
				}
			}
		}
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
	
	
//	public static void main(String[] args) {
//		Map tester = new Map(10, 10);
////		for(int i = 0; i < 1; i++) {
////			System.out.println(new Map(10, 10).toString());
////		}
//		System.out.println(tester.toString());
//		System.out.println(tester.largeRooms.toString());
//	}
	
}
