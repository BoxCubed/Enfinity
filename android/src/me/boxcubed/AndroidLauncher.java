package me.boxcubed;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import me.boxcubed.android.AndroidAccess;

public class AndroidLauncher extends AndroidApplication {
	AndroidAccess access;
	Enfinity enfinity;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		access = new AndroidAccess(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		enfinity = new Enfinity();
		initialize(enfinity, config);
		enfinity.addAndroidAPI(access);
	}
}
