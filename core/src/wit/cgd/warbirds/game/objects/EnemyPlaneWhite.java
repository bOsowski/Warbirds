package wit.cgd.warbirds.game.objects;

import com.badlogic.gdx.math.Vector2;

import wit.cgd.warbirds.game.util.Constants;

public class EnemyPlaneWhite extends Enemy{
	
	double radius = 0.5f;
	double angle = 0;
	double centerx;
	double centery;

	public EnemyPlaneWhite(Level level, String enemyName, Vector2 position) {
		super(level, enemyName, position);
		centerx = position.x;
		centery = position.y;
	}

	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		if(isInScreen()){
			position.y = position.y  - 0.0166f;
			moveSideways(deltaTime);
			shoot();
		}
	}
	
	
	public void moveSideways(float deltaTime){
		position.x =(float) ((float) centerx + radius * Math.cos(angle));
		//position.y = (float) ((float) centery + radius * Math.sin(angle));
		
		
		angle += deltaTime*3;

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

		
		timeShootDelay = Constants.ENEMY_SHOOT_DELAY-1;
	}
}