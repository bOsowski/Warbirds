package wit.cgd.warbirds.game.objects;

import com.badlogic.gdx.math.Vector2;

import wit.cgd.warbirds.game.Assets;
import wit.cgd.warbirds.game.util.AudioManager;

public class ExtraLife extends Bonus{

	public ExtraLife(Level level, Vector2 position) {
		super(level, position);
		init();
	}
	
	public void init(){
		dimension.set(0.5f, 0.5f);
		animation = Assets.instance.bonus.extraLife_animation;
		setAnimation(animation);
		origin.set(dimension.x/2, dimension.y/2);
	}

	@Override
	public void onCollision() {
		Player.lives++;
		state = State.DEAD;
		AudioManager.instance.play(Assets.instance.sounds.levelFinished);
	}
	
	

}
