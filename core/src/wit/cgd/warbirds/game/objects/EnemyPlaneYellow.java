package wit.cgd.warbirds.game.objects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2D;

import wit.cgd.warbirds.game.util.Constants;

public class EnemyPlaneYellow extends Enemy{

	public EnemyPlaneYellow(Level level, String enemyName, Vector2 position) {
		super(level, enemyName, position);
	}

	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		if(isInScreen()){
			System.out.println("is in screen");
			aggressiveMovement(deltaTime);
			shoot();
		}
	}
	
	public float getAngle(Vector2 target) {
	    float angle = (float) Math.toDegrees(Math.atan2(target.y - position.y, target.x - position.x));

	    return angle;
	}

	
	private void moveTowardsPlayer(float deltaTime){
	    Vector2 directionToMouse = new Vector2();
	    directionToMouse.x = level.player.position.x - position.x;
	    directionToMouse.y = level.player.position.y - position.y;
	    this.position.x += directionToMouse.x * deltaTime;
	    this.position.y += directionToMouse.y * deltaTime;
	}
	
	
	private void aggressiveMovement(float deltaTime) {
		rotation = getAngle(level.player.position) + 90;//rotate the plane towards the player.
		moveTowardsPlayer(deltaTime);
	//	moveTowardsPlayerSteadily(deltaTime);
	}

	private void moveTowardsPlayerSteadily(float deltaTime) {
		Vector2 experyment = new Vector2();
		experyment.x = position.x;
		experyment.y = position.y;
		experyment.rotate(rotation);
		experyment.nor();
		position.x -= experyment.x * deltaTime;
		position.y -= experyment.y * deltaTime;	
	}

	@Override
	protected void shoot() {

		if (timeShootDelay>0) return;
		// get bullet
		Bullet bullet = level.bulletPool.obtain();
		//Bullet bullet = new Bullet(level);
		bullet.reset();
		bullet.position.set(position);
		bullet.rotation = rotation;
		bullet.setType(Bullet.BulletType.ENEMY_SMART);
		level.bullets.add(bullet);
		super.shoot();
		
		timeShootDelay = 0.2f;
	}
}