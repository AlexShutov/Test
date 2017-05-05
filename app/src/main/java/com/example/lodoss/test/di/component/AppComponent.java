package com.example.lodoss.test.di.component;

import android.app.Application;
import android.content.Context;

import com.example.lodoss.test.MainActivity;
import com.example.lodoss.test.SieveApplication;
import com.example.lodoss.test.di.module.ApplicationModule;
import com.example.lodoss.test.di.module.ModelModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Alex on 5/5/2017.
 */

@Singleton
@Component(modules = {
        ApplicationModule.class,
        ModelModule.class
        })

public interface AppComponent {
    Application getApplication();

    Context getContext();

    void inject(MainActivity activity);

    void inject(SieveApplication application);
}
