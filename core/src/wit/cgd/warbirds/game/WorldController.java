package wit.cgd.warbirds.game;

import java.util.Random;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Rectangle;
import wit.cgd.warbirds.game.objects.AbstractGameObject;
import wit.cgd.warbirds.game.objects.AbstractGameObject.State;
import wit.cgd.warbirds.game.objects.Bonus;
import wit.cgd.warbirds.game.objects.Bullet.BulletType;
import wit.cgd.warbirds.game.objects.Bullet;
import wit.cgd.warbirds.game.objects.Enemy;
import wit.cgd.warbirds.game.objects.ExtraLife;
import wit.cgd.warbirds.game.objects.Level;
import wit.cgd.warbirds.game.objects.Player;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.CameraHelper;
import wit.cgd.warbirds.game.util.Constants;

public class WorldController extends InputAdapter {

	private static final String	TAG	= WorldController.class.getName();

	private Game				game;
	public CameraHelper			cameraHelper;
	public Level				level;
	private int					levelNumber = 1;
	private boolean 			isTimeSet = false;
	private long 				setTime;
	private boolean				gameFinished = false;
	
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();
	private Rectangle r3 = new Rectangle();
	private Rectangle r4 = new Rectangle();

	public WorldController(Game game) {
		this.game = game;
		init();
	}

	private void init() {
		Gdx.input.setInputProcessor(this);
		level = new Level(levelNumber);
		cameraHelper = new CameraHelper();
		cameraHelper.setTarget(level);
	}


	public void update(float deltaTime) {
		if(level.player.state != State.DEAD){
			handleDebugInput(deltaTime);
			handleGameInput(deltaTime);
			cameraHelper.update(deltaTime);
			level.update(deltaTime);
			cullObjects();
			checkBulletEnemyCollision();
			checkEnemyBulletPlayerCollision();
			checkEnemyPlayerCollision();
			checkPlayerBonusCollision();
			
			if(level.enemies.size() == 0 && !gameFinished){
				WorldRenderer.renderText("Level "+levelNumber+" Completed!");
				if(!isTimeSet){
					setTime = System.currentTimeMillis();
					isTimeSet = true;
				}
				
				System.out.println("stoppedTime = "+setTime+"      currentTime = "+System.currentTimeMillis());
				if(System.currentTimeMillis() - setTime >= 2000){
					if(Gdx.files.internal("levels/level-"+(levelNumber+1)+".json").exists()){
						AudioManager.instance.play(Assets.instance.sounds.levelFinished);
						levelNumber++;
						init();
						isTimeSet = false;
					}
					else{
						AudioManager.instance.play(Assets.instance.sounds.levelFinished);
						gameFinished = true;
					}

				}

			}
			else if(gameFinished){
				WorldRenderer.renderText("Congratulations, you have completed the game!");
			}
		}
		else{
			Player.isGameOver = true;
		}
	}
	
	public void incrementLevel(){
		levelNumber++;
	}


	/**
	 * Remove object because they are out of screen bounds or because they have died
	 */
	public void cullObjects() {
		
		// cull bullets 
		for (int k=level.bullets.size; --k>=0; ) { 	// traverse array backwards !!!
			Bullet it = level.bullets.get(k);
			if (it.state == Bullet.State.DEAD) {
				level.bullets.removeIndex(k);
				level.bulletPool.free(it);
			} else if (it.state==Bullet.State.ACTIVE && !isInScreen(it)) {
				it.state = Bullet.State.DYING;
				it.timeToDie = Constants.BULLET_DIE_DELAY;
			}else if(!isInScreen(it)){
				it.state = State.DEAD;
			}
			
		}

		Random rand = new Random();

		// TODO cull enemies
		for(int i = level.enemies.size()-1; i >= 0; i--){
			Enemy enemy = level.enemies.get(i);
			if(enemy.state == Enemy.State.DEAD){
				if(rand.nextInt(3) == 2){
					level.bonuses.add(new ExtraLife(level,enemy.position));
				}
				level.enemies.remove(i);
			}
			
		}
		
		//cull bonuses
		
		for(int i = level.bonuses.size()-1; i>= 0; i--){
			if(level.bonuses.get(i).state == State.DEAD){
				level.bonuses.remove(i);
			}
		}
	}

