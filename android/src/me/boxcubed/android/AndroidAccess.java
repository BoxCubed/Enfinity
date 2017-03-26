package me.boxcubed.android;

import android.widget.Toast;

import com.badlogic.gdx.Gdx;

import me.boxcubed.AndroidLauncher;
import me.boxcubed.platform.AndroidAPI;

/**
 * Created by ryan9_000 on 26/03/2017.
 */

public class AndroidAccess implements AndroidAPI {
    AndroidLauncher launcher;


    public AndroidAccess(AndroidLauncher launcher) {
        this.launcher = launcher;

    }

    @Override
    public void makeToast(final String string) {
        launcher.handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(launcher, string, Toast.LENGTH_LONG).show();
                Gdx.app.log("[Android]", "sent toast : " + string);
            }
        });


    }
}
