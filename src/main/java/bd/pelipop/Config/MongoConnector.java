package bd.pelipop.Config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;

public class MongoConnector {

    public static void main(String[] args) {
        // --- 1. CONFIGURA TU CADENA DE CONEXIÓN ---
        // ¡NUNCA pongas tus credenciales directamente en el código en producción!
        // Usa variables de entorno o un sistema de configuración.

        // Ejemplo para MongoDB Atlas (recomendado)
        // Reemplaza <usuario>, <password> y <cluster-url> con tus datos.
        String connectionString = "mongodb+srv://Danieloid:vV4IEO9Aw1RQaYXV@danieloide.pjftt3a.mongodb.net/?appName=Danieloide";

        // Ejemplo para una base de datos local
        // String connectionString = "mongodb://localhost:27017";

        // --- 2. CREA LA CONFIGURACIÓN DEL CLIENTE Y CONECTA ---
        // El bloque try-with-resources se asegura de cerrar la conexión al final.
        ServerApi serverApi = ServerApi.builder()
                .version(ServerApiVersion.V1)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .serverApi(serverApi)
                .build();

        try (MongoClient mongoClient = MongoClients.create(settings)) {

            // --- 3. VERIFICA LA CONEXIÓN ---
            // Intenta enviar un "ping" para confirmar que la conexión fue exitosa.
            try {
                MongoDatabase database = mongoClient.getDatabase("PeliPop"); // Obtiene tu base de datos
                database.runCommand(new Document("ping", 1));
                System.out.println("¡Ping exitoso! Conectado correctamente a la base de datos 'PeliPop'.");

                // --- 4. OBTÉN UNA COLECCIÓN Y REALIZA OPERACIONES ---
                // Ahora puedes trabajar con tus colecciones.
                // Por ejemplo, la colección donde guardarás las estadísticas.
                MongoCollection<Document> collection = database.getCollection("estadisticas_peliculas");

                // Ejemplo: Contar cuántos documentos hay en la colección
                long documentCount = collection.countDocuments();
                System.out.println("La colección 'estadisticas_peliculas' tiene " + documentCount + " documentos.");

                // Ejemplo: Encontrar el primer documento de la colección
                Document primerDocumento = collection.find().first();
                if (primerDocumento != null) {
                    System.out.println("Primer documento encontrado: " + primerDocumento.toJson());
                } else {
                    System.out.println("La colección está vacía.");
                }

            } catch (MongoException e) {
                System.err.println("Error al conectar o interactuar con MongoDB: " + e.getMessage());
                e.printStackTrace();
            }

        } // El cliente se cierra automáticamente aquí.
    }
}
