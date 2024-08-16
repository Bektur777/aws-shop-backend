package kg.bektur.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;

public class ConfigLoaderTest {

    @Test
    void loadConfig() {
        Dotenv dotenv = Dotenv.configure().load();

        System.out.println(dotenv.get("bektur777"));
    }
}
