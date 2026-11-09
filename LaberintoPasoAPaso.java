import java.util.ArrayList;
import java.util.Scanner;

/*
 * Version de Laberinto.java que explica paso a paso lo que hace A*.
 * La busqueda es exactamente la misma (mismo mapa, mismos costos, misma
 * heuristica, mismo orden de vecinos y mismo desempate); solo se agregan
 * mensajes que muestran cada decision del algoritmo.
 *
 * Ejecucion:  java LaberintoPasoAPaso.java
 */
public class LaberintoPasoAPaso {

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
    static final char ACTUAL = '@';
    static final char CERRADA = 'c';
    static final char ABIERTA = 'a';

    static final String COLOR_JUGADOR = "\u001B[30;48;5;75m";
    static final String COLOR_OBSTACULO = "\u001B[1;31m";
    static final String COLOR_META = "\u001B[1;35m";
    static final String COLOR_ACTUAL = "\u001B[1;30;48;5;114m";
    static final String COLOR_CERRADA = "\u001B[30;48;5;250m";
    static final String COLOR_ABIERTA = "\u001B[30;48;5;229m";
    static final String COLOR_TITULO = "\u001B[1m";
    static final String COLOR_RESET = "\u001B[0m";

    static final String[] DIRECCIONES = {"Arriba", "Abajo", "Izquierda", "Derecha"};

    int player_xpos = -1;
    int player_ypos = -1;
    int meta_xpos = -1;
    int meta_ypos = -1;
    ArrayList<int[]> movimientos = new ArrayList<>();

