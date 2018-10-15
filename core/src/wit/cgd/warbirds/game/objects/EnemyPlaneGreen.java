package wit.cgd.warbirds.game.objects;

import com.badlogic.gdx.math.Vector2;

import wit.cgd.warbirds.game.util.Constants;

public class EnemyPlaneGreen extends Enemy{

	public EnemyPlaneGreen(Level level, String enemyName, Vector2 position) {
		super(level, enemyName, position);
	}

	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		if(isInScreen()){
			position.y = position.y - 0.0166f;
			shoot();
		}
	}
	
	
	
	@Override
	protected void shoot() {

		if (timeShootDelay>0) return;
		// get bullet
		Bullet bullet = level.bulletPool.obtain();
		//Bullet bullet = new Bullet(level);
		bullet.reset();
		bullet.setType(Bullet.BulletType.ENEMY);
		bullet.position.set(position);
		level.bullets.add(bullet);
		
		super.shoot();
		
		timeShootDelay = Constants.ENEMY_SHOOT_DELAY;
	}
}
