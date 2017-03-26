package me.boxcubed;

import com.badlogic.gdx.Game;

import me.boxcubed.platform.AndroidAPI;

public class Enfinity extends Game  {
	public static Enfinity instance;
	public static AndroidAPI androidAPI;

	public Enfinity() {
		instance = this;

	}

	public void addAndroidAPI(AndroidAPI api) {
		androidAPI = api;

	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
		setScreen(new GameScreen());
	}

}
