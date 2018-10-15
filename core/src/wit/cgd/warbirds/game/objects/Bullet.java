package wit.cgd.warbirds.game.objects;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.util.Constants;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;


public class Bullet extends AbstractGameObject implements Poolable {
	
	//private BulletType bulletType;
	
	public static enum BulletType{
		PLAYER,
		ENEMY,
		ENEMY_SMART
	}
	
	public BulletType bulletType;
	

	public static final String TAG = Player.class.getName();
	private TextureRegion region;
	
	Bullet(Level level) {
		super(level);
		init();
	}
	
	public void init() {
		dimension.set(0.5f, 0.5f);
				
		region = Assets.instance.doubleBullet.region;

		// Center image on game object
		origin.set(dimension.x / 2, dimension.y / 2);
		
	}
	
	public void setType(BulletType bulletType){
		//Different speeds based on bullet's owner
		if(bulletType == BulletType.PLAYER){
			rotation = 0;
			velocity.y = Constants.BULLET_SPEED;
			velocity.x = 0;
			region = Assets.instance.doubleBullet.region;
			this.bulletType = bulletType;
		}
		else if(bulletType == BulletType.ENEMY){
			velocity.y = -0.5f*Constants.BULLET_SPEED;
			velocity.x = 0;
			region = Assets.instance.bullet.region;
			rotation = 180;
			this.bulletType = bulletType;
		}
		else if(bulletType == BulletType.ENEMY_SMART){
			Vector2 vect = new Vector2();
			vect.x = position.x;
			vect.y = position.y;
			vect.rotate(rotation);
			vect.nor();
			velocity.y = vect.y * -Constants.BULLET_SPEED*1.3f;
			velocity.x = vect.x * -Constants.BULLET_SPEED*1.3f;
			rotation = rotation + 180;
			region = Assets.instance.bullet.region;
			this.bulletType = bulletType;
		}
	}

	

	@Override
	public void render(SpriteBatch batch) {
		batch.draw(region.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, 
				dimension.x, dimension.y, scale.x, scale.y, rotation, 
				region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
				false, false);		
	}

	@Override
	public void reset() {
		state = State.ACTIVE;
	}
}
