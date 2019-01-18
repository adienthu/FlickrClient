package com.example.flickrclient;

import android.app.Application;

import com.example.flickrclient.service.factory.FactoryLocator;
import com.example.flickrclient.service.factory.ServiceFactory;

public class FlickrApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FactoryLocator.Register(new ServiceFactory());
    }
}
