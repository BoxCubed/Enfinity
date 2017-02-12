package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.States.GameStateManager;
import com.mygdx.game.States.Playstate;

public class Enfinity extends ApplicationAdapter {
	SpriteBatch batch;
	OrthographicCamera cam;
	Vector3 camPos;	//Camera position
	Texture image;
	GameStateManager gsm;
	public static final int PPM = 150;
	@Override
	public void create () {
		batch = new SpriteBatch();
		cam = new OrthographicCamera();
		cam.setToOrtho(false, 300, 100);
		gsm = new GameStateManager();
		cam.position.set(0, 0, 0);
		gsm.push(new Playstate(gsm));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());//Calls the gsm so that it can update all the states.
        batch.setProjectionMatrix(cam.combined);
		batch.begin();
		gsm.render(batch);
		batch.end();
		update();
	}
	public void update(){
        cam.update();
    }
	@Override
	public void dispose () {
		batch.dispose();
	}
}
