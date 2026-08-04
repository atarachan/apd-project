package ca.senecacollege.malibuluminahotel.config;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class InjectorHolder {

    private static final Injector injector = Guice.createInjector(new AppModule());

    public static Injector getInjector() {
        return injector;
    }
}
