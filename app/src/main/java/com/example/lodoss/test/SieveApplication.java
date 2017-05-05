package com.example.lodoss.test;

import android.app.Application;

import com.example.lodoss.test.di.component.AppComponent;
import com.example.lodoss.test.di.component.DaggerAppComponent;
import com.example.lodoss.test.di.module.ApplicationModule;
import com.example.lodoss.test.di.module.ModelModule;

import javax.inject.Inject;

/**
 * Created by Alex on 5/5/2017.
 */

public class SieveApplication extends Application {

    private AppComponent component;

    @Inject
    SieveModel sieveModel;

    @Override
    public void onCreate() {
        super.onCreate();
        initDiComponent();
        component.inject(this);
    }

    private void initDiComponent(){
        component = DaggerAppComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .modelModule(new ModelModule())
                .build();
    }

    public AppComponent getComponent() {
        return component;
    }

    public SieveModel getSieveModel() {
        return sieveModel;
    }
}
