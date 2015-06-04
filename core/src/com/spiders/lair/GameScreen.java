package com.spiders.lair;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
	final SpidersLair game;

	private Texture dropImage;
	private Sound dropSound;
	//private Music bgm;
	
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;

	private Player player;
	private int playerRad = 25;

	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private int dropsGathered;
	private int dropsShot;

	private Array<Integer> keys = new Array<Integer>();
	
	private Map map;
	
	//BULLET VARIABLES/CONSTANTS
	private Pool<Bullet> bulletPool = Pools.get(Bullet.class);
	private Array<Bullet> bullets = new Array<Bullet>();
	private long lastShotTime = 0;
	private final long minTimeBetweenShots = 100000000;
	private float bulletSpeed = 12;
	private final int bulletRad = 10;

	//MOVEMENT VARIABLES/CONSTANTS
	private int numDirectionsHeld = 0; //Number of directions currently being held
	private final float maxSpeed = 9; //Maximum speed in either direction
	private final float acceleration = 0.5f; //Acceleration when inputting forward
	private final float deceleration = 0.5f; //Deceleration when not inputting forward or backward
	private final float forcedDeceleration = 1; //Deceleration when inputting backward
	
	//VOLUME CONSTANTS
	private final float dropVolume = 0.4f;
	//private final float bgmVolume = 0.4f;

	public GameScreen(final SpidersLair sl) {
		game = sl;

		dropImage = new Texture(Gdx.files.internal("droplet.png"));

		dropSound = Gdx.audio.newSound(Gdx.files.internal("coin.wav"));
		dropSound.setVolume(lastDropTime, 0.4f);
		//bgm = Gdx.audio.newMusic(Gdx.files.internal("smb.wav"));
		//bgm.setVolume(bgmVolume);

		//bgm.setLooping(true);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, SpidersLair.WIDTH, SpidersLair.HEIGHT);
		
		shapeRenderer = new ShapeRenderer();

		//player = new Player(SpidersLair.WIDTH/2 - playerRad, SpidersLair.HEIGHT/2 - playerRad, playerRad);
		player = new Player(-1 * playerRad, -1 * playerRad, playerRad);

		raindrops = new Array<Rectangle>();
		spawnRaindrop();
		
		map = new Map(10,10);
		//System.out.println(map);
		
		Gdx.input.setInputProcessor(new InputAdapter() {

			public boolean keyDown(int keyCode) {
				//System.out.println(keyCode);

				//If the player isn't holding any directions, hitting a 
				//direction automatically sets their angle in that direction
				if(numDirectionsHeld == 0) {
					if (keyCode == 22 || keyCode == 32) //RIGHT or D
						player.theta = 0;
					else if(keyCode == 19 || keyCode == 51) //UP or W
						player.theta = Math.PI/2;
					else if(keyCode == 21 || keyCode == 29) //LEFT or A
						player.theta = Math.PI;
					else if(keyCode == 20 || keyCode == 47) //DOWN or S
						player.theta = 3 * Math.PI / 2;
				}
				
				int i = 0;
				if ((i = keys.indexOf(keyCode, true)) >= 0) {
					keys.removeIndex(i);
					keys.add(keyCode);
				} else {
					if(isDirection(keyCode))
					   numDirectionsHeld++;
					keys.add(keyCode);
				}
				return true;
			}

			public boolean keyUp(int keyCode) {
				if (keys.contains(keyCode, true)) {
					if(isDirection(keyCode))
						numDirectionsHeld--;
					keys.removeValue(keyCode, true);
				}
				return true;
			}
		});
	}

	public void render(float delta) {
		handlePlayer();
		handleBullets();
		
		Gdx.gl.glClearColor(0.8f, 0.8f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		Vector3 coords = new Vector3(player.x + playerRad, player.y + playerRad,0);
		camera.translate(coords.sub(camera.position));
		camera.update();
		
		shapeRenderer.setProjectionMatrix(camera.combined);
		//map.drawMap(shapeRenderer); //Uncomment this line to display the entire map on the screen
		map.drawMap(shapeRenderer, player.x, player.y);
		//TODO: check where player is, if they're hitting a door/wall, update currentRoom, etc.

		game.batch.setProjectionMatrix(camera.combined);
		game.batch.begin();
		game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, SpidersLair.HEIGHT);
		game.font.draw(game.batch, "Drops Shot: " + dropsShot, 0, SpidersLair.HEIGHT - 20);
		for (Rectangle raindrop : raindrops) {
			game.batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		for(Bullet b: bullets) {
			game.batch.draw(Bullet.texture, b.x, b.y, b.radius*2, b.radius*2);
		}
		game.batch.draw(player.texture, player.x, player.y, player.radius, player.radius, player.radius*2, player.radius*2, 1, 1, 
				(float) (180 * player.theta / Math.PI), 0, 0, player.texture.getWidth(), player.texture.getHeight(), false, false);
		game.batch.end();

		if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
			spawnRaindrop();

		Iterator<Rectangle> iter = raindrops.iterator();
		while (iter.hasNext()) {
			Rectangle raindrop = iter.next();

			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + raindrop.height < 0)
				iter.remove();
		}
	}
	
	//Returns true if keyCode is the code for an arrow key or WASD
	private boolean isDirection(int keyCode) {
		return keyCode == 22 || keyCode == 32 || keyCode == 19 || keyCode == 51 ||
				   keyCode == 21 || keyCode == 29 || keyCode == 20 || keyCode == 47;
	}

	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.width = 64;
		raindrop.height = 64;
		raindrop.x = MathUtils.random(0, SpidersLair.WIDTH - raindrop.width);
		raindrop.y = SpidersLair.HEIGHT;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}

	private void handlePlayer() {
		moveVertical();
		moveHorizontal();
		calculateTheta();
	}
	
	private void moveVertical() {
		boolean accelerated = false;
		
		for(int i = keys.size-1; i >= 0; i--) {
			int key = keys.get(i);
			if(key == 19 || key == 51) { //UP or W
				if (player.dy >= 0)
					player.dy += acceleration;
				else
					player.dy += forcedDeceleration;
				
				accelerated = true;
				break;
			} else if(key == 20 || key == 47) { //DOWN or S
				if (player.dy <= 0)
					player.dy -= acceleration;
				else
					player.dy -= forcedDeceleration;
				
				accelerated = true;
				break;
			}
		}
		
		if(!accelerated) {
			if (player.dy != 0)
				player.dy -= deceleration * (player.dy > 0 ? 1 : -1);
		}
		
		if (player.dy > maxSpeed)
			player.dy = maxSpeed;
		if (player.dy < -1 * maxSpeed)
			player.dy = -1 * maxSpeed;
		
		player.y += player.dy;
	}
	
	private void moveHorizontal() {
		boolean accelerated = false;
		
		for(int i = keys.size-1; i >= 0; i--) {
			int key = keys.get(i);
			if(key == 22 || key == 32) { //RIGHT or D
				if (player.dx >= 0)
					player.dx += acceleration;
				else
					player.dx += forcedDeceleration;
				
				accelerated = true;
				break;
			} else if(key == 21 || key == 29) { //LEFT or A
				if (player.dx <= 0)
					player.dx -= acceleration;
				else
					player.dx -= forcedDeceleration;
				
				accelerated = true;
				break;
			}
		}
		
		if(!accelerated) {
			if (player.dx != 0)
				player.dx -= deceleration * (player.dx > 0 ? 1 : -1);
		}
		
		if (player.dx > maxSpeed)
			player.dx = maxSpeed;
		if (player.dx < -1 * maxSpeed)
			player.dx = -1 * maxSpeed;
		
		player.x += player.dx;
	}
	
	private void calculateTheta() {
		//If no directions are being held, theta will retain its previous value
		if(numDirectionsHeld == 0) return;
		
		if(player.dx == 0) {
			if(player.dy > 0) {
				player.theta = Math.PI/2;
			} else if(player.dy < -0) {
				player.theta = -1 * Math.PI/2;
			}
		} else {
			double atan = Math.atan(player.dy/player.dx);
			player.theta = player.dx > 0 ? atan : atan + Math.PI;
		}
	}
	
	private void handleBullets() {
		moveBullets();
		shootNewBullets();
		detectHits();
	}
	
	private void shootNewBullets() {
		if(!keys.contains(62, true) || TimeUtils.nanoTime() - lastShotTime < minTimeBetweenShots) return;
		
		lastShotTime = TimeUtils.nanoTime();
		Bullet newB = bulletPool.obtain();
		bullets.add(newB);
		newB.radius = bulletRad;
		newB.x = player.x + player.radius - newB.radius + ((player.radius + newB.radius) * (float) Math.cos(player.theta));
		newB.y = player.y + player.radius - newB.radius + ((player.radius + newB.radius) * (float) Math.sin(player.theta));
		
		newB.dx = (int) (bulletSpeed * Math.cos(player.theta));
		newB.dy = (int) (bulletSpeed * Math.sin(player.theta));
	}
	
	private void moveBullets() {
		Iterator<Bullet> iter = bullets.iterator();
		while(iter.hasNext()) {
			Bullet b = iter.next();
			
			b.x += b.dx;
			b.y += b.dy;
			if(b.x < 0 - 2 * b.radius || b.x > SpidersLair.WIDTH ||
			   b.y < 0 - 2 * b.radius || b.y > SpidersLair.HEIGHT) {
				iter.remove();
				bulletPool.free(b);
			}
		}
	}
	
	private void detectHits() {
		Iterator<Bullet> bIter = bullets.iterator();
		while(bIter.hasNext()) {
			Bullet b = bIter.next();
			Iterator<Rectangle> rIter = raindrops.iterator();
			while(rIter.hasNext()) {
				Rectangle raindrop = rIter.next();
				if(Math.abs(raindrop.x + raindrop.width/2 - b.x - b.radius) < raindrop.width/2 + b.radius &&
				   Math.abs(raindrop.y + raindrop.height/2 - b.y - b.radius) < raindrop.height/2 + b.radius) {
					if(Intersector.overlaps(b,raindrop)) {
						rIter.remove();
						bIter.remove();
						dropsShot++;
						break;
					}
				}
			}
		}
	}

	public void dispose() {
		dropImage.dispose();
		dropSound.dispose();
		//bgm.dispose();
	}

	@Override
	public void show() {
		//bgm.play();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}
}
