import java.util.Scanner;
import java.util.ArrayList;


public class PE07escacs {

    // Array List 
    ArrayList<Character> capturedByWhite = new ArrayList<>();
    ArrayList<Character> capturedByBlack = new ArrayList<>();
    // Players
    String whitePlayer;
    String blackPlayer;

    // Constants
    static final int SIZE = 8;
    static final char EMPTY = '.';

    // Game state
    char[][] board = new char[SIZE][SIZE];
    int currentPlayer; // 0 = white, 1 = black

    // Historial
    int moveCount;
    String[] history = new String[150];

    // Input
    Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        PE07escacs game = new PE07escacs();
        game.mainGame();
    }

    // Main

    public void mainGame() {
    askPlayers();
    startGame();
    showBoard();
    showCaptured();

    boolean gameOver = false;

    while (!gameOver) {

        System.out.println();
        System.out.print("Turn: ");
        if (currentPlayer == 0) {
            System.out.println("White (" + whitePlayer + ")");
        } else {
            System.out.println("Black (" + blackPlayer + ")");
        }

        String move = askMove();

        if (isHelp(move)) {
            showHelp();
            continue;
        }

        if (isResign(move)) {
            System.out.println("Resign. Game over.");
            System.out.print("Winner: ");
            if (currentPlayer == 0) System.out.println(blackPlayer);
            else System.out.println(whitePlayer);

            gameOver = true;
            printSummary();
            break;
        }

        boolean moved = processMove(move);

        if (moved) {
            saveHistory(move);
            moveCount++;
            changeTurn();
            showBoard();
            showCaptured();
        } else {
            System.out.println("Invalid move. Try again.");
        }
    }
}


    // Set-up
    public void startGame() {
        initBoard();
        placeInitialPieces();
        currentPlayer = 0; // Blanc comença
        moveCount = 0;


        clearHistory();
        capturedByWhite.clear();        capturedByBlack.clear();
    }
    

    public void initBoard() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                board[row][col] = EMPTY;
            }
        }
    }

    public void placeInitialPieces() {
        // Negres 0 i 1.
        board[0][0] = 'r'; board[0][1] = 'n'; board[0][2] = 'b'; board[0][3] = 'q';
        board[0][4] = 'k'; board[0][5] = 'b'; board[0][6] = 'n'; board[0][7] = 'r';

        for (int col = 0; col < SIZE; col++) {
            board[1][col] = 'p';
        }

        // Blanques 6 i 7
        board[7][0] = 'R'; board[7][1] = 'N'; board[7][2] = 'B'; board[7][3] = 'Q';
        board[7][4] = 'K'; board[7][5] = 'B'; board[7][6] = 'N'; board[7][7] = 'R';

        for (int col = 0; col < SIZE; col++) {
            board[6][col] = 'P';
        }
    }

    // Tauler
  public void showBoard() {

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
        System.out.println("---------- HELP ----------");
        System.out.println("e2 e4   -> move a piece from e2 to e4");
        System.out.println("help    -> show this help");
        System.out.println("resign  -> resign the game");
        System.out.println("--------------------------");
    }

    //Jugadors:

    public void askPlayers() {
        whitePlayer = askName("White player name: ");
        blackPlayer = askName("Black player name: ");
    }

    public String askName(String msg) {
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
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }

    public String getCurrentPlayerName() {
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

    // Els inputs + comandes
    public String askMove() {
        System.out.print("Move (e2 e4 / help / resign): ");
        return sc.nextLine().trim();
    }

    public boolean isHelp(String s) {
        return s.equalsIgnoreCase("help");
    }

    public boolean isResign(String s) {
        return s.equalsIgnoreCase("resign");
    }

   //Historial + Canvia de torn
    public void changeTurn() {
        if (currentPlayer == 0) currentPlayer = 1;
        else currentPlayer = 0;
    }

    public void saveHistory(String move) {
        if (moveCount < history.length) {
            history[moveCount] = move;
        }
    }

    public void clearHistory() {
        for (int i = 0; i < history.length; i++) {
            history[i] = null;
        }
    }

    
    // Falta validar totes les peçes (Reina,rei,peo ...)
    public boolean processMove(String move) {
        if (!isValidFormat(move)) {
            System.out.println("Invalid format. Use: e2 e4");
            return false;
        }

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

        // Moviment bàsic, sense normas del joc d'escacs.
        board[toRow][toCol] = piece;
        board[fromRow][fromCol] = EMPTY;

        return true;
    }

    public boolean isValidFormat(String move) {
        if (move.length() != 5) return false;
        if (move.charAt(2) != ' ') return false;

        char c1 = move.charAt(0);
        char r1 = move.charAt(1);
        char c2 = move.charAt(3);
        char r2 = move.charAt(4);

        if (c1 < 'a' || c1 > 'h') return false;
        if (c2 < 'a' || c2 > 'h') return false;
        if (r1 < '1' || r1 > '8') return false;
        if (r2 < '1' || r2 > '8') return false;

        return true;
    }

    public boolean isCurrentPlayerPiece(char piece) {
        if (currentPlayer == 0) {
            return Character.isUpperCase(piece);
        } else {
            return Character.isLowerCase(piece);
        }
    }

    public void showCaptured(){
        System.out.println("Captured By White: ");
        for(int i=0; i <capturedByWhite.size();i++){
            System.out.println(capturedByWhite.get(i) + " ");
        }
        System.out.println();

        for(int i=0; i<capturedByBlack.size();i++){
            System.out.println(capturedByBlack.get(i) + " ");
        }
        System.out.println(); 

    }

    public void printSummary(){
        System.out.println("Game Summary:");
        for(int i=0; i<moveCount;i++){
            System.out.println((i+1) + ". " + history[i]);
        }  
        
    }
}