    // Datos para la explicacion paso a paso
    boolean pausar = false;
    Scanner teclado;
    int iteraciones = 0;
    int total_cerrada = 0;
    int total_abierta = 0;

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
            this.g = g; //costo
            this.h = h; //heuristica
            this.f = g+h;   //funcion de evaluacion o puntuacion A*
            this.padre = padre;
        }
    }

    // ------------------------------------------------------------------
    // Utilidades para mostrar la explicacion
    // ------------------------------------------------------------------

    // Coordenadas como las ve el usuario (de 1 a 10)
    static String pos(int y, int x){
        return "(" + (y+1) + "," + (x+1) + ")";
    }

    static String describir(Nodo n){
        return pos(n.y, n.x) + "  g=" + n.g + "  h=" + n.h + "  f=" + n.f;
    }

    static void titulo(String texto){
        System.out.println();
        System.out.println(COLOR_TITULO + "================ " + texto + " ================" + COLOR_RESET);
    }

    void pausa(){
        if (pausar) {
            System.out.print("[Enter para continuar] ");
            if (teclado.hasNextLine()) {
                teclado.nextLine();
            } else {
                pausar = false;
            }
        }
    }

    String explicarHeuristica(int y, int x){
        return "|" + (y+1) + " - " + (meta_ypos+1) + "| + |" + (x+1) + " - " + (meta_xpos+1) + "| = " + heuristica(y, x);
    }

    void imprimirLista(String nombre, ArrayList<Nodo> lista, Nodo elegido){
        System.out.println(nombre + ":");
        if (lista.isEmpty()) {
            System.out.println("   (vacia)");
        }
        for(Nodo nodo: lista){
            String marca = nodo == elegido ? "   <- menor f" : "";
            System.out.println("   " + describir(nodo) + marca);
        }
    }

    // Tablero con el estado de la busqueda: actual, lista cerrada y lista abierta
    void imprimirEstado(Nodo actual, ArrayList<Nodo> lista_abierta, ArrayList<Nodo> lista_cerrada){
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
                if (actual != null && y == actual.y && x == actual.x) {
                    System.out.print(COLOR_ACTUAL + ACTUAL + " " + COLOR_RESET);
                }else if (y == player_ypos && x == player_xpos) {
                    System.out.print(COLOR_JUGADOR + PLAYER + " " + COLOR_RESET);
                }else if(y == meta_ypos && x == meta_xpos){
                    System.out.print(COLOR_META + META + " " + COLOR_RESET);
                }else if(MAPA[y][x]==OBSTACULO){
                    System.out.print(COLOR_OBSTACULO + MAPA[y][x] + " " + COLOR_RESET);
                }else if(buscarNodo(lista_cerrada, y, x) != null){
                    System.out.print(COLOR_CERRADA + CERRADA + " " + COLOR_RESET);
                }else if(buscarNodo(lista_abierta, y, x) != null){
                    System.out.print(COLOR_ABIERTA + ABIERTA + " " + COLOR_RESET);
                }else{
                    System.out.print(MAPA[y][x] + " ");
                }
            }
            System.out.println();
        }
        System.out.println("@ actual   c lista cerrada   a lista abierta   P inicio   M meta   X obstaculo");
    }

    // ------------------------------------------------------------------
    // Mismo codigo que Laberinto.java
    // ------------------------------------------------------------------

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
                }else if(casillaEnMovimientos(y, x)){
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

        if (!esTransitable(y, x)) {
            System.out.println("error, la posicion esta fuera del rango del tablero o hay un obstaculo en la casilla seleccionada, elija una casilla distinta.");
        }else{
                validez = true;
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

    Nodo buscarMenorF(ArrayList<Nodo> lista){
        Nodo menor = lista.get(0);

        for(Nodo nodo: lista){
            if (nodo.f < menor.f) {
                menor = nodo;
            }
        }

        return menor;
    }

    // ------------------------------------------------------------------
    // A* con explicacion paso a paso
    // ------------------------------------------------------------------

    void revisarVecinos(Nodo actual, ArrayList<Nodo> lista_abierta, ArrayList<Nodo> lista_cerrada){
        int[] vecinos_y = {actual.y-1, actual.y+1, actual.y, actual.y};
        int[] vecinos_x = {actual.x, actual.x, actual.x-1, actual.x+1};

        System.out.println("Se revisan los vecinos de " + pos(actual.y, actual.x) + ":");

        for(int i = 0; i < 4; i++){
            int y = vecinos_y[i];
            int x = vecinos_x[i];
            String etiqueta = "   " + DIRECCIONES[i] + " ";

            if (!dentroDelTablero(y, x)) {
                System.out.println(etiqueta + "-> fuera del tablero, se ignora.");
            }else if (!esTransitable(y, x)) {
                System.out.println(etiqueta + pos(y, x) + " -> es un obstaculo, se ignora.");
            }else if (buscarNodo(lista_cerrada, y, x) != null) {
                System.out.println(etiqueta + pos(y, x) + " -> ya esta en la lista cerrada, se ignora.");
            }else{
                int costo_casilla = costo(y, x);
                int nuevo_g = actual.g + costo_casilla;
                Nodo vecino = buscarNodo(lista_abierta, y, x);
                String calculo = "nuevo g = " + actual.g + " + " + costo_casilla + " = " + nuevo_g;

                if (vecino == null) {
                    int h = heuristica(y, x);
                    lista_abierta.add(new Nodo(y, x, nuevo_g, h, actual));
                    System.out.println(etiqueta + pos(y, x) + " -> " + calculo
                        + ", h = " + explicarHeuristica(y, x)
                        + ", f = " + (nuevo_g + h)
                        + ". Es nueva: se agrega a la lista abierta con padre " + pos(actual.y, actual.x) + ".");
                }else if(nuevo_g < vecino.g){
                    int g_anterior = vecino.g;
                    vecino.g = nuevo_g;
                    vecino.f = nuevo_g + vecino.h;
                    vecino.padre = actual;
                    System.out.println(etiqueta + pos(y, x) + " -> " + calculo
                        + ". Ya estaba en la lista abierta con g = " + g_anterior
                        + "; este camino es mas barato: se actualiza g = " + vecino.g
                        + ", f = " + vecino.f + " y padre " + pos(actual.y, actual.x) + ".");
                }else{
                    System.out.println(etiqueta + pos(y, x) + " -> " + calculo
                        + ". Ya estaba en la lista abierta con g = " + vecino.g
                        + "; este camino no es mas barato: se deja igual.");
                }
            }
        }
    }

    ArrayList<int[]> reconstruirRuta(Nodo nodo_meta){
        ArrayList<int[]> ruta = new ArrayList<>();
        Nodo nodo = nodo_meta;

        titulo("RECONSTRUCCION DE LA RUTA");
        System.out.println("Se parte de la meta y se sigue a cada padre hasta llegar al inicio:");

        while (nodo != null) {
            ruta.add(0, new int[]{nodo.y, nodo.x});
            if (nodo.padre != null) {
                System.out.println("   " + pos(nodo.y, nodo.x) + "  su padre es  " + pos(nodo.padre.y, nodo.padre.x));
            }else{
                System.out.println("   " + pos(nodo.y, nodo.x) + "  no tiene padre: es el inicio.");
            }
            nodo = nodo.padre;
        }

        System.out.println("Cada casilla se inserta al principio de la lista, asi la ruta queda ordenada de inicio a meta.");

        return ruta;
    }

    ArrayList<int[]> resolverAEstrella(){
        ArrayList<Nodo> lista_abierta = new ArrayList<>();
        ArrayList<Nodo> lista_cerrada = new ArrayList<>();
        ArrayList<int[]> ruta = new ArrayList<>();
        boolean encontrado = false;

        Nodo inicial = new Nodo(player_ypos, player_xpos, 0, heuristica(player_ypos, player_xpos), null);
        lista_abierta.add(inicial);

        titulo("PREPARACION");
        System.out.println("Inicio " + pos(player_ypos, player_xpos) + ", meta " + pos(meta_ypos, meta_xpos) + ".");
        System.out.println("Se crea el nodo inicial " + pos(inicial.y, inicial.x) + ":");
        System.out.println("   g = 0 (todavia no se ha pagado ningun costo)");
        System.out.println("   h = " + explicarHeuristica(inicial.y, inicial.x) + " (distancia Manhattan a la meta)");
        System.out.println("   f = g + h = " + inicial.f);
        System.out.println("Se coloca en la lista abierta. La lista cerrada empieza vacia.");
        pausa();

        while (!lista_abierta.isEmpty() && !encontrado) {
            iteraciones++;
            titulo("ITERACION " + iteraciones);

            Nodo actual = buscarMenorF(lista_abierta);
            int empates = 0;
            for(Nodo nodo: lista_abierta){
                if (nodo.f == actual.f) {
                    empates++;
                }
            }

            imprimirLista("Lista abierta (candidatas)", lista_abierta, actual);
            System.out.print("Se elige " + pos(actual.y, actual.x) + " porque tiene la menor f (" + actual.f + ")");
            if (empates > 1) {
                System.out.print("; hay " + empates + " casillas empatadas y se toma la que entro primero a la lista");
            }
            System.out.println(".");

            lista_abierta.remove(actual);
            lista_cerrada.add(actual);
            System.out.println("Se pasa " + pos(actual.y, actual.x) + " a la lista cerrada: su costo g = " + actual.g + " ya es el menor posible.");

            if (actual.y == meta_ypos && actual.x == meta_xpos) {
                System.out.println(pos(actual.y, actual.x) + " es la meta. Termina la busqueda con costo total " + actual.g + ".");
                encontrado = true;
                imprimirEstado(actual, lista_abierta, lista_cerrada);
                ruta = reconstruirRuta(actual);
            }else{
                System.out.println(pos(actual.y, actual.x) + " no es la meta.");
                revisarVecinos(actual, lista_abierta, lista_cerrada);
                imprimirEstado(actual, lista_abierta, lista_cerrada);
            }
            pausa();
        }

        if (!encontrado) {
            titulo("SIN RUTA");
            System.out.println("La lista abierta quedo vacia: ya no hay casillas por explorar y no se llego a la meta.");
        }

        total_cerrada = lista_cerrada.size();
        total_abierta = lista_abierta.size();

        return ruta;
    }

    void mostrarSolucion(){
        ArrayList<int[]> ruta = resolverAEstrella();

        titulo("RESULTADO");
        if (ruta.isEmpty()) {
            imprimirTablero();
            System.out.println("no existe una ruta entre el inicio y la meta.");
        }else{
            int costo_total = 0;

            movimientos = ruta;
            imprimirTablero();

            System.out.println("ruta encontrada con A*:");
            System.out.println();
            for(int i = 0; i < ruta.size(); i++){
                int[] pos = ruta.get(i);
                String costo = i>0 ? String.valueOf(costo(pos[0], pos[1])):"0";
                System.out.println("fila " + (pos[0]+1) + ", columna " + (pos[1]+1) + ", costo: " + costo);
                if (i > 0) {
                    costo_total = costo_total + costo(pos[0], pos[1]);
                }
            }
            System.out.println();
            System.out.println("pasos: " + (ruta.size()-1));
            System.out.println("costo total: " + costo_total);
        }

        System.out.println();
        System.out.println("iteraciones de A*: " + iteraciones);
        System.out.println("casillas exploradas (lista cerrada): " + total_cerrada);
        System.out.println("casillas descubiertas sin explorar (lista abierta): " + total_abierta);
    }

    void guardarMovimiento(int y,int x){
        movimientos.add(new int[]{y,x});
    }

    boolean casillaEnMovimientos(int y, int x){

        for(int[] pos:movimientos){
            if(y == pos[0] && x == pos[1]){
                return  true;
            }
        }

        return false;
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
        LaberintoPasoAPaso partida = new LaberintoPasoAPaso();
        Scanner teclado = new Scanner(System.in);
        boolean validez = false;

        partida.imprimirTablero();

        System.out.println("Introduce los numeros de fila (y) y columna (x) del inicio y de la meta, del 1 al " + MAPA.length + ".");
        System.out.println();

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

        System.out.print("Pausar despues de cada paso? (s/n): ");
        String respuesta = teclado.next();
        teclado.nextLine(); // descarta el resto de la linea
        partida.pausar = respuesta.equalsIgnoreCase("s");
        partida.teclado = teclado;

        partida.mostrarSolucion();

        teclado.close();
    }
}
