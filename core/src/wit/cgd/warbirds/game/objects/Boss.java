package wit.cgd.warbirds.game.objects;

import com.badlogic.gdx.math.Vector2;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.AbstractGameObject.State;
import wit.cgd.warbirds.game.objects.Bullet.BulletType;
import wit.cgd.warbirds.game.util.Constants;

public class Boss extends Enemy{
	
	private int hitpoints = 25;

	public Boss(Level level, String enemyName, Vector2 position) {
		super(level, enemyName, position);
	}
	
	@Override
	public void update(float deltaTime){
		if(isInScreen()){
			super.update(deltaTime);
			position.y = position.y + 0.0166f;
			shoot(); 
		}
	}
	
	@Override
	public void init() {
		dimension.set(2f,2f);
		
		animation = Assets.instance.enemies.get(enemyName).animationNormal;
		setAnimation(animation);
		
		//Center image on game object
		origin.set(dimension.x/2, dimension.y/2);
		state = State.ACTIVE;
		
		timeShootDelay = 0;
	}
	
	@Override
	protected void shoot(){
		if(timeShootDelay > 0){
			return;
		}
		
		Bullet bulletLeft = level.bulletPool.obtain();
		Bullet bulletMiddle = level.bulletPool.obtain();
		Bullet bulletRight = level.bulletPool.obtain();

		bulletLeft.reset();
		bulletLeft.setType(Bullet.BulletType.ENEMY);
		bulletLeft.position.set(new Vector2(position.x-0.7f, position.y));
		level.bullets.add(bulletLeft);
		
		bulletMiddle.reset();
		bulletMiddle.setType(Bullet.BulletType.ENEMY);
		bulletMiddle.position.set(position);
		level.bullets.add(bulletMiddle);
		
		bulletRight.reset();
		bulletRight.setType(Bullet.BulletType.ENEMY);
		bulletRight.position.set(new Vector2(position.x+0.7f, position.y));
		level.bullets.add(bulletRight);
		
		super.shoot();
		
		timeShootDelay = Constants.ENEMY_SHOOT_DELAY-1;
	}
	
	@Override
	public void onCollision(){
		hitpoints--;
		if(hitpoints <= 0){
			super.onCollision();
		}
	}
	
	@Override
	public boolean isInScreen()  {
		return ((position.x>-Constants.VIEWPORT_WIDTH/2 && position.x<Constants.VIEWPORT_WIDTH/2) && 
				(position.y>level.start && position.y+2<level.end));
	}

}
