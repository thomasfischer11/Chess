package sample;


import java.util.ArrayList;

public abstract class Game {
    public Position currentPosition;
    public ChessController controller;

    public Game (ChessController controller) {
        this.currentPosition = new Position(Position.FENtoArray(Position.StartFEN), new Move(), new Move(), true , false, false, false, false, false, false, false);
        this.controller = controller;
    }

    public Game (){ }

    public void GameOver(int gameState) { // 0 = still in progress ; 1 = stalemate ; 2 = white wins ; 3 = black wins
        if (gameState == 0) return;
        if (gameState == 1) controller.wonButton.setText("Draw!");
        if (gameState == 2) controller.wonButton.setText("White Won!");
        if (gameState == 3) controller.wonButton.setText("Black Won!");

        controller.wonButton.setVisible(true);
    }

    public ArrayList<Integer> getPossibleMoveFields (int index){
        ArrayList<Integer> moveFields = new ArrayList<>();
        for (Move move: currentPosition.computeLegalMoves(index)) {
            moveFields.add(move.to);
        }
        return moveFields;
    }

    public abstract void makeMove (Move move);



}
