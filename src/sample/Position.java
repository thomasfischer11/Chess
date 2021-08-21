package sample;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Position {

    public static final String StartFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    public static final int[] StartBoard = {
            Figure.WRook, Figure.WKnight, Figure.WBishop, Figure.WQueen, Figure.WKing, Figure.WBishop, Figure.WKnight, Figure.WRook,
            Figure.WPawn, Figure.WPawn, Figure.WPawn, Figure.WPawn, Figure.WPawn, Figure.WPawn, Figure.WPawn, Figure.WPawn,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            Figure.BPawn, Figure.BPawn, Figure.BPawn, Figure.BPawn, Figure.BPawn, Figure.BPawn, Figure.BPawn, Figure.BPawn,
            Figure.BRook, Figure.BKnight, Figure.BBishop, Figure.BQueen, Figure.BKing, Figure.BBishop, Figure.BKnight, Figure.BRook
    };
    public static final int leftWhiteRookIndex = 0;
    public static final int rightWhiteRookIndex = 7;
    public static final int leftBlackRookIndex = 56;
    public static final int rightBlackRookIndex = 63;

    public int[] chessboard;
    public boolean whiteMove;
    public final ArrayList<Integer> blackFigureIndices;
    public final ArrayList<Integer> whiteFigureIndices;
    private int whiteKingIndex;
    private int blackKingIndex;
    private Move previousMove;
    private Move beforePreviousMove;
    private boolean whiteKingIsThreatened;
    private boolean blackKingIsThreatened;
    private boolean movedToCheck = false;
    public boolean previousMoveWasPromotion;
    public boolean whiteKingWasMoved;
    private boolean leftWhiteRookWasMoved;
    private boolean rightWhiteRookWasMoved;
    public boolean blackKingWasMoved;
    private boolean leftBlackRookWasMoved;
    private boolean rightBlackRookWasMoved;
    private int searchDepth;
    private Move bestMove;
    private int[] topValues = new int[5];
    private ArrayList<Move> topMoves = new ArrayList<>();


    public Position(int[] chessboard, Move previousMove, Move beforePreviousMove, boolean whiteMove, boolean whiteKingWasMoved, boolean leftWhiteRookWasMoved, boolean rightWhiteRookWasMoved, boolean blackKingWasMoved, boolean leftBlackRookWasMoved, boolean rightBlackRookWasMoved, boolean previousMoveWasPromotion) {
        this.chessboard = chessboard;
        this.whiteFigureIndices = new ArrayList<>();
        this.blackFigureIndices = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            if (chessboard[i] > 0) {
                //numberOfWhiteFigures++;
                whiteFigureIndices.add(i);
                if (chessboard[i] == Figure.WKing) whiteKingIndex = i;
            } else if (chessboard[i] < 0) {
                //numberOfBlackFigures++;
                blackFigureIndices.add(i);
                if (chessboard[i] == Figure.BKing) {
                    blackKingIndex = i;
                }
            }
        }
        this.whiteMove = whiteMove;
        this.previousMove = previousMove;
        this.beforePreviousMove = beforePreviousMove;
        this.whiteKingIsThreatened = isKingThreatened(true);
        this.blackKingIsThreatened = isKingThreatened(false);
        this.whiteKingWasMoved = whiteKingWasMoved;
        this.leftWhiteRookWasMoved = leftWhiteRookWasMoved;
        this.rightWhiteRookWasMoved = rightWhiteRookWasMoved;
        this.blackKingWasMoved = blackKingWasMoved;
        this.leftBlackRookWasMoved = leftBlackRookWasMoved;
        this.rightBlackRookWasMoved = rightBlackRookWasMoved;
        this.previousMoveWasPromotion = previousMoveWasPromotion;
    }

    public static int getRowByIndex(int index) {
        return (index >> 3) + 1;
    }

    public static int getColumnByIndex(int index) {
        return (index & 7) + 1;
    }

    public static int getIndexByRowAndColumn(int row, int column) {
        return (row - 1) * 8 + column - 1;
    }

    public static void printBoard(int[] chessboard) {
        System.out.println();
        for (int i = 7; i >= 0; i--) {
            for (int j = 0; j < 8; j++) {
                System.out.print(chessboard[8 * i + j] + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static int[] FENtoArray(String FEN) {
        int[] chessboard = new int[64];
        char c;
        int j;
        int column = 1;
        int row = 8;
        for (int i = 0, n = FEN.length(); i < n; i++) {
            c = FEN.charAt(i);
            if (c == '/') {
                row--;
                column = 1;
            } else {
                if (Character.isDigit(c)) {
                    column += Character.getNumericValue(c);
                } else {
                    j = getIndexByRowAndColumn(row, column);
                    if (c == 'k') chessboard[j] = -1024;
                    else if (c == 'K') chessboard[j] = 1024;
                    else if (c == 'q') chessboard[j] = -90;
                    else if (c == 'Q') chessboard[j] = 90;
                    else if (c == 'r') chessboard[j] = -50;
                    else if (c == 'R') chessboard[j] = 50;
                    else if (c == 'b') chessboard[j] = -32;
                    else if (c == 'B') chessboard[j] = 32;
                    else if (c == 'n') chessboard[j] = -30;
                    else if (c == 'N') chessboard[j] = 30;
                    else if (c == 'p') chessboard[j] = -10;
                    else if (c == 'P') chessboard[j] = 10;
                    column++;
                }
            }
        }
        return chessboard;
    }

    public int makeMoveBackend(Move move) {
        previousMoveWasPromotion = false;
        //check if kings/rooks are moved (because of castling)
        if (!(whiteKingWasMoved && blackKingWasMoved)) checkIfKingsOrRooksAreMoved(move);
        // remove beaten figure
        if (chessboard[move.to] > 0) whiteFigureIndices.remove((Integer) move.to);
        //if (chessboard[move.to] > 0) numberOfWhiteFigures--;
        if (chessboard[move.to] < 0) blackFigureIndices.remove((Integer) move.to);
        //if (chessboard[move.to] < 0) numberOfBlackFigures--;
        // update figure list
        updateFigureIndices(move.from, move.to);
        // make move
        int beatenValue = chessboard[move.to];
        chessboard[move.to] = chessboard[move.from];
        chessboard[move.from] = 0;
        // was that a special move ?
        if (Math.abs(chessboard[move.to]) == Figure.WPawn) {
            if (getRowByIndex(move.to) == 8 || getRowByIndex(move.to) == 1) { // promotion
                chessboard[move.to] *= 9;
                previousMoveWasPromotion = true;
            } else if (move.distance % 8 != 0 && beatenValue == 0) { // en passant
                if (whiteMove) {
                    blackFigureIndices.remove(Integer.valueOf(move.to - 8));
                    beatenValue = -10;
                    chessboard[move.to - 8] = 0;
                } else {
                    whiteFigureIndices.remove(Integer.valueOf(move.to + 8));
                    beatenValue = 10;
                    chessboard[move.to + 8] = 0;
                }
            }
        }
        if (Math.abs(chessboard[move.to]) == Figure.WKing) { // castling ?
            if (move.distance == 2) { // castling short
                chessboard[move.to - 1] = chessboard[move.to + 1];
                chessboard[move.to + 1] = 0;
                updateFigureIndices(move.to + 1, move.to - 1);
            } else if (move.distance == -2) { // castling long
                chessboard[move.to + 1] = chessboard[move.to - 2];
                chessboard[move.to - 2] = 0;
                updateFigureIndices(move.to - 2, move.to + 1);
            }
        }
        // change whose move it is
        whiteMove = !whiteMove;
        // check if kings are threatened now.
        if ((whiteMove && (chessboard[move.to] == -30 || areOnSameLine(move.from, whiteKingIndex) || areOnSameLine(move.to, whiteKingIndex))) || whiteKingIsThreatened || areOnSameLine(move.from, whiteKingIndex))
            whiteKingIsThreatened = isKingThreatened(true);
        if ((!whiteMove && (chessboard[move.to] == 30 || areOnSameLine(move.from, blackKingIndex) || areOnSameLine(move.to, blackKingIndex))) || blackKingIsThreatened || areOnSameLine(move.from, blackKingIndex))
            blackKingIsThreatened = isKingThreatened(false);
        if (whiteMove) movedToCheck = blackKingIsThreatened;
        else movedToCheck = whiteKingIsThreatened;
        // set previous moves
        beforePreviousMove = previousMove;
        previousMove = move;
        return beatenValue;
    }

    private boolean areOnSameLine(int index1, int index2) {
        return (getRowByIndex(index1) == getRowByIndex(index2) || getColumnByIndex(index1) == getColumnByIndex(index2) || (index1 - index2) % 9 == 0 || (index1 - index2) % 7 == 0);
    }

    public void undoMoveBackend(Move move, int beatenValue, Move beforePrevious, boolean[] savedBooleans) {
        //set booleans
        whiteKingWasMoved = savedBooleans[0];
        leftWhiteRookWasMoved = savedBooleans[1];
        rightWhiteRookWasMoved = savedBooleans[2];
        blackKingWasMoved = savedBooleans[3];
        leftBlackRookWasMoved = savedBooleans[4];
        rightBlackRookWasMoved = savedBooleans[5];
        previousMoveWasPromotion = savedBooleans[6];
        whiteKingIsThreatened = savedBooleans[7];
        blackKingIsThreatened = savedBooleans[8];
        // update figure list
        updateFigureIndices(move.to, move.from);
        // undo move
        chessboard[move.from] = chessboard[move.to];
        chessboard[move.to] = beatenValue;
        // add beaten figure
        if (beatenValue > 0) whiteFigureIndices.add(move.to);
        if (beatenValue < 0) blackFigureIndices.add(move.to);
        //check for special moves
        if (Math.abs(chessboard[move.from]) == Figure.WPawn) {
            if (move instanceof EnPassant) { // en passant
                chessboard[move.to] = 0;
                if (!whiteMove) {
                    updateFigureIndices(move.to, move.to - 8);
                    chessboard[move.to - 8] = -10;
                } else {
                    updateFigureIndices(move.to, move.to + 8);
                    chessboard[move.to + 8] = 10;
                }
            }
        }
        if (previousMoveWasPromotion) { // promotion
            chessboard[move.from] /= 9;
            previousMoveWasPromotion = false;
        }
        if (Math.abs(chessboard[move.from]) == Figure.WKing) { // castling ?
            if (move.distance == 2) { // castling short
                chessboard[move.to + 1] = chessboard[move.to - 1];
                chessboard[move.to - 1] = 0;
                updateFigureIndices(move.to - 1, move.to + 1);
            } else if (move.distance == -2) { // castling long
                chessboard[move.to - 2] = chessboard[move.to + 1];
                chessboard[move.to + 1] = 0;
                updateFigureIndices(move.to + 1, move.to - 2);
            }
        }
        whiteMove = !whiteMove;
        if (whiteMove) movedToCheck = blackKingIsThreatened;
        else movedToCheck = whiteKingIsThreatened;
        previousMove = beforePreviousMove;
        beforePreviousMove = beforePrevious;
    }

    public ArrayList<Move> computePseudoLegalMoves(int index) {
        ArrayList<Move> moves = new ArrayList<>();
        int figureValue = chessboard[index];
        if (figureValue != 0) { // field not empty
            int row = getRowByIndex(index);
            int column = getColumnByIndex(index);
            if (figureValue == Figure.WPawn) {
                if (chessboard[index + 8] == 0) {
                    moves.add(new Move(index, index + 8));
                    if (row == 2 && chessboard[index + 16] == 0) moves.add(new Move(index, index + 16));
                }
                if (column > 1 && chessboard[index + 7] < 0) moves.add(new Move(index, index + 7));
                if (column < 8 && chessboard[index + 9] < 0) moves.add(new Move(index, index + 9));
                if (row == 5) { //check en passant
                    if (previousMove.from - index == 15 && previousMove.to - index == -1 && chessboard[index - 1] == Figure.BPawn) {
                        moves.add(new EnPassant(index, index + 7, index - 1));
                    } else if (previousMove.from - index == 17 && previousMove.to - index == 1 && chessboard[index + 1] == Figure.BPawn) {
                        moves.add(new EnPassant(index, index + 9, index + 1));
                    }
                }
            } else if (figureValue == Figure.BPawn) {
                if (chessboard[index - 8] == 0) {
                    moves.add(new Move(index, index - 8));
                    if (getRowByIndex(index) == 7 && chessboard[index - 16] == 0)
                        moves.add(new Move(index, index - 16));
                }
                if (column < 8 && chessboard[index - 7] > 0) moves.add(new Move(index, index - 7));
                if (column > 1 && chessboard[index - 9] > 0) moves.add(new Move(index, index - 9));
                if (getRowByIndex(index) == 4) { //check en passant
                    if (previousMove.from - index == -15 && previousMove.to - index == 1 && chessboard[index + 1] == Figure.WPawn)
                        moves.add(new EnPassant(index, index - 7, index + 1));
                    else if (previousMove.from - index == -17 && previousMove.to - index == -1 && chessboard[index - 1] == Figure.WPawn)
                        moves.add(new EnPassant(index, index - 9, index - 1));
                }
            } else if (Math.abs(figureValue) == Figure.WKnight) {
                if (column < 8) {
                    if (column < 7) {
                        if (row < 8 && figureValue * chessboard[index + 10] <= 0)
                            moves.add(new Move(index, index + 10));
                        if (row > 1 && figureValue * chessboard[index - 6] <= 0)
                            moves.add(new Move(index, index - 6));
                    }
                    if (row < 7 && figureValue * chessboard[index + 17] <= 0)
                        moves.add(new Move(index, index + 17));
                    if (row > 2 && figureValue * chessboard[index - 15] <= 0)
                        moves.add(new Move(index, index - 15));
                }
                if (column > 1) {
                    if (column > 2) {
                        if (row < 8 && figureValue * chessboard[index + 6] <= 0)
                            moves.add(new Move(index, index + 6));
                        if (row > 1 && figureValue * chessboard[index - 10] <= 0)
                            moves.add(new Move(index, index - 10));
                    }
                    if (row < 7 && figureValue * chessboard[index + 15] <= 0)
                        moves.add(new Move(index, index + 15));
                    if (row > 2 && figureValue * chessboard[index - 17] <= 0)
                        moves.add(new Move(index, index - 17));
                }
            } else if (Math.abs(figureValue) == Figure.WBishop) {
                addMovesInDirection(index, moves, Figure.DiagonalMoves);
            } else if (Math.abs(figureValue) == Figure.WRook) {
                addMovesInDirection(index, moves, Figure.StraightMoves);
            } else if (Math.abs(figureValue) == Figure.WQueen) {
                addMovesInDirection(index, moves, Figure.StraightMoves);
                addMovesInDirection(index, moves, Figure.DiagonalMoves);
            } else if (whiteKingIndex == index || blackKingIndex == index) {
                for (int i = 0; i < 4; i++) {
                    if (index + Figure.StraightMoves[i] >= 0 && index + Figure.StraightMoves[i] < 64 && figureValue * chessboard[index + Figure.StraightMoves[i]] <= 0 && Math.abs(getColumnByIndex(index + Figure.StraightMoves[i]) - getColumnByIndex(index)) < 2)
                        moves.add(new Move(index, index + Figure.StraightMoves[i]));
                    if (index + Figure.DiagonalMoves[i] >= 0 && index + Figure.DiagonalMoves[i] < 64 && figureValue * chessboard[index + Figure.DiagonalMoves[i]] <= 0 && Math.abs(getColumnByIndex(index + Figure.DiagonalMoves[i]) - getColumnByIndex(index)) < 2)
                        moves.add(new Move(index, index + Figure.DiagonalMoves[i]));
                }
                //check for castling
                if (whiteKingIndex == index && !whiteKingWasMoved && !whiteKingIsThreatened) {
                    if (!leftWhiteRookWasMoved) if (isNoFigureBetween(whiteKingIndex, leftWhiteRookIndex))
                        moves.add(new Move(index, index - 2));
                    if (!rightWhiteRookWasMoved) if (isNoFigureBetween(whiteKingIndex, rightWhiteRookIndex))
                        moves.add(new Move(index, index + 2));
                }
                if (blackKingIndex == index && !blackKingWasMoved && !blackKingIsThreatened) {
                    if (!leftBlackRookWasMoved) if (isNoFigureBetween(blackKingIndex, leftBlackRookIndex))
                        moves.add(new Move(index, index - 2));
                    if (!rightBlackRookWasMoved) if (isNoFigureBetween(blackKingIndex, rightBlackRookIndex))
                        moves.add(new Move(index, index + 2));
                }
            }
        }
        return moves;
    }

    private void addMovesInDirection(int index, ArrayList<Move> moves, int[] directionMoves) {
        for (int i = 0; i < 4; i++) {
            for (int j = 1; index + directionMoves[i] * j >= 0 && index + directionMoves[i] * j < 64 && Math.abs(getColumnByIndex(index + directionMoves[i] * j) - getColumnByIndex(index + directionMoves[i] * (j - 1))) < 2; j++) {
                if (chessboard[index + directionMoves[i] * j] != 0) {
                    if (chessboard[index] * chessboard[index + directionMoves[i] * j] < 0)
                        moves.add(new Move(index, index + directionMoves[i] * j));
                    break;
                }
                moves.add(new Move(index, index + directionMoves[i] * j));
            }
        }
    }

    private boolean isNoFigureBetween(int figure1Index, int figure2Index) {
        if (figure1Index > figure2Index) {
            int assuredValue = figure1Index;
            figure1Index = figure2Index;
            figure2Index = assuredValue;
        }
        for (int i = figure1Index + 1; i < figure2Index; i++) {
            if (chessboard[i] != 0) return false;
        }
        return true;
    }

    public ArrayList<Move> computeLegalMoves(int index) {
        ArrayList<Move> moves = computePseudoLegalMoves(index);
        if (!moves.isEmpty()) {
            ArrayList<Move> invalidMoves = new ArrayList<>();
            // check whose move this is , if king is threatened -> check all moves, if not, just check moves that could get king threatened
            if (whiteMove) {
                if (whiteKingIsThreatened) checkMoves(moves, invalidMoves, true);
                else checkMoves(getPotentiallyInvalidMoves(moves, whiteKingIndex), invalidMoves, true);
            } else {
                if (blackKingIsThreatened) checkMoves(moves, invalidMoves, false);
                else checkMoves(getPotentiallyInvalidMoves(moves, blackKingIndex), invalidMoves, false);
            }
            moves.removeAll(invalidMoves);
            // remove castling move if king has to move across a threatened field (= if 'moves' doesn't contain move to the threatened field anymore)
            if (!moves.isEmpty()) {
                if (moves.get(0).from == whiteKingIndex) removeInvalidCastling(moves, whiteKingIndex);
                else if (moves.get(0).from == blackKingIndex) removeInvalidCastling(moves, blackKingIndex);
            }
        }
        return moves;
    }

    private void removeInvalidCastling(ArrayList<Move> moves, int kingIndex) {
        if (!moves.contains(new Move(kingIndex, kingIndex + 1))) {
            moves.remove(new Move(kingIndex, kingIndex + 2));
        }
        if (!moves.contains(new Move(kingIndex, kingIndex - 1))) moves.remove(new Move(kingIndex, kingIndex - 2));

    }

    private ArrayList<Move> getPotentiallyInvalidMoves(ArrayList<Move> moves, int kingIndex) { // king isn't threatened atm
        int figureIndex;
        int kingRow = getRowByIndex(kingIndex);
        int kingColumn = getColumnByIndex(kingIndex);
        ArrayList<Move> potentiallyInvalidMoves = new ArrayList<>();
        for (Move m : moves) { // check if the figure to move is on same row/column/diagonal as king --> if not, move is definitely legit and must not be checked later
            figureIndex = m.from;
            if ((getRowByIndex(figureIndex) == kingRow || getColumnByIndex(figureIndex) == kingColumn || (figureIndex - kingIndex) % 9 == 0 || (figureIndex - kingIndex) % 7 == 0)) {
                potentiallyInvalidMoves.add(m);
            }
        }
        return potentiallyInvalidMoves;
    }

    private void checkMoves(ArrayList<Move> moves, ArrayList<Move> invalidMoves, boolean whiteMoves) {
        int beatenValue;
        Move beforePrevious = beforePreviousMove;
        boolean[] savedBooleans = {whiteKingWasMoved, leftWhiteRookWasMoved, rightWhiteRookWasMoved, blackKingWasMoved, leftBlackRookWasMoved, rightBlackRookWasMoved, false, whiteKingIsThreatened, blackKingIsThreatened};
        //Position p = this.copy();
        for (Move move : moves) {
            beatenValue = makeMoveBackend(move);
            savedBooleans[6] = previousMoveWasPromotion;
            if ((whiteMoves && whiteKingIsThreatened) || (!whiteMoves && blackKingIsThreatened))
                invalidMoves.add(move); //if king threatened, remove this from possible moves
            undoMoveBackend(move, beatenValue, beforePrevious, savedBooleans);
        }
    }

    private Position copy() {
        int[] newChessboard = new int[64];
        System.arraycopy(chessboard, 0, newChessboard, 0, 64);
        Move newPreviousMove = new Move(previousMove.from, previousMove.to);
        Move newBeforePreviousMove = new Move(beforePreviousMove.from, beforePreviousMove.to);
        return new Position(newChessboard, newPreviousMove, newBeforePreviousMove, whiteMove, whiteKingWasMoved, leftWhiteRookWasMoved, rightWhiteRookWasMoved, blackKingWasMoved, leftBlackRookWasMoved, rightBlackRookWasMoved, previousMoveWasPromotion);
    }

    private void checkIfKingsOrRooksAreMoved(Move move) {
        if (!whiteKingWasMoved) {
            if (move.from == whiteKingIndex) whiteKingWasMoved = true;
            if (!leftWhiteRookWasMoved)
                if (move.from == leftWhiteRookIndex || move.to == leftWhiteRookIndex) leftWhiteRookWasMoved = true;
            if (!rightWhiteRookWasMoved)
                if (move.from == rightWhiteRookIndex || move.to == rightWhiteRookIndex) rightWhiteRookWasMoved = true;
        }
        if (!blackKingWasMoved) {
            if (move.from == blackKingIndex) blackKingWasMoved = true;
            if (!leftBlackRookWasMoved)
                if (move.from == leftBlackRookIndex || move.to == leftBlackRookIndex) leftBlackRookWasMoved = true;
            if (!rightBlackRookWasMoved)
                if (move.from == rightBlackRookIndex || move.to == rightBlackRookIndex) rightBlackRookWasMoved = true;
        }
    }

    public int checkIfGameOver() {
        if (whiteMove) {
            if (whiteKingIsThreatened && isThereNoPossibleMove()) return 3;
            else if (whiteFigureIndices.size() < 5 && isThereNoPossibleMove()) return 1;
        } else {
            if (blackKingIsThreatened && isThereNoPossibleMove()) return 2;
            else if (blackFigureIndices.size() < 5 && isThereNoPossibleMove()) return 1;
        }
        if (whiteFigureIndices.size() + blackFigureIndices.size() < 4) {
            if (whiteFigureIndices.size() + blackFigureIndices.size() < 3) return 1;
            else if (whiteFigureIndices.size() == 2 && (chessboard[whiteFigureIndices.get(0)] == Figure.WKnight || chessboard[whiteFigureIndices.get(0)] == Figure.WBishop || chessboard[whiteFigureIndices.get(1)] == Figure.WKnight || chessboard[whiteFigureIndices.get(1)] == Figure.WBishop))
                return 1;
            else if (blackFigureIndices.size() == 2 && (chessboard[blackFigureIndices.get(0)] == Figure.BKnight || chessboard[blackFigureIndices.get(0)] == Figure.BBishop || chessboard[blackFigureIndices.get(1)] == Figure.BKnight || chessboard[blackFigureIndices.get(1)] == Figure.BBishop))
                return 1;
        }
        return 0;
    }

    private boolean isKingThreatened(boolean whiteKing) {
        if (whiteKing) {
            /*for (int i = 0; i < 64; i++) {
                if (chessboard[i] < 0){
                    if (areOnSameLine(i, whiteKingIndex) || chessboard[i] == Figure.BKnight){
                        for (Move move : computePseudoLegalMoves(i)) {
                            //System.out.print(" " + m.to);
                            if (move.to == whiteKingIndex) {
                                //System.out.println("figure: " + i + "move: from: " + move.from + " to: " + move.to );
                                return true;
                            }
                        }
                    }
                }
                //System.out.println();
            }*/
            for (Integer i : blackFigureIndices) {
                if (areOnSameLine(i, whiteKingIndex) || chessboard[i] == Figure.BKnight) {
                    for (Move move : computePseudoLegalMoves(i)) {
                        //System.out.print(" " + m.to);
                        if (move.to == whiteKingIndex) {
                            //System.out.println("figure: " + i + "move: from: " + move.from + " to: " + move.to );
                            return true;
                        }
                    }
                }
                //System.out.println();
            }
        } else {
            /*for (int i = 0; i <64; i++) {
                if (chessboard[i] > 0){
                    //System.out.println(i);
                    if (areOnSameLine(i, blackKingIndex) || chessboard[i] == Figure.WKnight){
                        for (Move move : computePseudoLegalMoves(i)) {
                            //System.out.println(" " + m.to);
                            if (move.to == blackKingIndex) {
                                //System.out.println("figure: " + i + "move: from: " + move.from + " to: " + move.to );
                                return true;
                            }
                        }
                    }
                }
            }*/
            for (Integer i : whiteFigureIndices) {
                //System.out.println(i);
                if (areOnSameLine(i, blackKingIndex) || chessboard[i] == Figure.WKnight) {
                    for (Move move : computePseudoLegalMoves(i)) {
                        //System.out.println(" " + m.to);
                        if (move.to == blackKingIndex) {
                            //System.out.println("figure: " + i + "move: from: " + move.from + " to: " + move.to );
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public ArrayList<Move> getAllPossibleMoves() {
        ArrayList<Integer> figureIndices = new ArrayList<>();
        ArrayList<Move> allMoves = new ArrayList<>();
        if (whiteMove) {
            figureIndices.addAll(whiteFigureIndices);
        } else {
            figureIndices.addAll(blackFigureIndices);
        }
        for (Integer i : figureIndices) {
            allMoves.addAll(computeLegalMoves(i));
        }
        return allMoves;
    }

    public ArrayList<Move> getAllPseudoLegalMoves() {
        ArrayList<Integer> figureIndices = new ArrayList<>();
        ArrayList<Move> allMoves = new ArrayList<>();
        if (whiteMove) {
            figureIndices.addAll(whiteFigureIndices);
        } else {
            figureIndices.addAll(blackFigureIndices);
        }
        for (Integer i : figureIndices) {
            allMoves.addAll(computePseudoLegalMoves(i));
        }
        return allMoves;
    }

    private boolean isThereNoPossibleMove() {
        return getAllPossibleMoves().isEmpty();
    }

    private void updateFigureIndices(int from, int to) {
        if (whiteFigureIndices.contains(from)) {
            whiteFigureIndices.set(whiteFigureIndices.indexOf(from), to);
            if (whiteKingIndex == from) whiteKingIndex = to;
        } else if (blackFigureIndices.contains(from)) {
            blackFigureIndices.set(blackFigureIndices.indexOf(from), to);
            if (blackKingIndex == from) blackKingIndex = to;
        }
    }

    public int moveComputingTest(int depth) {
        if (depth == 0) {
            return 1;
        }
        boolean[] savedBooleans = {whiteKingWasMoved, leftWhiteRookWasMoved, rightWhiteRookWasMoved, blackKingWasMoved, leftBlackRookWasMoved, rightBlackRookWasMoved, false, whiteKingIsThreatened, blackKingIsThreatened};
        Move beforePrevious = new Move(beforePreviousMove.from, beforePreviousMove.to);
        int beatenValue;
        int totalPositions = 0;
        for (Move move : getAllPseudoLegalMoves()) {
            beatenValue = makeMoveBackend(move);
            savedBooleans[6] = previousMoveWasPromotion;
            if (!movedToCheck) {
                totalPositions += moveComputingTest(depth - 1);
            }
            undoMoveBackend(move, beatenValue, beforePrevious, savedBooleans);
        }
        return totalPositions;
    }

    public int evaluate() {
        int value = 0;
        int gameState = checkIfGameOver();

        if (gameState != 0) {
            if (gameState == 1) return 0;
            else if (gameState == 2) value = 1024;
            else if (gameState == 3) value = -1024;
        } else {
            for (int i = 0; i < 64; i++) {
                value += chessboard[i];
            }

            boolean assuredWhiteMove = whiteMove;
            whiteMove = true;
            if (!whiteKingIsThreatened) value += getAllPseudoLegalMoves().size();
            whiteMove = false;
            if (!blackKingIsThreatened) value -= getAllPseudoLegalMoves().size();
            whiteMove = assuredWhiteMove;
        }
        if (!whiteMove) value = -value;
        return value;
    }


    public Move calcBestMove() {
        bestMove = null;
        long startTime = System.nanoTime();
        for (int i = 1; (System.nanoTime() - startTime) / 1000000 < 800f; i++) {
            searchDepth = i;
            for (int j = 0; j < 5; j++) topValues[j] = -2048;
            alphaBetaMiniMax(searchDepth, -2048, 2048);
            for (Move move : topMoves) {
                System.out.println("top move from: " + move.from + " to: " + move.to);
            }
            System.out.println();
        }
        System.out.println("time spent to search best move (depth " + searchDepth + ") : " + (System.nanoTime() - startTime) / 1000000 + " ms");
        topMoves.clear();
        //int figuresRemaining = whiteFigureIndices.size() + blackFigureIndices.size();
        //if (figuresRemaining < 17) searchDepth++;
        //if (figuresRemaining < 9) searchDepth++;
        //miniMax(depth);
        //alphaBetaMiniMax(searchDepth, -2048, 2048);
        return bestMove;
    }

    int alphaBetaMiniMax(int depth, int alpha, int beta) {
        if (depth == 0) {
            return evaluate();
        }
        ArrayList<Move> moves = getAllPseudoLegalMoves();
        if (moves.isEmpty()) return evaluate();
        if (depth == searchDepth) {
            moves.removeAll(topMoves);
            moves.addAll(0, topMoves);
        }
        int maxValue = alpha;
        boolean[] savedBooleans = {whiteKingWasMoved, leftWhiteRookWasMoved, rightWhiteRookWasMoved, blackKingWasMoved, leftBlackRookWasMoved, rightBlackRookWasMoved, false, whiteKingIsThreatened, blackKingIsThreatened};
        Move beforePrevious = new Move(beforePreviousMove.from, beforePreviousMove.to);
        int value;
        int beatenValue;
        for (Move move : moves) {
            beatenValue = makeMoveBackend(move);
            savedBooleans[6] = previousMoveWasPromotion;
            if (!movedToCheck) value = -alphaBetaMiniMax(depth - 1, -beta, -maxValue);
            else value = -2048;
            undoMoveBackend(move, beatenValue, beforePrevious, savedBooleans);
            if (value > maxValue) {
                maxValue = value;
                if (depth == searchDepth) bestMove = move;
                if (maxValue >= beta) break;
            }
            if (depth == searchDepth) {
                if (value > topValues[0]) {
                    if (!topMoves.contains(move)) {
                        int i;
                        for (i = 0; i < 5; i++) {
                            if (value < topValues[i]) {
                                i--;
                                break;
                            }
                        }
                        if (i == 5) i--;
                        if (topMoves.size() > i) topMoves.add(i, move);
                        else topMoves.add(move);
                        if (topMoves.size() > 5) topMoves.remove(0);
                        for (int j = 0; j < i; j++) {
                            topValues[j] = topValues[j + 1];
                        }
                        topValues[i] = value;
                    }
                }
            }
        }

        return maxValue;
    }

    int miniMax(int depth) {
        if (depth == 0) {
            return evaluate();
        }
        ArrayList<Move> moves = getAllPossibleMoves();
        if (moves.isEmpty()) return evaluate();
        int maxValue = -2048;
        boolean[] savedBooleans = {whiteKingWasMoved, leftWhiteRookWasMoved, rightWhiteRookWasMoved, blackKingWasMoved, leftBlackRookWasMoved, rightBlackRookWasMoved, false, whiteKingIsThreatened, blackKingIsThreatened};
        Move beforePrevious = new Move(beforePreviousMove.from, beforePreviousMove.to);
        int beatenValue;
        for (Move move : moves) {
            beatenValue = makeMoveBackend(move);
            savedBooleans[6] = previousMoveWasPromotion;
            int value = -miniMax(depth - 1);
            undoMoveBackend(move, beatenValue, beforePrevious, savedBooleans);
            if (value > maxValue) {
                maxValue = value;
                if (depth == searchDepth) {
                    bestMove = move;
                }
            }
        }
        return maxValue;
    }

}
