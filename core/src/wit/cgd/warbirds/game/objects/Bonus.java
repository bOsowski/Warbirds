package wit.cgd.warbirds.game.objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Bonus extends AbstractGameObject{
	
	private TextureRegion region;
	protected Animation animation;
	
	public Bonus(Level level, Vector2 position) {
		super(level);
		this.position = position;
	}


	@Override
	public void render(SpriteBatch batch) {
		region = (TextureRegion) animation.getKeyFrame(stateTime, true);

		batch.draw(region.getTexture(), position.x-origin.x, position.y-origin.y, origin.x, origin.y, 
			dimension.x, dimension.y, scale.x, scale.y, rotation, 
			region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
			false, false);
	}
	
	@Override
	public void update(float deltaTime){
		super.update(deltaTime);
		if(!isInScreen()){
			state = State.DEAD;
		}
	}

	public abstract void onCollision();
	
}
