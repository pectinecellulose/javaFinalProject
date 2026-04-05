import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Gestion de la configuration de la base de données via fichier .env
 */
public class DatabaseConfig {
    private static Properties properties;
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        properties = new Properties();
        try {
            // Essayer de charger le fichier .env
            FileInputStream fis = new FileInputStream(".env");
            properties.load(fis);
            fis.close();
            System.out.println("Configuration chargee depuis le fichier .env");
        } catch (IOException e) {
            System.out.println("AVERTISSEMENT: Fichier .env non trouve. Utilisation des valeurs par defaut.");
            System.out.println("Pour configurer la base de donnees, copiez .env.example en .env");
            
            // Valeurs par defaut
            properties.setProperty("DB_HOST", "localhost");
            properties.setProperty("DB_PORT", "3306");
            properties.setProperty("DB_NAME", "bibliotheque");
            properties.setProperty("DB_USER", "root");
            properties.setProperty("DB_PASSWORD", "");
            properties.setProperty("MYSQL_DRIVER", "com.mysql.cj.jdbc.Driver");
            properties.setProperty("DB_USE_SSL", "false");
            properties.setProperty("DB_ALLOW_PUBLIC_KEY_RETRIEVAL", "true");
            properties.setProperty("DB_SERVER_TIMEZONE", "UTC");
        }
    }
    
    public static String getDbHost() {
        return properties.getProperty("DB_HOST", "localhost");
    }
    
    public static String getDbPort() {
        return properties.getProperty("DB_PORT", "3306");
    }
    
    public static String getDbName() {
        return properties.getProperty("DB_NAME", "bibliotheque");
    }
    
    public static String getDbUser() {
        return properties.getProperty("DB_USER", "root");
    }
    
    public static String getDbPassword() {
        return properties.getProperty("DB_PASSWORD", "");
    }
    
    public static String getMysqlDriver() {
        return properties.getProperty("MYSQL_DRIVER", "com.mysql.cj.jdbc.Driver");
    }
    
    public static String getDbUseSSL() {
        return properties.getProperty("DB_USE_SSL", "false");
    }
    
    public static String getDbAllowPublicKeyRetrieval() {
        return properties.getProperty("DB_ALLOW_PUBLIC_KEY_RETRIEVAL", "true");
    }
    
    public static String getDbServerTimezone() {
        return properties.getProperty("DB_SERVER_TIMEZONE", "UTC");
    }
    
    /**
     * Construit l'URL de connexion JDBC complete
     */
    public static String getJdbcUrl() {
        return String.format("jdbc:mysql://%s:%s/%s?useSSL=%s&allowPublicKeyRetrieval=%s&serverTimezone=%s",
                getDbHost(),
                getDbPort(),
                getDbName(),
                getDbUseSSL(),
                getDbAllowPublicKeyRetrieval(),
                getDbServerTimezone());
    }
    
    /**
     * Affiche la configuration actuelle (sans afficher le mot de passe)
     */
    public static void displayConfig() {
        
    }
}
