package com.stackberry.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    public static final Properties properties = new Properties();

    static {
        try (InputStream inputStream = ConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")){
            if (inputStream == null){
                System.out.println("Não foi possível achar o arquvio application.properties");
            } else {
                properties.load(inputStream);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    // Vai buscar minha propriedade pela chave
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
