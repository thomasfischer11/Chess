package sample;

import java.util.ArrayList;

public class HHGame extends Game{

    public HHGame (ChessController controller) {
        super(controller);
    }

    public HHGame () { }

    @Override
    public void makeMove (Move move) {
        currentPosition.makeMoveBackend(move);
        controller.makeMoveFrontend(move);
        GameOver(currentPosition.checkIfGameOver());
    }

}
