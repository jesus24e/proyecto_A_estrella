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

    static final char PLAYER = '@';
    static int player_xpos = -1;
    static int player_ypos = -1;
    static final String COLOR_JUGADOR = "\u001B[32m";
    static final String COLOR_OBSTACULO = "\u001B[1;31m";

    void imprimirTablero(boolean validezCasilla){

        if (validezCasilla) {
            
            int fila = player_ypos+1;
            int columna = player_xpos+1;
            
            System.out.println("posicion de inicio y:"+fila+", "+" x:"+columna );
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

    Boolean estadoInicial(int y,int x){
        int columna = x-1;
        int fila = y-1;
        boolean validez = false;

        if (((fila<0 || columna<0)||(fila>MAPA.length-1 || columna>MAPA[0].length-1))) {
                System.out.println("error, la posicion esta fuera del tablero.");
        }else{
            if (MAPA[fila][columna]!='X') {
                validez = true;
                player_xpos = columna;
                player_ypos = fila;
            }else{
                System.out.println("hay un obstaculo en la casilla seleccionada.");
            }
        }
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
            int y = leerEntero(teclado, "Fila (y): ");
            int x = leerEntero(teclado, "Columna (x): ");
            validez = partida.estadoInicial(y, x);
        }

        partida.imprimirTablero(validez);
    }
}