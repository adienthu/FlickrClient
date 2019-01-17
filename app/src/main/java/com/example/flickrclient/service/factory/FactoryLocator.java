package com.example.flickrclient.service.factory;

public class FactoryLocator {
    static Factory sFactory;

    public static Factory getFactory() {
        return sFactory;
    }

    public static void Register(Factory factory)
    {
        if (sFactory == null)
            sFactory = factory;
    }
}
