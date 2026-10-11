import java.util.ArrayList;
import java.util.Scanner;

public class Laberinto {

    static final char[][] MAPA = {
        {'.', '.', '4', '.', '2', '.', '.', 'X', '.', '.'},
        {'4', '.', 'X', '.', '3', 'X', '2', '.', '.', 'X'},
        {'X', '.', 'X', '2', '.', '4', '3', 'X', '.', '.'},
        {'3', '3', 'X', '.', '.', '3', '.', '.', '2', 'X'},
        {'.', '5', '3', '.', 'X', '.', 'X', 'X', '.', '.'},
        {'.', '.', 'X', 'X', '2', 'X', '.', '4', '.', 'X'},
        {'X', 'X', '.', 'X', '.', '5', '2', '.', '.', '.'},
        {'.', '3', '.', '2', '.', '3', '2', '.', '3', 'X'},
        {'.', '.', 'X', 'X', 'X', '.', 'X', 'X', '.', '.'},
        {'.', '.', '3', '.', '3', '.', '.', '.', '2', '.'}
    };

    static final char PLAYER = 'P';
    static final char META = 'M';
    static final char OBSTACULO = 'X';
    static final String COLOR_JUGADOR = "\u001B[32m";
    static final String COLOR_OBSTACULO = "\u001B[1;31m";
    static final String COLOR_META = "\u001B[1;33m";
    static final String COLOR_RESET = "\u001B[0m";

    int player_xpos = -1;
    int player_ypos = -1;
    int meta_xpos = -1;
    int meta_ypos = -1;
    int costo_acumulado = 0;
    ArrayList<int[]> movimientos = new ArrayList<>();

    static class Nodo {
        int y;
        int x;
        int g;
        int h;
        int f;
        Nodo padre;

        Nodo(int y, int x, int g, int h, Nodo padre){
            this.y = y;
            this.x = x;
            this.g = g;
            this.h = h;
            this.f = g+h;
            this.padre = padre;
        }
    }

