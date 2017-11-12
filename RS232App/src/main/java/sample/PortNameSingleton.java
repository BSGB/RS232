package sample;

import lombok.Getter;
import lombok.Setter;

public class PortNameSingleton {
    private static PortNameSingleton ourInstance = new PortNameSingleton();

    public static PortNameSingleton getInstance() {
        return ourInstance;
    }

    private PortNameSingleton() {
    }

    @Setter@Getter
    private String portName;
}
