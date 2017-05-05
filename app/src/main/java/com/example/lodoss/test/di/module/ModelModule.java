package com.example.lodoss.test.di.module;

import android.content.Context;

import com.example.lodoss.test.SieveModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Alex on 5/5/2017.
 */

@Module(includes = {ApplicationModule.class})
public class ModelModule {

    /**
     * Create single instance of model, computing list of prime numbers
     * @param context
     * @return
     */
    @Provides
    @Singleton
    SieveModel provideSieveModel(Context context){
        SieveModel model = new SieveModel(context);
        return model;
    }

}
