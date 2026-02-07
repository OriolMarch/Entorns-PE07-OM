import java.util.Scanner;
import java.util.ArrayList;

public class PE07escacs {

    // Llistes on hauríem de guardar les peces capturades.
    // (Ara mateix el codi NO les omple, perquè processMove no fa captura real.)
    ArrayList<Character> capturedByWhite = new ArrayList<>();
    ArrayList<Character> capturedByBlack = new ArrayList<>();

    // Noms dels jugadors
    String whitePlayer;
    String blackPlayer;

    // Tauler d'escacs: 8x8 i caràcter per representar buit
    static final int SIZE = 8;
    static final char EMPTY = '.';

    // Estat del joc
    char[][] board = new char[SIZE][SIZE];

    // 0 = blanc, 1 = negre
    int currentPlayer;

    // Historial (guardem els moviments en text: "e2 e4")
    int moveCount;
    String[] history = new String[150];

    // Entrada per consola
    Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        PE07escacs game = new PE07escacs();
        game.mainGame();
    }

    public void mainGame() {
        // 1) Demanem noms
        askPlayers();

        // 2) Preparem el tauler i reiniciem variables
        startGame();

        // 3) Primera impressió (tauler + captures)
        showBoard();
        showCaptured();

        boolean gameOver = false;

        // Bucle principal: només s'acaba quan algú es rendeix
        while (!gameOver) {

            System.out.println();
            System.out.print("Turn: ");
            if (currentPlayer == 0) {
                System.out.println("White (" + whitePlayer + ")");
            } else {
                System.out.println("Black (" + blackPlayer + ")");
            }

            // L'usuari entra un moviment o una comanda
            String move = askMove();

            // "help" no canvia res del joc, només mostra info
            if (isHelp(move)) {
                showHelp();
                continue;
            }

            // "resign" acaba la partida immediatament
            if (isResign(move)) {
                System.out.println("Resign. Game over.");
                System.out.print("Winner: ");
                if (currentPlayer == 0) System.out.println(blackPlayer);
                else System.out.println(whitePlayer);

                gameOver = true;
                printSummary();
                break;
            }

            // Intentem aplicar el moviment
            boolean moved = processMove(move);

            if (moved) {
                // Si s'ha pogut moure: guardem moviment, incrementem comptador i canviem torn
                saveHistory(move);
                moveCount++;
                changeTurn();
                showBoard();
                showCaptured();
            } else {
                // Si no: mateix jugador torna a intentar (no canviem torn)
                System.out.println("Invalid move. Try again.");
            }
        }
    }

    public void startGame() {
        // Reinicialitza el tauler i col·loca les peces com al principi d'una partida
        initBoard();
        placeInitialPieces();

        // Blanc comença
        currentPlayer = 0;

        // Reinici d'historial i captures
        moveCount = 0;
        clearHistory();

        // Buidem llistes de captures
        capturedByWhite.clear();
        capturedByBlack.clear();
    }

    public void initBoard() {
        // Deixa totes les caselles buides (.)
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = EMPTY;
            }
        }
    }

    public void placeInitialPieces() {
        // Negres (files 0 i 1)
        board[0][0] = 'r'; board[0][1] = 'n'; board[0][2] = 'b'; board[0][3] = 'q';
        board[0][4] = 'k'; board[0][5] = 'b'; board[0][6] = 'n'; board[0][7] = 'r';

        for (int col = 0; col < SIZE; col++) {
            board[1][col] = 'p';
        }

        // Blanques (files 6 i 7)
        board[7][0] = 'R'; board[7][1] = 'N'; board[7][2] = 'B'; board[7][3] = 'Q';
        board[7][4] = 'K'; board[7][5] = 'B'; board[7][6] = 'N'; board[7][7] = 'R';

        for (int col = 0; col < SIZE; col++) {
            board[6][col] = 'P';
        }
    }

    public void showBoard() {
        // Imprimeix el tauler amb coordenades (a-h i 8-1) per orientar-se fàcilment
        System.out.println();
        System.out.println("      a   b   c   d   e   f   g   h");
        System.out.println("    ---------------------------------");

        for (int row = 0; row < SIZE; row++) {
            System.out.print(" " + (8 - row) + "  |");

            for (int col = 0; col < SIZE; col++) {
                System.out.print(" " + board[row][col] + " |");
            }

            System.out.println("  " + (8 - row));
            System.out.println("    ---------------------------------");
        }

        System.out.println("      a   b   c   d   e   f   g   h");
    }

    public void showHelp() {
        // Ajuda ràpida per l'usuari
        System.out.println("---------- HELP ----------");
        System.out.println("e2 e4   -> move a piece from e2 to e4");
        System.out.println("help    -> show this help");
        System.out.println("resign  -> resign the game");
        System.out.println("--------------------------");
    }

    public void askPlayers() {
        // Demana noms i els valida (no buit i no només números)
        whitePlayer = askName("White player name: ");
        blackPlayer = askName("Black player name: ");
    }

    public String askName(String msg) {
        // Bucla fins que el nom compleixi condicions
        String name;
        while (true) {
            System.out.print(msg);
            name = sc.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Name cannot be empty.");
            } else if (isNumber(name)) {
                System.out.println("Name cannot be a number.");
            } else {
                return name;
            }
        }
    }

    public boolean isNumber(String s) {
        // Retorna true si TOTS els caràcters són dígits
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public String getCurrentPlayerName() {
        // Només serveix per imprimir (lògica simple de torn)
        if (currentPlayer == 0) return whitePlayer;
        return blackPlayer;
    }

    public String getOtherPlayerName() {
        if (currentPlayer == 0) return blackPlayer;
        return whitePlayer;
    }

    public String getCurrentPlayerColor() {
        if (currentPlayer == 0) return "White";
        return "Black";
    }
    
    public String askMove() {
        // Format esperat: "e2 e4" o comandes
        System.out.print("Move (e2 e4 / help / resign): ");
        return sc.nextLine().trim();
    }

    public boolean isHelp(String s) {
        return s.equalsIgnoreCase("help");
    }

    public boolean isResign(String s) {
        return s.equalsIgnoreCase("resign");
    }

    public void changeTurn() {
        // Alterna entre 0 i 1 (blanc/negre)
        if (currentPlayer == 0) currentPlayer = 1;
        else currentPlayer = 0;
    }

    public void saveHistory(String move) {
        // Guarda el moviment al vector si encara hi ha espai
        if (moveCount < history.length) {
            history[moveCount] = move;
        }
    }

    public void clearHistory() {
        // Deixa l'historial net (null) per una nova partida
        for (int i = 0; i < history.length; i++) {
            history[i] = null;
        }
    }

    // IMPORTANT:
    // Ara mateix aquí NO es valida el moviment real de les peces (peó, cavall, etc.).
    // Només comprova format + origen amb peça + que sigui del jugador actual.
    public boolean processMove(String move) {
        if (!isValidFormat(move)) {
            System.out.println("Invalid format. Use: e2 e4");
            return false;
        }

        // Convertim "e2" a coordenades del tauler:
        // col = lletra - 'a'
        // row = 8 - número (perquè la fila 8 és row 0 a l'array)
        int fromCol = move.charAt(0) - 'a';
        int fromRow = 8 - (move.charAt(1) - '0');
        int toCol   = move.charAt(3) - 'a';
        int toRow   = 8 - (move.charAt(4) - '0');

        char piece = board[fromRow][fromCol];

        if (piece == EMPTY) {
            System.out.println("No piece at origin.");
            return false;
        }

        if (!isCurrentPlayerPiece(piece)) {
            System.out.println("That piece is not yours.");
            return false;
        }

        // Aquí mou directament la peça sense regles d'escacs.
        // Si vols captures reals, aquí s'hauria de:
        //  - mirar board[toRow][toCol]
        //  - decidir si és enemic
        //  - afegir-ho a capturedByWhite / capturedByBlack
        board[toRow][toCol] = piece;
        board[fromRow][fromCol] = EMPTY;

        return true;
    }

    public boolean isValidFormat(String move) {
        // Format mínim: "e2 e4" (5 caràcters i espai al mig)
        if (move.length() != 5) return false;
        if (move.charAt(2) != ' ') return false;

        char c1 = move.charAt(0);
        char r1 = move.charAt(1);
        char c2 = move.charAt(3);
        char r2 = move.charAt(4);

        // Rang vàlid del tauler: a-h i 1-8
        if (c1 < 'a' || c1 > 'h') return false;
        if (c2 < 'a' || c2 > 'h') return false;
        if (r1 < '1' || r1 > '8') return false;
        if (r2 < '1' || r2 > '8') return false;

        return true;
    }

    public boolean isCurrentPlayerPiece(char piece) {
        // Majúscules = blanques, minúscules = negres
        if (currentPlayer == 0) {
            return Character.isUpperCase(piece);
        } else {
            return Character.isLowerCase(piece);
        }
    }

    public void showCaptured(){
        // Mostra les llistes de captures.
        // Avís: ara mateix estaran buides perquè no s'afegeixen captures enlloc.
        System.out.println("Captured By White: ");
        for(int i=0; i <capturedByWhite.size();i++){
            System.out.println(capturedByWhite.get(i) + " ");
        }
        System.out.println();

        // Aquí falten els textos "Captured By Black:", però no ho canvio perquè és lògica/UI original.
        for(int i=0; i<capturedByBlack.size();i++){
            System.out.println(capturedByBlack.get(i) + " ");
        }
        System.out.println(); 
    }

    public void printSummary(){
        // Imprimeix el resum: llista de moviments guardats
        System.out.println("Game Summary:");
        for(int i=0; i<moveCount;i++){
            System.out.println((i+1) + ". " + history[i]);
        }  
    }
}
