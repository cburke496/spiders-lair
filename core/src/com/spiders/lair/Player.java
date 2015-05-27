package com.spiders.lair;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;

public class Player extends Circle{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4881803697933684877L;
	
	public Texture texture = new Texture(Gdx.files.internal("player.png"));
	public float dx, dy;
	double theta;//Angle of player's velocity (or previous velocity if currently stationary)

	public Player(int x, int y, int size) {
		super(x,y,size);
		
		dx = 0;
		dy = 0;
		theta = 0;
	}
}
