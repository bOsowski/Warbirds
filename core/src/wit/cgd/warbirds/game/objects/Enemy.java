package wit.cgd.warbirds.game.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.objects.AbstractGameObject.State;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.Constants;

public abstract class Enemy extends AbstractGameObject{
	
	protected String enemyName;
	protected Animation animation;
	protected float timeShootDelay;
	private TextureRegion region;
	private boolean	wasInScreen = false;

	public Enemy(Level level, String enemyName, Vector2 position){
		super(level);
		this.enemyName = enemyName;
		super.position = position;
		init();
	}

	public void render (SpriteBatch batch) {
		
		region = (TextureRegion) animation.getKeyFrame(stateTime, true);

		batch.draw(region.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, 
			dimension.x, dimension.y, scale.x, scale.y, rotation, 
			region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
			false, false);
	}
	
	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		timeShootDelay -= deltaTime;
		
		if(isInScreen()){
			wasInScreen = true;
		}
		else if(!isInScreen() && wasInScreen){
			state = State.DEAD;
		}
	}
	
	public void init() {
		dimension.set(0.5f,0.5f);
		
		animation = Assets.instance.enemies.get(enemyName).animationNormal;
		setAnimation(animation);
		
		//Center image on game object
		origin.set(dimension.x/2, dimension.y/2);
		state = State.ACTIVE;
		
		timeShootDelay = 0;
	}
	
	public void onCollision(){
		animation = Assets.instance.enemies.get(enemyName).animationExplosionBig;
		state = State.DYING;
		timeToDie = Constants.ENEMY_DIE_DELAY;
		AudioManager.instance.play(Assets.instance.sounds.explosion);
	}
	
	protected void shoot(){
		AudioManager.instance.play(Assets.instance.sounds.enemyShoot);
	};
	
}
