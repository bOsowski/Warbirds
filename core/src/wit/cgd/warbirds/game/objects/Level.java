package wit.cgd.warbirds.game.objects;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.Pool;

import wit.cgd.warbirds.game.WorldController;
import wit.cgd.warbirds.game.objects.AbstractGameObject.State;
import wit.cgd.warbirds.game.util.Constants;

public class Level extends AbstractGameObject {

	public static final String	TAG		= Level.class.getName();

	public Player				player	= null;
	public LevelDecoration		levelDecoration;
	public float				start;
	public float				end;
	public ArrayList<Enemy>		enemies = new ArrayList<Enemy>();;
	private int					seed;
	public ArrayList<Bonus>		bonuses;

	public final Array<Bullet> bullets = new Array<Bullet>();
	public final Pool<Bullet> bulletPool = new Pool<Bullet>() {
	
    	@Override
    	protected Bullet newObject() {
    		return new Bullet(level);
    	}
    };
    
	/**
	 * Simple class to store generic object in level.
	 */
	public static class LevelObject {
		String	name;
		int		x;
		int		y;
		float	rotation;
		int		state;
	}

	/**
	 * Collection of all objects in level
	 */
	public static class LevelMap {
		ArrayList<Seed>			islands;
		ArrayList<LevelObject>	enemies;
		String					name;
		float					length;
	}

	
	public static class Seed{
		int seed;
	}

	public Level(int levelNumber) {
		super(null);
		init(levelNumber);

	}

	private void init(int levelNumber) {

		// player
		player = new Player(this);
		player.position.set(0, 0);

		levelDecoration = new LevelDecoration(this);
		
		bonuses = new ArrayList<Bonus>();

		// read and parse level map (form a json file)
		String map = Gdx.files.internal("levels/level-"+levelNumber+".json").readString();

		Json json = new Json();
		json.setElementType(LevelMap.class, "enemies", LevelObject.class);
		LevelMap data = new LevelMap();
		data = json.fromJson(LevelMap.class, map);
		
		Gdx.app.log(TAG, "Data name = " + data.name);
		Gdx.app.log(TAG, "islands . . . ");
		for (Object e : data.islands) {
			Seed s = (Seed) e;
			seed = s.seed;
			System.out.println("SEED: "+data.name);
		}
		Random rand = new Random(seed);
		for(float x = 0, y = 0; y <= 250 ;y+=2 + rand.nextFloat() *(2)){
			x = -3.5f + rand.nextFloat() *(3.5f + 3.5f);
			
			ArrayList<String> decorationNames = new ArrayList<String>();
			decorationNames.add("islandSmall");
			decorationNames.add("islandTiny");
			decorationNames.add("islandBig");
			
			levelDecoration.add(decorationNames.get(rand.nextInt(3)), x, y, rand.nextInt(360));
		}
		Gdx.app.log(TAG, "enemies . . . ");
		for (Object e : data.enemies) {
			LevelObject p = (LevelObject) e;
			Gdx.app.log(TAG, "type = " + p.name + "\tx = " + p.x + "\ty =" + p.y);
			// TODO add enemies
			//System.out.println("enemy added   "+p.name);
			if(p.name.equals("enemy_plane_green")){
				enemies.add(new EnemyPlaneGreen(this, p.name, new Vector2(p.x, p.y)));
			}
			else if(p.name.equals("enemy_plane_white")){
				enemies.add(new EnemyPlaneWhite(this, p.name, new Vector2(p.x, p.y)));
			}
			else if(p.name.equals("enemy_plane_yellow")){
				enemies.add(new EnemyPlaneYellow(this, p.name, new Vector2(p.x, p.y)));
			}
			else if(p.name.equals("boss")){
				enemies.add(new Boss(this, "enemy_plane_white", new Vector2(p.x, p.y)));
			}

		}
		

		position.set(0, 0);
		velocity.y = Constants.SCROLL_SPEED;
		state = State.ACTIVE;

	}

	public void update(float deltaTime) {

		super.update(deltaTime);
		
		// limits for rendering
		start = position.y - scale.y * Constants.VIEWPORT_HEIGHT;
		end = position.y + scale.y * Constants.VIEWPORT_HEIGHT;

		player.update(deltaTime);
		
		for (Bullet bullet: bullets)
			bullet.update(deltaTime);
		
		for(Enemy enemy: enemies){
			enemy.update(deltaTime);
		}
		
		for(Bonus bonus: bonuses){
			bonus.update(deltaTime);
		}
		
	}

	public void render(SpriteBatch batch) {

		levelDecoration.render(batch);
		player.render(batch);
		
		for (Bullet bullet: bullets)
			bullet.render(batch);
		
		for(Enemy enemy: enemies){
			enemy.render(batch);
		}
		
		for(Bonus bonus: bonuses){
			bonus.render(batch);
		}


	}

}
