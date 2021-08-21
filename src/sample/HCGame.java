package sample;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;

public class HCGame extends Game{
    private boolean computerIsWhite;

    public HCGame (ChessController controller, boolean computerIsWhite) {
        super(controller);
        this.computerIsWhite = computerIsWhite;
    }

    public HCGame () { }


    @Override
    public void makeMove (Move move) {
        currentPosition.makeMoveBackend(move);
        controller.makeMoveFrontend(move);
        System.out.println("evaluation: " + currentPosition.evaluate());
        int gameState = currentPosition.checkIfGameOver();
        if (gameState == 0){
            if (currentPosition.whiteMove == computerIsWhite){
                makeMove(currentPosition.calcBestMove());
            }
        }
        else  GameOver(gameState);
    }

    @FXML
    public AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long l) {
            if (currentPosition.whiteMove == computerIsWhite){
                Move move = currentPosition.calcBestMove();
                System.out.println("computer making move from " + move.from + " to " + move.to);
                currentPosition.makeMoveBackend(move);
                controller.makeMoveFrontend(move);
                timer.stop();
            }
            else {
                System.out.println("making move from " + controller.currentMove.from + " to " + controller.currentMove.to);
                currentPosition.makeMoveBackend(controller.currentMove);
                controller.makeMoveFrontend(controller.currentMove);
            }
            int gameState = currentPosition.checkIfGameOver();
            if (gameState != 0){
                timer.stop();
                GameOver(gameState);
            }
        }
    };

}