	// Collision detection methods
	public void checkBulletEnemyCollision() {
		for(Enemy enemy: level.enemies){
			r2.set(enemy.position.x, enemy.position.y, enemy.dimension.x, enemy.dimension.y);
			
			for(Bullet bullet: level.bullets){
				r4.set(bullet.position.x, bullet.position.y, bullet.dimension.x, bullet.dimension.y);
				if(r2.overlaps(r4) && bullet.bulletType == BulletType.PLAYER){
					if(enemy.state != State.DYING){
						enemy.onCollision();
						bullet.state = State.DEAD;
						System.out.println("Collision of enemy with bullet.");
					}
				}
			}
		}
	}
	
	
	public void checkEnemyBulletPlayerCollision() {
		r1.set(level.player.position.x, level.player.position.y, level.player.dimension.x, level.player.dimension.y);
		
		for(Bullet bullet: level.bullets){
			r3.set(bullet.position.x, bullet.position.y, bullet.dimension.x, bullet.dimension.y);
			if(r1.overlaps(r3) && (bullet.bulletType == BulletType.ENEMY || bullet.bulletType == BulletType.ENEMY_SMART)){
				level.player.decreaseLives(1);
				bullet.state = State.DEAD;
				System.out.println("Collision of player with enemy bullet.");
			}
		}
		
	}
	
	
	public void checkEnemyPlayerCollision() {
		
		r1.set(level.player.position.x, level.player.position.y, level.player.dimension.x, level.player.dimension.y);
		
		for(Enemy enemy: level.enemies){
			r2.set(enemy.position.x, enemy.position.y, enemy.dimension.x, enemy.dimension.y);
			if(r1.overlaps(r2)){
				level.player.decreaseLives(3);
				System.out.println("Collision of enemy with player.");
			}
		}
		
	}
	

	private void checkPlayerBonusCollision() {
		r1.set(level.player.position.x, level.player.position.y, level.player.dimension.x, level.player.dimension.y);
		
		for(Bonus bonus: level.bonuses){
			r2.set(bonus.position.x, bonus.position.y, bonus.dimension.x, bonus.dimension.y);
			if(r1.overlaps(r2)){
				bonus.onCollision();
				System.out.println("Collision of player with Bonus.");
			}
		}
		
	}
	

	public boolean isInScreen(AbstractGameObject obj) {
		return ((obj.position.x>=-Constants.VIEWPORT_WIDTH/2 && obj.position.x<=Constants.VIEWPORT_WIDTH/2)
				&&
				(obj.position.y>=level.start && obj.position.y<=level.end));
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.ESCAPE || keycode == Keys.BACK) {
			Gdx.app.exit();
		}
		return false;
	}

	private void handleGameInput(float deltaTime) {

		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
			level.player.velocity.x = -Constants.PLANE_H_SPEED;
		} else if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
			level.player.velocity.x = Constants.PLANE_H_SPEED;
		} else {
			level.player.velocity.x = 0;
		}
		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
			level.player.velocity.y = Constants.PLANE_MAX_V_SPEED;
		} else if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
			level.player.velocity.y = Constants.PLANE_MIN_V_SPEED;
		} else {
			level.player.velocity.y = Constants.SCROLL_SPEED;
		}
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			level.player.shoot();
		}
	}

	private void handleDebugInput(float deltaTime) {
		if (Gdx.app.getType() != ApplicationType.Desktop) return;

		if (Gdx.input.isKeyPressed(Keys.ENTER)) {
			cameraHelper.setTarget(!cameraHelper.hasTarget() ? level : null);
		}
		
		//jump to levels
		if(Gdx.input.isKeyPressed(Keys.K)){ level.enemies.clear();}

		if (!cameraHelper.hasTarget()) {
			// Camera Controls (move)
			float camMoveSpeed = 5 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;
			if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camMoveSpeed *= camMoveSpeedAccelerationFactor;
			if (Gdx.input.isKeyPressed(Keys.LEFT)) moveCamera(-camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.RIGHT)) moveCamera(camMoveSpeed, 0);
			if (Gdx.input.isKeyPressed(Keys.UP)) moveCamera(0, camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.DOWN)) moveCamera(0, -camMoveSpeed);
			if (Gdx.input.isKeyPressed(Keys.BACKSPACE)) cameraHelper.reset();
		}

		// Camera Controls (zoom)
		float camZoomSpeed = 1 * deltaTime;
		float camZoomSpeedAccelerationFactor = 5;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)) camZoomSpeed *= camZoomSpeedAccelerationFactor;
		if (Gdx.input.isKeyPressed(Keys.COMMA)) cameraHelper.addZoom(camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.PERIOD)) cameraHelper.addZoom(-camZoomSpeed);
		if (Gdx.input.isKeyPressed(Keys.SLASH)) cameraHelper.setZoom(1);
	}

	private void moveCamera(float x, float y) {
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		// TODO - implement touch pad type controls
		return true;
	}

}
