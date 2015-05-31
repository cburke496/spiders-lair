package com.spiders.lair;

public interface Enemy {
	
	void move();
	void shoot();
	void loseHealth();
	int getHealth();
	
}
