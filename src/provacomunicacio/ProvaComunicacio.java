/*
 * Universitat de les Illes Balears
 */
package provacomunicacio;

/**
 *
 * @author Antoni
 */
public class ProvaComunicacio {

    static String idSala;
    static String contrasenyaSala;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Aquest és un programa per provar el mòdul de comunicació.");
        
        boolean continua = true;
        do {
            System.out.println("Què vols fer?");
            System.out.println(" - [c]rear una sala");
            System.out.println(" - [u]nir-te a una sala");
            System.out.println(" - [e]nviar un missatge");
            System.out.println(" - [r]ebre un missatge");
            System.out.println(" - [s]ortir");
            System.out.print("Tria una opció: ");
            char opcio = opcioTeclat();
            
            switch (opcio) {
                case 'c':
                case 'C':
                    creaSala();
                    break;
                case 'u':
                case 'U':
                    uneixSala();
                    break;
                case 'e':
                case 'E':
                    enviaMissatge();
                    break;
                case 'r':
                case 'R':
                    repMissatge();
                    break;
                case 's':
                case 'S':
                    continua = false;
                    break;
                default:
                    System.out.println("Opció '" + opcio + "' desconeguda.");
                    break;
            }
        } while (continua);
    }
    
    public static char opcioTeclat() {
        char[] linia = LT.readLineChar();
        return linia.length > 0 ? linia[0] : '\0';
    }
    
    public static void creaSala() {
        System.out.println("Crearem una sala.");
        System.out.println("Si vols que tengui contrasenya, escriu [s].");
        char volContrasenya = opcioTeclat();
        final String[] titolDetalls = {"Número de la sala", "Contrasenya"};
        String[] detalls;
        if (volContrasenya == 's' || volContrasenya == 'S') {
            System.out.println("Ok, la vols amb contrasenya.");
            System.out.println("Escriu un text si la vols triar o si no, pitja <enter> i serà aleatòria.");
            String contrasenya = LT.readLine();
            char[] contrasenyaArray = contrasenya.toCharArray();
            if (contrasenyaArray.length > 0) {
                System.out.println("D'acord, crearem una sala amb la contrasenya \"" + contrasenya + "\".");
                detalls = Comunicacio.create(contrasenya);
            } else {
                System.out.println("Llavors crearem una sala amb contrasenya aleatòria.");
                detalls = Comunicacio.create(true);
            }
        } else {
            System.out.println("Entesos, crearem una sala sense contrasenya.");
            detalls = Comunicacio.create(false);
        }
        System.out.println("Sala creada.");
        // compte que iteram els detalls de la sala, que poden ser 1 o 2
        for (int i = 0; i < detalls.length; i++) {
            System.out.println(" * " + titolDetalls[i] + ": " + detalls[i]);
        }
        
        idSala = detalls[0];
        contrasenyaSala = detalls.length > 1 ? detalls[1] : null;
        
        System.out.println();
    }
    
    public static void uneixSala() {
        System.out.println("Ens unirem a una sala");
        System.out.print("Escriu el número de la sala: ");
        idSala = LT.readLine();
        System.out.print("Escriu la contrasenya de la sala, si en té, o pitja <enter> si no en té: ");
        contrasenyaSala = LT.readLine();
        
        String missatge = Comunicacio.join(idSala, contrasenyaSala);
        System.out.println(missatge);
        System.out.println();
    }
    
    public static void enviaMissatge() {
        if (idSala == null) {
            System.out.println("Abans has de crear o unir-te a una sala!");
            return;
        }
        System.out.print("Escriu el text que vulguis enviar: ");
        String text = LT.readLine();
        Comunicacio.send(idSala, contrasenyaSala, text);
        System.out.println();
    }
    
    public static void repMissatge() {
        if (idSala == null) {
            System.out.println("Abans has de crear o unir-te a una sala!");
            return;
        }
        String text = Comunicacio.receive(idSala, contrasenyaSala);
        if (text == null) {
            System.out.println(" -- no queden missatges --");
        } else {
            System.out.println(text);
        }
        System.out.println();
    }
}
