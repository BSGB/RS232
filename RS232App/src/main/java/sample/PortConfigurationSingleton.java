package sample;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

public class PortConfigurationSingleton {
    private static PortConfigurationSingleton ourInstance = new PortConfigurationSingleton();

    public static PortConfigurationSingleton getInstance() {
        return ourInstance;
    }

    private PortConfigurationSingleton() {
    }

    @Getter@Setter
    private HashMap<String, PortConfiguration> portsConfiguration = new HashMap<>();
}
