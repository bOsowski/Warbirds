package wit.cgd.warbirds.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import wit.cgd.warbirds.game.util.Constants;

public class Assets implements Disposable, AssetErrorListener {

	public static final String	TAG			= Assets.class.getName();
	public static final Assets	instance	= new Assets();

	private AssetManager		assetManager;

	public AssetFonts			fonts;

	public AssetSounds			sounds;
	public AssetMusic			music;

	public HashMap<String, AssetEnemy>			enemies;
	public AssetPlayer			player;
	public Asset				bullet;
	public Asset				doubleBullet;
	public AssetBonus			bonus;
	public AssetLevelDecoration	levelDecoration;

	private Assets() {}

	public void init(AssetManager assetManager) {

		this.assetManager = assetManager;
		assetManager.setErrorListener(this);

		// load texture for game sprites
		assetManager.load(Constants.TEXTURE_ATLAS_GAME, TextureAtlas.class);

		// TODO load sounds
		assetManager.load("sounds/player_bullet_01.wav", Sound.class);
		assetManager.load("sounds/player_hit.wav", Sound.class);
		assetManager.load("sounds/enemy_shoot.wav", Sound.class);
		assetManager.load("sounds/explosion.wav", Sound.class);
		assetManager.load("sounds/explosion_large.wav", Sound.class);
		assetManager.load("sounds/level_finished.wav", Sound.class);
		assetManager.load("sounds/277403__landlucky__game-over-sfx-and-voice.wav", Sound.class);

		// load music
		assetManager.load("music/Mystery_Mammal_-_11_-_All_Your_Organs_Get_A_Laugh.mp3", Music.class);

		assetManager.finishLoading();


		Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assetManager.getAssetNames())
			Gdx.app.debug(TAG, "asset: " + a);

		// create atlas for game sprites
		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_GAME);
		for (Texture t : atlas.getTextures()){
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
		
		fonts = new AssetFonts();

		// create game resource objects
		player = new AssetPlayer(atlas);
		
		enemies = new HashMap<String, AssetEnemy>();
		
		enemies.put("enemy_plane_green", new AssetEnemy(atlas, "enemy_plane_green"));
		enemies.put("enemy_plane_white", new AssetEnemy(atlas, "enemy_plane_white"));
		enemies.put("enemy_plane_yellow", new AssetEnemy(atlas, "enemy_plane_yellow"));
		levelDecoration = new AssetLevelDecoration(atlas);
		bonus = new AssetBonus(atlas);
		bullet = new Asset(atlas, "bullet");
		doubleBullet  = new Asset(atlas, "bullet_double");
		
		// create sound and music resource objects
		sounds = new AssetSounds(assetManager);
		music = new AssetMusic(assetManager);

	}

	@Override
	public void dispose() {
		assetManager.dispose();
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		Gdx.app.error(TAG, "Couldn't load asset '" + asset + "'", (Exception) throwable);
	}

	public class Asset {
		public final AtlasRegion	region;

		public Asset(TextureAtlas atlas, String imageName) {
			region = atlas.findRegion(imageName);
			Gdx.app.log(TAG, "Loaded asset '" + imageName + "'");
		}
	}

	public class AssetPlayer {
		public final AtlasRegion	region;
		public final Animation		animationNormal;
		public final Animation		animationExplosionBig;

		public AssetPlayer(TextureAtlas atlas) {
			region = atlas.findRegion("player");

			Array<AtlasRegion> regions = atlas.findRegions("player");
			animationNormal = new Animation(1.0f / 15.0f, regions, Animation.PlayMode.LOOP);
			regions = atlas.findRegions("explosion_big");
			animationExplosionBig = new Animation(1.0f / 15.0f, regions, Animation.PlayMode.LOOP);
		}
	}
	
	public class AssetEnemy{
		public final AtlasRegion 	region;
		public final Animation		animationNormal;
		public final Animation 		animationExplosionBig;
		
		public AssetEnemy(TextureAtlas atlas, String enemyName){
			region = atlas.findRegion(enemyName);
			
			Array<AtlasRegion> regions = atlas.findRegions(enemyName);
			animationNormal = new Animation(1.0f/15.0f, regions, Animation.PlayMode.LOOP);
			regions = atlas.findRegions("explosion_big");
			animationExplosionBig = new Animation(1.0f/15.0f, regions, Animation.PlayMode.LOOP);
		}
	}

	public class AssetLevelDecoration {

		public final AtlasRegion	islandBig;
		public final AtlasRegion	islandSmall;
		public final AtlasRegion	islandTiny;
		public final AtlasRegion	water;

		public AssetLevelDecoration(TextureAtlas atlas) {
			water = atlas.findRegion("water");
			islandBig = atlas.findRegion("island_big");
			islandSmall = atlas.findRegion("island_small");
			islandTiny = atlas.findRegion("island_tiny");
		}
	}
	                                                             
	public class AssetBonus{
		public final AtlasRegion extraLife;
		public final Animation	extraLife_animation;
		
		public AssetBonus(TextureAtlas atlas){
			//life bonus
			extraLife = atlas.findRegion("life_up");
			Array<AtlasRegion> regions = atlas.findRegions("life_up");
			extraLife_animation = new Animation(1.0f/15.0f, regions, Animation.PlayMode.LOOP);

		}
	}


	public class AssetFonts {
		public final BitmapFont	defaultSmall;
		public final BitmapFont	defaultNormal; 
		public final BitmapFont	defaultBig;

		public AssetFonts() {
			// create three fonts using Libgdx's built-in 15px bitmap font
			defaultSmall = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"), true);
			defaultNormal = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"), true);
			defaultBig = new BitmapFont(Gdx.files.internal("images/arial-15.fnt"), true);
			// set font sizes
			defaultSmall.getData().setScale(1.0f);
			defaultNormal.getData().setScale(2.5f);
			defaultBig.getData().setScale(4.0f);
			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

	public class AssetSounds {

		// TODO list reference to sound assets
		 public final Sound playerShoot;
		 public final Sound playerHit;
		 public final Sound enemyShoot;
		 public final Sound explosion;
		 public final Sound explosionLarge;
		 public final Sound levelFinished;
		 public final Sound gameOver;

		public AssetSounds(AssetManager am) {
			// TODO 
			playerShoot = am.get("sounds/player_bullet_01.wav", Sound.class);
			playerHit = am.get("sounds/player_hit.wav", Sound.class);
			enemyShoot = am.get("sounds/enemy_shoot.wav", Sound.class);
			explosion = am.get("sounds/explosion.wav", Sound.class);
			explosionLarge = am.get("sounds/explosion_large.wav", Sound.class);
			levelFinished = am.get("sounds/level_finished.wav", Sound.class);
			gameOver = am.get("sounds/277403__landlucky__game-over-sfx-and-voice.wav", Sound.class);
		}
	}
	

	public class AssetMusic {
		// TODO list reference to music assets
		 public final Music song01;

		public AssetMusic(AssetManager am) {
			// TODO
			 song01 = am.get("music/Mystery_Mammal_-_11_-_All_Your_Organs_Get_A_Laugh.mp3", Music.class);
		}
	}

}
