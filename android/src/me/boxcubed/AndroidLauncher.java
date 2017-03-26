package me.boxcubed;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import me.boxcubed.android.AndroidAccess;

public class AndroidLauncher extends AndroidApplication {
	AndroidAccess access;
	Enfinity enfinity;
	private SensorManager mSensorManager;
	private Sensor mSensor;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		access = new AndroidAccess(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		//if(Gdx.input.isPeripheralAvailable(Input.Peripheral.Accelerometer))
		config.useAccelerometer = true;
		enfinity = new Enfinity();
		initialize(enfinity, config);
		enfinity.addAndroidAPI(access);
	}
}
