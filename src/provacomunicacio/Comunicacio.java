/*
 * Universitat de les Illes Balears
 */
package provacomunicacio;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 *
 * @author Antoni
 */
public class Comunicacio {

    private final static String HOST = "https://programacio.ltim.uib.es";
    private final static URI CREATE_URI;
    private final static URI JOIN_URI;
    private final static URI SEND_URI;
    private final static URI RECEIVE_URI;
    private final static HttpClient CLIENT;
    
    static {
        // superxapussa perquè reconegui el certificat SSL
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            CLIENT = HttpClient.newBuilder()
                    .sslContext(sslContext)
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();
            try {
                CREATE_URI  = new URI(HOST + "/create");
                JOIN_URI    = new URI(HOST + "/join");
                SEND_URI    = new URI(HOST + "/send");
                RECEIVE_URI = new URI(HOST + "/receive");
            } catch (URISyntaxException e) {
                throw new Error(e);
            }
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new Error(e);
        }
    }
    
    /**
     * Crea una sala de comunicació.
     * @return la informació de la sala: `id`\n`password`
     */
    public static String[] create() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(CREATE_URI)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            return responseBody.split("\n");
        } catch (IOException | InterruptedException e) {
            throw new Error(e);
        }
    }
    
    /**
     * Crea una sala de comunicació especificant si es vol contrasenya.
     * @param passwordRequired si es vol contrasenya o no
     * @return la informació de la sala: `id`\n`password` si es volia contrasenya, si no, només l'`id`
     */
    public static String[] create(boolean passwordRequired) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(CREATE_URI)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("passwordRequired=" + passwordRequired))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            return responseBody.split("\n");
        } catch (IOException | InterruptedException e) {
            throw new Error(e);
        }
    }
    
    /**
     * Crea una sala de comunicació amb la contrasenya especificada.
     * @param password la contrasenya que es vol
     * @return la informació de la sala: `id`\n`password`
     */
    public static String[] create(String password) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(CREATE_URI)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString("password=" + password))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            return responseBody.split("\n");
        } catch (IOException | InterruptedException e) {
            throw new Error(e);
        }
    }
    
    /**
     * Intenta unir-se a una sala de comunicació.
     * @param roomId identificador de la sala
     * @param password contrasenya de la sala (`null` si no en té)
     * @return un text `OK` si ha anat bé o un missatge d'error
     */
    public static String join(String roomId, String password) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(JOIN_URI)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            String.format("id=%s&password=%s", 
                                    roomId, 
                                    password)))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new Error(e);
        }
    }
    
    /**
     * Envia un missatge a una sala.
     * @param roomId identificador de la sala
     * @param password contrasenya de la sala (`null` si no en té)
     * @param message el missatge que se vol enviar
     */
    public static void send(String roomId, String password, String message) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(SEND_URI)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            String.format("id=%s&password=%s&message=%s", 
                                    roomId, 
                                    password, 
                                    message)))
                    .build();
            HttpResponse<Void> response = CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException e) {
            throw new Error(e);
        }
    }
    
    /**
     * Rep un missatge d'una sala.
     * @param roomId identificador de la sala
     * @param password contrasenya de la sala (`null` si no en té)
     * @return el primer missatge pendent de llegir o `null` si no en quedaven
     */
    public static String receive(String roomId, String password) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(RECEIVE_URI)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            String.format("id=%s&password=%s", 
                                    roomId, 
                                    password)))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            final String body = response.body();
            if (body.equals("__NULL__")) {
                return null;
            } else {
                return body;
            }
        } catch (IOException | InterruptedException e) {
            throw new Error(e);
        }
    }
}
