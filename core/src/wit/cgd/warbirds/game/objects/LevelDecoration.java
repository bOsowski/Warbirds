package wit.cgd.warbirds.game.objects;

import wit.cgd.warbirds.game.Assets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import wit.cgd.warbirds.game.util.Constants;

public class LevelDecoration extends AbstractGameObject {

	private TextureRegion water;
	private TextureRegion islandBig;
	private TextureRegion islandSmall;
	private TextureRegion islandTiny;

	public Array<Island> islands;

	private class Island extends AbstractGameObject {
		private TextureRegion region;
		
		public Island(Level level, TextureRegion region) {
			super(level);
			this.region = region;
		}
		
		public void render (SpriteBatch batch) {
			batch.draw(region.getTexture(), 
				position.x + origin.x, position.y + origin.y,
				origin.x, origin.y, dimension.x, dimension.y,
				scale.x, scale.y, rotation, 
				region.getRegionX(), region.getRegionY(), region.getRegionWidth(), region.getRegionHeight(), 
				false, false);
		}
	}
		
	public LevelDecoration (Level level) {
		super(level);
		init();
	}

	private void init () {
		dimension.set(1, 1);

		islandBig = Assets.instance.levelDecoration.islandBig;
		islandSmall = Assets.instance.levelDecoration.islandSmall;
		islandTiny = Assets.instance.levelDecoration.islandTiny;
		water = Assets.instance.levelDecoration.water;
		
		islands = new Array<Island>();
	}

	@Override
	public void render(SpriteBatch batch) {
		TextureRegion region = water;
		int t =  (int) (Constants.VIEWPORT_HEIGHT*scale.x/scale.y);
		
		// water
		for (int k=(int) level.start; k<level.end; ++k) 
			for (int c=-((int) Constants.VIEWPORT_WIDTH/2); c<(int) (Constants.VIEWPORT_WIDTH/2); ++c) batch.draw(region.getTexture(), 
				origin.x + position.x+c, origin.y + position.y+k, 
				origin.x, origin.y, 
				1.1f, 1.1f, 
				1, 1, rotation, 
				region.getRegionX(), region.getRegionY(),
				region.getRegionWidth(), region.getRegionHeight(), false, false);

		for (Island island: islands) {
			region = island.region;
			if (island.position.y<level.start || island.position.y>level.end) continue;
			batch.draw(region.getTexture(), 
					island.position.x-island.origin.x, island.position.y-island.origin.y, 
					island.origin.x, island.origin.y, 
					1.1f, 1.1f, 
					1, 1, island.rotation, 
					region.getRegionX(), region.getRegionY(),
					region.getRegionWidth(), region.getRegionHeight(), false, false);
		}
		
		// islands
	
	}

	public void add(String name, float x, float y, float rotation) {
		Island island = null;
		if (name.equals("islandBig")) {
			island = new Island(level, islandBig);
		} else if (name.equals("islandSmall")) {
			island = new Island(level, islandSmall);
		} else if (name.equals("islandTiny")) {
			island = new Island(level, islandTiny);
		}
		island.origin.x = island.dimension.x/2; 
		island.origin.y = island.dimension.y/2; 
		island.position.set(x,y);
		island.rotation = rotation;
		islands.add(island);
	}
}
