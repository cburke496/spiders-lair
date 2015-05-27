package com.spiders.lair;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;

public class Bullet extends Circle{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2069399374615712215L;
	
	public static final Texture texture = new Texture(Gdx.files.internal("bullet.png"));
	public int dx, dy;

	public Bullet() {
		super();
	}
}
