
public class MoveValidator {

    /**
     * Valida un moviment segons la peça a l'origen.
     *
     * @param board tauler
     * @param fromRow fila origen (0..7)
     * @param fromCol col origen (0..7)
     * @param toRow fila desti (0..7)
     * @param toCol col desti (0..7)
     * @return resultat (valid/invalid + motiu)
     */
    public ValidationResult validate(char[][] board, int fromRow, int fromCol, int toRow, int toCol) {
        if (!isInside(fromRow, fromCol) || !isInside(toRow, toCol)) {
            return ValidationResult.fail("movement goes out of board");
        }

        char piece = board[fromRow][fromCol];
        if (piece == PE07escacs.EMPTY) {
            return ValidationResult.fail("origin is empty");
        }

        // Evitar capturar peça pròpia
        char target = board[toRow][toCol];
        if (target != PE07escacs.EMPTY && isSameColor(piece, target)) {
            return ValidationResult.fail("destination has your own piece");
        }

        if (isPawn(piece)) {
            return validatePawn(board, piece, fromRow, fromCol, toRow, toCol);
        }

        if (isKnight(piece)) {
            return validateKnight(board, piece, fromRow, fromCol, toRow, toCol);
        }

        return ValidationResult.fail("piece type not supported by tests");
    }

   
    private ValidationResult validatePawn(char[][] board, char pawn, int fr, int fc, int tr, int tc) {
        boolean white = Character.isUpperCase(pawn);
        int direction;
        if (white) {
            direction = -1;
        } else {
            direction = 1;
        }

        int dr = tr - fr;
        int dc = tc - fc;

        // Enrere
        if (dr == 0) {
            return ValidationResult.fail("pawn cannot stay in place");
        }
        if (white && dr > 0) {
            return ValidationResult.fail("pawn cannot move backwards");
        }
        if (!white && dr < 0) {
            return ValidationResult.fail("pawn cannot move backwards");
        }

        // Captura diagonal
        if (Math.abs(dc) == 1 && dr == direction) {
            char target = board[tr][tc];
            if (target == PE07escacs.EMPTY) {
                return ValidationResult.fail("pawn diagonal capture needs an enemy piece");
            }
            if (isSameColor(pawn, target)) {
                return ValidationResult.fail("pawn cannot capture own piece");
            }
            return ValidationResult.ok();
        }

        // Moviment endavant (mateixa columna)
        if (dc != 0) {
            return ValidationResult.fail("pawn cannot move sideways");
        }

        // 1 pas
        if (dr == direction) {
            if (board[tr][tc] != PE07escacs.EMPTY) {
                return ValidationResult.fail("pawn forward square is occupied");
            }
            return ValidationResult.ok();
        }

        // 2 passos des d'inicial
        int startRow;
        if (white) {
            startRow = 6;
        } else {
            startRow = 1;
        }

        if (fr == startRow && dr == 2 * direction) {
            int intermediateRow = fr + direction;
            if (board[intermediateRow][fc] != PE07escacs.EMPTY) {
                return ValidationResult.fail("pawn two-step is blocked");
            }
            if (board[tr][tc] != PE07escacs.EMPTY) {
                return ValidationResult.fail("pawn destination is occupied");
            }
            return ValidationResult.ok();
        }

        return ValidationResult.fail("invalid pawn movement");
    }

 
    private ValidationResult validateKnight(char[][] board, char knight, int fr, int fc, int tr, int tc) {
        int dr = Math.abs(tr - fr);
        int dc = Math.abs(tc - fc);

        boolean lShape = (dr == 2 && dc == 1) || (dr == 1 && dc == 2);
        if (!lShape) {
            return ValidationResult.fail("knight must move in L shape");
        }

        // Si hi ha peça a destí, ja hem comprovat que no és pròpia.
        return ValidationResult.ok();
    }

    private boolean isInside(int r, int c) {
        return r >= 0 && r < PE07escacs.SIZE && c >= 0 && c < PE07escacs.SIZE;
    }

    private boolean isPawn(char piece) {
        return piece == 'P' || piece == 'p';
    }

    private boolean isKnight(char piece) {
        return piece == 'N' || piece == 'n';
    }

    private boolean isSameColor(char a, char b) {
        if (Character.isUpperCase(a) && Character.isUpperCase(b)) {
            return true;
        }
        if (Character.isLowerCase(a) && Character.isLowerCase(b)) {
            return true;
        }
        return false;
    }
}
