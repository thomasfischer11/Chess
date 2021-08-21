package sample;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;

public class CCGame extends Game{

    public CCGame (ChessController controller) {
        super(controller);
    }

    public CCGame () { }

    @Override
    public void makeMove(Move move) {

    }

    @FXML
    public AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long l) {
            if (currentPosition.checkIfGameOver() == 0){
                Move move = currentPosition.calcBestMove();
                currentPosition.makeMoveBackend(move);
                controller.makeMoveFrontend(move);
                System.out.println("evaluation: " + currentPosition.evaluate());
            }
            else {
                timer.stop();
                GameOver(currentPosition.checkIfGameOver());
            }
        }
    };


}