    void imprimirTablero(){
        System.out.println();
        System.out.print("   ");
        for(int conteo = 1; conteo<=MAPA[0].length; conteo++){
            System.out.print(conteo+" ");
        }
        System.out.println();
        System.out.println("   "+"_".repeat(MAPA[0].length*2));
        for(int y=0; y < MAPA.length; y++){
            if (y>=9) {
                System.out.print(y+1+"|");
            }else{
                System.out.print(y+1+" |");
            }
            for(int x = 0; x < MAPA[0].length; x++){
                if (y == player_ypos && x == player_xpos) {
                    System.out.print(COLOR_JUGADOR + PLAYER + " " + COLOR_RESET);
                }else if(y == meta_ypos && x == meta_xpos){
                    System.out.print(COLOR_META + META + " " + COLOR_RESET);
                }else if(MAPA[y][x]==OBSTACULO){
                    System.out.print(COLOR_OBSTACULO + MAPA[y][x] + " " + COLOR_RESET);
                }else if(revisarCasillaMovimientos(y, x)){
                    System.out.print(COLOR_JUGADOR + MAPA[y][x] + " " + COLOR_RESET);
                }else{
                    System.out.print(MAPA[y][x] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    boolean definirInicio(int inicio_y,int inicio_x){
        int columna_inicio = inicio_x-1;
        int fila_inicio = inicio_y-1;
        boolean validez = false;

        if (comprobarValidezCasilla(fila_inicio, columna_inicio)) {
            validez = true;
            player_xpos = columna_inicio;
            player_ypos = fila_inicio;
        }

        return validez;
    }

    boolean definirFin(int fin_y, int fin_x){
        int columna_fin = fin_x-1;
        int fila_fin = fin_y-1;
        boolean validez = false;

        if (comprobarValidezCasilla(fila_fin, columna_fin)) {
            if (fila_fin == player_ypos && columna_fin == player_xpos) {
                System.out.println("la meta no puede estar en la casilla de inicio, elija una casilla distinta.");
            }else{
                validez = true;
                meta_xpos = columna_fin;
                meta_ypos = fila_fin;
            }
        }

        return validez;
    }

    boolean dentroDelTablero(int y, int x){
        boolean validez = true;

        if ((y<0 || x<0)||(y>MAPA.length-1 || x>MAPA[0].length-1)) {
            validez = false;
        }

        return validez;
    }

    boolean esTransitable(int y, int x){
        boolean validez = false;

        if (dentroDelTablero(y, x)) {
            if (MAPA[y][x]!=OBSTACULO) {
                validez = true;
            }
        }

        return validez;
    }

    boolean comprobarValidezCasilla(int y, int x){
        boolean validez = false;

        if (!dentroDelTablero(y, x)) {
            System.out.println("error, la posicion esta fuera del rango del tablero.");
        }else{
            if (MAPA[y][x]!=OBSTACULO) {
                validez = true;
            }else{
                System.out.println("hay un obstaculo en la casilla seleccionada elija una casilla distinta.");
            }
        }

        return validez;
    }

    int costo(int y, int x){
        int valor = 1;

        if (MAPA[y][x]!='.') {
            valor = Character.getNumericValue(MAPA[y][x]);
        }

        return valor;
    }

    int heuristica(int y, int x){
        int distancia_y = Math.abs(y - meta_ypos);
        int distancia_x = Math.abs(x - meta_xpos);

        return distancia_y + distancia_x;
    }

    Nodo buscarNodo(ArrayList<Nodo> lista, int y, int x){
        Nodo encontrado = null;

        for(Nodo nodo: lista){
            if (nodo.y == y && nodo.x == x) {
                encontrado = nodo;
            }
        }

        return encontrado;
    }

    // buscando modificar para eficiencia no tengo idea como lol
    Nodo buscarMenorF(ArrayList<Nodo> lista){
        Nodo menor = lista.get(0);

        for(Nodo nodo: lista){
            if (nodo.f < menor.f) {
                menor = nodo;
            }
        }

        return menor;
    }

    void revisarVecinos(Nodo actual, ArrayList<Nodo> lista_abierta, ArrayList<Nodo> lista_cerrada){
        int[] vecinos_y = {actual.y-1, actual.y+1, actual.y, actual.y};
        int[] vecinos_x = {actual.x, actual.x, actual.x-1, actual.x+1};

        for(int i = 0; i < 4; i++){
            int y = vecinos_y[i];
            int x = vecinos_x[i];

            if (esTransitable(y, x) && buscarNodo(lista_cerrada, y, x) == null) {
                int nuevo_g = actual.g + costo(y, x);
                Nodo vecino = buscarNodo(lista_abierta, y, x);

                if (vecino == null) {
                    lista_abierta.add(new Nodo(y, x, nuevo_g, heuristica(y, x), actual));
                }else if(nuevo_g < vecino.g){
                    vecino.g = nuevo_g;
                    vecino.f = nuevo_g + vecino.h;
                    vecino.padre = actual;
                }
            }
        }
    }

    ArrayList<int[]> reconstruirRuta(Nodo nodo_meta){
        ArrayList<int[]> ruta = new ArrayList<>();
        Nodo nodo = nodo_meta;

        while (nodo != null) {
            ruta.add(0, new int[]{nodo.y, nodo.x});
            nodo = nodo.padre;
        }

        return ruta;
    }

    ArrayList<int[]> resolverAEstrella(){
        ArrayList<Nodo> lista_abierta = new ArrayList<>();
        ArrayList<Nodo> lista_cerrada = new ArrayList<>();
        ArrayList<int[]> ruta = new ArrayList<>();
        boolean encontrado = false;

        lista_abierta.add(new Nodo(player_ypos, player_xpos, 0, heuristica(player_ypos, player_xpos), null));

        while (!lista_abierta.isEmpty() && !encontrado) {
            Nodo actual = buscarMenorF(lista_abierta);
            lista_abierta.remove(actual);
            lista_cerrada.add(actual);

            if (actual.y == meta_ypos && actual.x == meta_xpos) {
                encontrado = true;
                ruta = reconstruirRuta(actual);
            }else{
                revisarVecinos(actual, lista_abierta, lista_cerrada);
            }
        }

        return ruta;
    }

    void mostrarSolucion(){
        ArrayList<int[]> ruta = resolverAEstrella();

        if (ruta.isEmpty()) {
            imprimirTablero();
            System.out.println("no existe una ruta entre el inicio y la meta.");
        }else{
            int costo_total = 0;

            movimientos = ruta;
            imprimirTablero();

            System.out.println("ruta encontrada con A*:");
            for(int i = 0; i < ruta.size(); i++){
                int[] pos = ruta.get(i);
                System.out.println("fila " + (pos[0]+1) + ", columna " + (pos[1]+1));
                if (i > 0) {
                    costo_total = costo_total + costo(pos[0], pos[1]);
                }
            }
            System.out.println("pasos: " + (ruta.size()-1));
            System.out.println("costo total: " + costo_total);
        }
    }

    boolean moverJugador(String direccion){
        boolean validez = false;
        boolean direccion_valida = true;

        int x = player_xpos;
        int y = player_ypos;

        switch (direccion) {
            case "u":
                    x = player_xpos;
                    y = player_ypos-1;
                break;
            case "d":
                    x = player_xpos;
                    y = player_ypos+1;
                break;
            case "l":
                    x = player_xpos-1;
                    y = player_ypos;
                break;
            case "r":
                    x = player_xpos+1;
                    y = player_ypos;
                break;

            default:
                System.out.println("selecciona una direccion valida");
                direccion_valida = false;
                break;
        }

        if(direccion_valida && comprobarValidezCasilla(y, x)){
            validez = true;
            player_xpos = x;
            player_ypos = y;
            costo_acumulado = costo_acumulado + costo(y, x);
        }

        return validez;
    }

    void guardarMovimiento(int y,int x){
        movimientos.add(new int[]{y,x});
    }

    boolean revisarCasillaMovimientos(int y, int x){

        for(int[] pos:movimientos){
            if(y == pos[0] && x == pos[1]){
                return  true;
            }
        }

        return false;
    }

    boolean enMeta(){
        boolean llegada = false;

        if (player_ypos == meta_ypos && player_xpos == meta_xpos) {
            llegada = true;
        }

        return llegada;
    }

    void jugarManual(Scanner teclado){
        boolean validez;
        boolean salir = false;
        String direccion;

        imprimirTablero();

        while (!enMeta() && !salir) {
            System.out.println("elige una direccion para mover al jugador a esa casilla, arriba (u) | abajo (d) | derecha (r) | izquierda (l) | salir (q)");
            direccion = teclado.next();

            if (direccion.equals("q")) {
                salir = true;
            }else{
                validez = moverJugador(direccion);
                if (validez) {
                    guardarMovimiento(player_ypos, player_xpos);
                    imprimirTablero();
                    System.out.println("posicion actual y:"+(player_ypos+1)+", x:"+(player_xpos+1)+" | costo acumulado: "+costo_acumulado);
                }
            }
        }

        if (enMeta()) {
            System.out.println("llegaste a la meta en " + (movimientos.size()-1) + " pasos con costo total " + costo_acumulado);
        }
    }

    static int leerEntero(Scanner teclado, String mensaje){
        System.out.print(mensaje);
        while (!teclado.hasNextInt()) {
            System.out.println("Eso no es un numero.");
            teclado.next();
            System.out.print(mensaje);
        }
        return teclado.nextInt();
    }

    public static void main(String[] args) {
        Laberinto partida = new Laberinto();
        Scanner teclado = new Scanner(System.in);
        boolean validez = false;

        while (!validez) {
            int inicio_y = leerEntero(teclado, "Fila inicio (y): ");
            int inicio_x = leerEntero(teclado, "Columna inicio (x): ");
            validez = partida.definirInicio(inicio_y, inicio_x);
            if (validez) {
                partida.guardarMovimiento(partida.player_ypos, partida.player_xpos);
            }
        }

        validez = false;

        while (!validez) {
            int fin_y = leerEntero(teclado, "Fila fin (y): ");
            int fin_x = leerEntero(teclado, "Columna fin (x): ");
            validez = partida.definirFin(fin_y, fin_x);
        }

        String modo = "";

        while (!modo.equals("a") && !modo.equals("m")) {
            System.out.println("elige un modo: resolver con A* (a) | mover al jugador manualmente (m)");
            modo = teclado.next();
        }

        if (modo.equals("a")) {
            partida.mostrarSolucion();
        }else{
            partida.jugarManual(teclado);
        }

        teclado.close();
    }
}
