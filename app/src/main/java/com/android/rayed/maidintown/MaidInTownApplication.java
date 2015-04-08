package com.android.rayed.maidintown;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Rayed on 3/22/2015.
 */
public class MaidInTownApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "iJv9mYztS0XJGsaaJzyYkvgAOHwPUKcFkOadkxc5", "91IjH5pO0mAhxmaiFiMhYc77Y1BjUmKvdfOrwxjr");

    }
}
