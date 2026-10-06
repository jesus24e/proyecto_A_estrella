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
    static int player_xpos = -1;
    static int player_ypos = -1;
    static int meta_xpos = -1;
    static int meta_ypos = -1;    
    static final String COLOR_JUGADOR = "\u001B[35m";
    static final String COLOR_OBSTACULO = "\u001B[1;31m";
    static final String COLOR_META = "\u001B[1;33m";;

    void imprimirTablero(boolean validezCasilla){

        if (validezCasilla) {
            
            int fila_player = player_ypos+1;
            int columna_player = player_xpos+1;
            
            System.out.println("posicion de inicio y:"+fila_player+", "+" x:"+columna_player );
            System.out.println();
            System.out.print("   ");
            for(int conteo = 1; conteo<=10; conteo++){
                System.out.print(conteo+" ");
            }
            System.out.println();
            System.out.println("   "+"_".repeat(20));
            for(int y=0; y < MAPA.length; y++){
                if (y==9) {
                    System.out.print(y+1+"|");
                }else{
                    System.out.print(y+1+" |");
                }
                for(int x = 0; x < MAPA[0].length; x++){
                    if (y == player_ypos && x == player_xpos) {
                        System.out.print(COLOR_JUGADOR+PLAYER + " "+"\u001B[0m");
                    }else if(y == meta_ypos && x == meta_xpos){
                        System.out.print(COLOR_META + META +" \u001B[0m");
                    }else if(MAPA[y][x]=='X'){
                        System.out.print(COLOR_OBSTACULO+ MAPA[y][x] +" \u001B[0m");
                    }else{
                        System.out.print(MAPA[y][x] + " ");
                    }
                }
                System.out.println();
            }
        }
    }

    Boolean definirInicio(int inicio_y,int inicio_x){
        int columna_inicio = inicio_x-1;
        int fila_inicio = inicio_y-1;
        boolean validez = false;

        if (comprobarValidezCasilla(columna_inicio, fila_inicio)) {
            validez = true;
            player_xpos = columna_inicio;
            player_ypos = fila_inicio;
        }
    
        return validez;        
    }
    Boolean definirFin(int fin_y, int fin_x){
        int columna_fin = fin_x-1;
        int fila_fin = fin_y-1;
        boolean validez = false;

        if (comprobarValidezCasilla(columna_fin, fila_fin)) {
            validez = true;
            meta_xpos = columna_fin;
            meta_ypos = fila_fin;
        }
    
        return validez;           
    }

    boolean comprobarValidezCasilla(int x, int y){
        boolean validez = false;

        if (((y<0 || x<0)||(y>MAPA.length-1 || x>MAPA[0].length-1))) {
                System.out.println("error, la posicion esta fuera del rango del tablero.");
        }else{
            if (MAPA[y][x]!='X') {
                validez = true;
            }else{
                System.out.println("hay un obstaculo en la casilla seleccionada elija una casilla distinta.");
            }
        }

        return validez;
    }

    boolean moverJugador(String direccion){
        boolean validez = false;

        int x=-1;
        int y=-1;

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
                break;
        }

        if(comprobarValidezCasilla(x, y)){
            validez = true;
        }

        player_xpos = x;
        player_ypos = y;

        return validez;
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
        }

        validez = false;

        while (!validez) {
            int fin_y = leerEntero(teclado, "Fila fin (y): ");
            int fin_x = leerEntero(teclado, "Columna fin (x): ");
            validez = partida.definirFin(fin_y, fin_x);
        }

        partida.imprimirTablero(validez);

        String direccion;
        
        while (validez) {
            System.out.println("elige una direccion para mover al jugador a esa casilla, arriba (u) | abajo (d) | derecha (r) | izquierda (l)");
            direccion = teclado.next();
            validez = partida.moverJugador(direccion);
            partida.imprimirTablero(validez);
        }

        


    }
}