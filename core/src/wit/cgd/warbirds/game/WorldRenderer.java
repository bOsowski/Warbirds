package wit.cgd.warbirds.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import wit.cgd.warbirds.game.objects.Player;
import wit.cgd.warbirds.game.util.AudioManager;
import wit.cgd.warbirds.game.util.Constants;

public class WorldRenderer implements Disposable {

	private static final String	TAG	= WorldRenderer.class.getName();

	public OrthographicCamera	camera;
	public OrthographicCamera	cameraGUI;
	
	private SpriteBatch			batch;
	private WorldController		worldController;
	
	private static	boolean		renderTimedMessage;
	private static String		renderMessage;
	private boolean 			isTimeSet = false;
	private long 				setTime;

	public WorldRenderer(WorldController worldController) {
		this.worldController = worldController;
		init();
	}

	private void init() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
		camera.position.set(0, 0, 0);
		camera.update();
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();
	}

	public void resize(int width, int height) {
		
		float scale = (float)height/(float)width;
		camera.viewportHeight = scale * Constants.VIEWPORT_HEIGHT;
		camera.update();
		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = scale*Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
		cameraGUI.update();
		
		// update level decoration
		worldController.level.levelDecoration.scale.y =  scale;
	}
	
	public void render() {
		
		// Game rendering
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.level.render(batch);
		batch.end();
		
		// GUI + HUD rendering 
		
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();
		renderLives(batch);
		if(Player.isGameOver){
			renderGameOver(batch);
			
			//make sure the game over sound is played only once.
			if(!isTimeSet){
				AudioManager.instance.play(Assets.instance.sounds.gameOver);
				isTimeSet = true;
			}
		}
			// TODO
		renderTimedText();
		batch.end();
	}

	@Override
	public void dispose() {
		batch.dispose();
	}
	
	private void renderLives(SpriteBatch spriteBatch){
        float x = cameraGUI.viewportWidth - 55*2.5f;
        float y = cameraGUI.viewportHeight - 20*2.5f;
        BitmapFont livesFont = Assets.instance.fonts.defaultNormal;
        livesFont.draw(batch, "Lives: "+Player.lives, x, y);
        livesFont.setColor(1, 1, 1, 1); // white
	}
	
	private void renderTimedText(){
		if(renderTimedMessage){
			if(!isTimeSet){
				setTime = System.currentTimeMillis();
				isTimeSet = true;
			}

			
	        BitmapFont font = Assets.instance.fonts.defaultBig;
	        GlyphLayout layout = new GlyphLayout(font, renderMessage);
	        font.draw(batch, layout, (cameraGUI.viewportWidth-layout.width)/2, (cameraGUI.viewportHeight - layout.height)/2);
	        font.setColor(0, 1f, 1f, 1); // yellowish
			
			//turn the rendering to false.
			if(System.currentTimeMillis() - setTime >= 1500){
				renderTimedMessage = false;
				isTimeSet = false;
			}
		}
	}
	
	public static void renderText(String message){
		renderMessage =  message;
		renderTimedMessage = true;
	}
	
	private void renderGameOver(SpriteBatch spriteBatch){
        BitmapFont livesFont = Assets.instance.fonts.defaultBig;
        GlyphLayout layout = new GlyphLayout(livesFont, "GAME OVER");
        livesFont.draw(batch, layout, (cameraGUI.viewportWidth-layout.width)/2, (cameraGUI.viewportHeight - layout.height)/2);
        livesFont.setColor(1, 0.2f, 0.2f, 1); // reddish
	}
}
