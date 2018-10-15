package wit.cgd.warbirds.game.objects;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.Constants;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

public class Player extends AbstractGameObject {

	public static final String TAG = Player.class.getName();
	
	private Animation animation;
	private TextureRegion region;
	private float timeShootDelay;
	public static int lives;
	public static boolean isGameOver = false;
	
	public Player (Level level) {
		super(level);
		init();
	}
	
	public void init() {
		dimension.set(1, 1);
		lives = 3;
		animation = Assets.instance.player.animationNormal;
		setAnimation(animation);

		// Center image on game object
		origin.set(dimension.x / 2, dimension.y / 2);
		timeShootDelay = 0;
		state = State.ACTIVE;
	}
	
	@Override
	public void update (float deltaTime) {
		super.update(deltaTime);
		position.x = MathUtils.clamp(position.x,-Constants.VIEWPORT_WIDTH/2+0.5f,Constants.VIEWPORT_WIDTH/2-0.5f);
		position.y = MathUtils.clamp(position.y,level.start+2, level.end-2);
	//	Gdx.app.log(TAG, "x Pos: " + position.x + " y Pos: "+ position.y);
		timeShootDelay -= deltaTime;
		if(lives <= 0 && state != State.DEAD && state != State.DYING){
			AudioManager.instance.play(Assets.instance.sounds.explosionLarge);
			state = State.DYING;
			timeToDie = Constants.ENEMY_DIE_DELAY*4.25f;
			animation = Assets.instance.player.animationExplosionBig;
			setAnimation(animation);
		}
		
	}

	public void shoot() {

		if (timeShootDelay>0) return;
		
		// get bullet
		Bullet bullet = level.bulletPool.obtain();
		bullet.reset();
		bullet.setType(Bullet.BulletType.PLAYER);
		bullet.position.set(position);
		level.bullets.add(bullet);
		
		AudioManager.instance.play(Assets.instance.sounds.playerShoot);
		timeShootDelay = Constants.PLAYER_SHOOT_DELAY;

	}
	
	public void decreaseLives(int livesToDecreseBy){
		if(lives > 0){
			AudioManager.instance.play(Assets.instance.sounds.playerHit);
			lives -= livesToDecreseBy;
		}
	}

	
	public void render (SpriteBatch batch) {
		
		region = (TextureRegion) animation.getKeyFrame(stateTime, true);

		batch.draw(region.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, 
			dimension.x, dimension.y, scale.x, scale.y, rotation, 
			region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
			false, false);
	}
	
}