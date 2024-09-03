Permet comunicar dos o més programes a través de sales.

# Funcionament bàsic

Es fa servir el servidor [https://programacio.ltim.uib.es/](https://programacio.ltim.uib.es/).

Aquesta pàgina conté informació de les sales creades i els missatges pendents de llegir, a mode d'ajuda.

Primer un client ha de crear una sala i llavors qualsevol client pot enviar i rebre missatges d'aquella sala si en coneix el nom i la contrasenya.

Els missatges segueixen un ordre LIFO independent de qui els hagi enviat:

1. S'envia `"missatge #1"`.
2. S'envia `"missatge #2"`.
3. Es rep `"missatge #1"`.
4. Es rep `"missatge #2"`.
5. Es rep `null` (no quedaven més missatges).

# Mètodes

La classe `Comunicació` conté els mètodes `static` necessaris per fer la comunicació.

Qualsevol estat (per exemple, nom i contrasenya de la sala) l'ha de mantenir l'usuari.

## Crear una sala:

Hi ha tres mètodes disponibles:

- `static String[] Comunicacio.create()`: crea una sala amb contrasenya o no segons la configuració del servidor.
- `static String[] Comunicacio.create(boolean passwordRequired)`: crea una sala amb contrasenya aleatòria o no segons el booleà.
- `static String[] Comunicacio.create(String password)`: crea una sala amb la contrasenya indicada.

Els tres mètodes retornen un `String[]` amb els continguts següents:
 - nom de la sala (un nombre incremental)
 - la contrasenya (si té contrasenya, si no, aquest element no existeix)

## Unir-se a una sala:

Això realment no fa res i només comprova que la combinació de nom de la sala i contrasenya siguin correctes.

- `static String Comunicacio.join(String roomId, String password)`: prova d'unir-se a la sala segons el seu nom i la contrasenya indicades (`null` si no té contrasenya).

Retorna l'String `OK` si la combinació és correcta i, si no, un missatge d'error que comença per `ERROR`.

## Enviar un missatge a una sala:

Una vegada la sala existeix es poden enviar tants missatges com es vulguin:

- `static void Comunicacio.send(String roomId, String password, String message`: envia `message` a la sala indicada amb la contrasenya indicada (`null` si no té contrasenya).

Si la combinació de sala i contrasenya és incorrecta, dispara un `Error` (que així no és necessari capturar-lo).

## Rebre un missatge d'una sala:

Una vegada la sala existeix es poden rebre missatges:

- `static String Comunicacio.receive(String roomId, String password`: rep el missatge més antic que encara no s'hagi llegit de la sala (si no té contrasenya, s'indica `null`).

Retorna l'`String` amb el missatge o `null` si no queden més missatges per llegir.

