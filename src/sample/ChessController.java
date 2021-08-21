package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class ChessController {

    private Game game;
    private Pane fieldClicked;
    private int fieldClickedIndex;
    private boolean didSelectAFigure = false;
    @FXML
    public Button wonButton;
    @FXML
    private Button buttonHumanVSHuman;
    @FXML
    private Button buttonHumanVSComputer;
    @FXML
    private Button buttonComputerVSComputer;
    @FXML
    public Pane field11;
    @FXML
    public Pane field12;
    @FXML
    public Pane field13;
    @FXML
    public Pane field14;
    @FXML
    public Pane field15;
    @FXML
    public Pane field16;
    @FXML
    public Pane field17;
    @FXML
    public Pane field18;
    @FXML
    public Pane field21;
    @FXML
    public Pane field22;
    @FXML
    public Pane field23;
    @FXML
    public Pane field24;
    @FXML
    public Pane field25;
    @FXML
    public Pane field26;
    @FXML
    public Pane field27;
    @FXML
    public Pane field28;
    @FXML
    public Pane field31;
    @FXML
    public Pane field32;
    @FXML
    public Pane field33;
    @FXML
    public Pane field34;
    @FXML
    public Pane field35;
    @FXML
    public Pane field36;
    @FXML
    public Pane field37;
    @FXML
    public Pane field38;
    @FXML
    public Pane field41;
    @FXML
    public Pane field42;
    @FXML
    public Pane field43;
    @FXML
    public Pane field44;
    @FXML
    public Pane field45;
    @FXML
    public Pane field46;
    @FXML
    public Pane field47;
    @FXML
    public Pane field48;
    @FXML
    public Pane field51;
    @FXML
    public Pane field52;
    @FXML
    public Pane field53;
    @FXML
    public Pane field54;
    @FXML
    public Pane field55;
    @FXML
    public Pane field56;
    @FXML
    public Pane field57;
    @FXML
    public Pane field58;
    @FXML
    public Pane field61;
    @FXML
    public Pane field62;
    @FXML
    public Pane field63;
    @FXML
    public Pane field64;
    @FXML
    public Pane field65;
    @FXML
    public Pane field66;
    @FXML
    public Pane field67;
    @FXML
    public Pane field68;
    @FXML
    public Pane field71;
    @FXML
    public Pane field72;
    @FXML
    public Pane field73;
    @FXML
    public Pane field74;
    @FXML
    public Pane field75;
    @FXML
    public Pane field76;
    @FXML
    public Pane field77;
    @FXML
    public Pane field78;
    @FXML
    public Pane field81;
    @FXML
    public Pane field82;
    @FXML
    public Pane field83;
    @FXML
    public Pane field84;
    @FXML
    public Pane field85;
    @FXML
    public Pane field86;
    @FXML
    public Pane field87;
    @FXML
    public Pane field88;
    @FXML
    private GridPane chessboard;
    private ArrayList<Integer> possibleMoves = new ArrayList<>();
    ArrayList<Pane> chessFields = new ArrayList<>();
    public Move currentMove = new Move(0,0);
    private boolean currentMoveIsPromotion;

    @FXML
    public void onFieldClicked (MouseEvent m) {
        if(didSelectAFigure && possibleMoves.contains(chessFields.indexOf(m.getSource()))) {
            resetFieldColor();
            //legit move
            currentMove = new Move(fieldClickedIndex, chessFields.indexOf(m.getSource()));
            if (Math.abs(game.currentPosition.chessboard[currentMove.from]) == Figure.WPawn){
                if (Position.getRowByIndex(currentMove.to) == 8 || Position.getRowByIndex(currentMove.to) == 1) currentMoveIsPromotion = true;
            }
            if (game instanceof HHGame) game.makeMove(currentMove);
            else if (game instanceof HCGame) ((HCGame) game).timer.start();
        }
        else{
            fieldClicked = (Pane) m.getSource();
            //field not empty & is it your move?
            if(((game.currentPosition.whiteMove && game.currentPosition.chessboard[chessFields.indexOf(fieldClicked)] > 0) || (!game.currentPosition.whiteMove && game.currentPosition.chessboard[chessFields.indexOf(fieldClicked)] < 0))){
                resetFieldColor();
                fieldClickedIndex = chessFields.indexOf(fieldClicked);
                System.out.println("clicked field " + fieldClickedIndex);
                didSelectAFigure = true;
                possibleMoves = game.getPossibleMoveFields(fieldClickedIndex);
                setFieldColor();
            }
        }
    }

    private void setFieldColor () {
        //set field color for possible moves
        fieldClicked.setStyle("-fx-background-color: #00288d");
        for (Integer i: possibleMoves) {
            chessFields.get(i).setStyle("-fx-background-color: #00dbd4");
        }
    }

    private void resetFieldColor() {
        for (int i = 0; i < 64; i++) {
            if ((Position.getColumnByIndex(i) + Position.getRowByIndex(i))%2 == 0) chessFields.get(i).setStyle("-fx-background-color: darkgreen");
            else chessFields.get(i).setStyle("-fx-background-color: lightgrey");
        }
    }

    public void makeMoveFrontend(Move move) {
        //remove beaten figure (backend & frontend)
        Pane fromPane = chessFields.get(move.from);
        Pane toPane = chessFields.get(move.to);
        ImageView figureChosen = (ImageView) fromPane.getChildren().get(0);
        boolean figureBeaten = false;
        if (!chessFields.get(move.to).getChildren().isEmpty()) {
            figureBeaten = true;
            toPane.getChildren().remove(0);
        }
        //move figure frontend
        toPane.getChildren().add(figureChosen);
        //check for special moves
        if (Math.abs(game.currentPosition.chessboard[move.to]) == Figure.WPawn){
            if (move.distance % 8 != 0 && !figureBeaten){ // en passant
                if (game.currentPosition.whiteMove){
                    chessFields.get(move.to + 8).getChildren().remove(0);
                }
                else {
                    chessFields.get(move.to - 8).getChildren().remove(0);
                }
            }
        }
        if (currentMoveIsPromotion) { //promotion
            chessFields.get(move.to).getChildren().remove(0);
            if (game.currentPosition.whiteMove){
                chessFields.get(move.to).getChildren().add(new ImageView(Figure.BQueenImage));
            }
            else chessFields.get(move.to).getChildren().add(new ImageView(Figure.WQueenImage));
            currentMoveIsPromotion = false;
        }
        if (Math.abs(game.currentPosition.chessboard[move.to]) == Figure.WKing){ // castling ?
            if (move.distance == 2){ // castling short
                chessFields.get(move.to - 1).getChildren().add(chessFields.get(move.to + 1).getChildren().get(0));
            }
            else if (move.distance == -2) { // castling long
                chessFields.get(move.to + 1).getChildren().add(chessFields.get(move.to - 2).getChildren().get(0));
            }
        }
        didSelectAFigure = false;
    }

    @FXML
    public void setHHGame (MouseEvent m) {
        game = new HHGame(this);
        setGame();
    }

    @FXML
    public void setHCGame (MouseEvent m) {
        game = new HCGame(this, false);
        setGame();
    }

    @FXML
    public void setCCGame (MouseEvent m) {
        game = new CCGame(this);
        setGame();
        ((CCGame) game).timer.start();
    }

    public void setGame () {

        setChessFields();
        buttonHumanVSHuman.setVisible(false);
        buttonHumanVSComputer.setVisible(false);
        buttonComputerVSComputer.setVisible(false);
        chessboard.setVisible(true);
        //int [] board = Position.FENtoArray("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1");
        //Position.printBoard(board);
        //Position p = new Position(board, new Move(), new Move(), true , true, false, false, false, false, false, false);
        int depth = 5;
        System.out.println("computing number of nodes in game tree with depth " + depth + " ...");
        long startTime = System.nanoTime();
        int testResult = game.currentPosition.moveComputingTest(depth);
        int time = (int) ((System.nanoTime() - startTime) / 1000000);
        System.out.println("test result :" + testResult + " time spent : " + time + " ms");

        //Position.countPossiblePositions(5);
    }

    private void setChessFields () {
        if (chessFields.isEmpty()) {
            chessFields.add(field11); chessFields.add(field21);chessFields.add(field31);chessFields.add(field41);chessFields.add(field51);chessFields.add(field61);chessFields.add(field71);chessFields.add(field81);chessFields.add(field12);chessFields.add(field22);chessFields.add(field32);chessFields.add(field42);chessFields.add(field52);chessFields.add(field62);chessFields.add(field72);chessFields.add(field82);chessFields.add(field13);chessFields.add(field23);chessFields.add(field33);chessFields.add(field43);chessFields.add(field53);chessFields.add(field63);chessFields.add(field73);chessFields.add(field83);chessFields.add(field14);chessFields.add(field24);chessFields.add(field34);chessFields.add(field44);chessFields.add(field54);chessFields.add(field64);chessFields.add(field74);chessFields.add(field84);chessFields.add(field15);chessFields.add(field25);chessFields.add(field35);chessFields.add(field45);chessFields.add(field55);chessFields.add(field65);chessFields.add(field75);chessFields.add(field85);chessFields.add(field16);chessFields.add(field26);chessFields.add(field36);chessFields.add(field46);chessFields.add(field56);chessFields.add(field66);chessFields.add(field76);chessFields.add(field86);chessFields.add(field17);chessFields.add(field27);chessFields.add(field37);chessFields.add(field47);chessFields.add(field57);chessFields.add(field67);chessFields.add(field77);chessFields.add(field87);chessFields.add(field18);chessFields.add(field28);chessFields.add(field38);chessFields.add(field48);chessFields.add(field58);chessFields.add(field68);chessFields.add(field78);chessFields.add(field88);
        }
        else {
            for (int i = 0; i < 64; i++){
                System.out.println(Position.StartBoard[i]);
                if (!chessFields.get(i).getChildren().isEmpty()) chessFields.get(i).getChildren().remove(0);
                switch (Position.StartBoard[i]) {
                    case Figure.WPawn -> chessFields.get(i).getChildren().add(new ImageView(Figure.WPawnImage));
                    case Figure.BPawn -> chessFields.get(i).getChildren().add(new ImageView(Figure.BPawnImage));
                    case Figure.WKnight -> chessFields.get(i).getChildren().add(new ImageView(Figure.WKnightImage));
                    case Figure.BKnight -> chessFields.get(i).getChildren().add(new ImageView(Figure.BKnightImage));
                    case Figure.WBishop -> chessFields.get(i).getChildren().add(new ImageView(Figure.WBishopImage));
                    case Figure.BBishop -> chessFields.get(i).getChildren().add(new ImageView(Figure.BBishopImage));
                    case Figure.WRook -> chessFields.get(i).getChildren().add(new ImageView(Figure.WRookImage));
                    case Figure.BRook -> chessFields.get(i).getChildren().add(new ImageView(Figure.BRookImage));
                    case Figure.WQueen -> chessFields.get(i).getChildren().add(new ImageView(Figure.WQueenImage));
                    case Figure.BQueen -> chessFields.get(i).getChildren().add(new ImageView(Figure.BQueenImage));
                    case Figure.WKing -> chessFields.get(i).getChildren().add(new ImageView(Figure.WKingImage));
                    case Figure.BKing -> chessFields.get(i).getChildren().add(new ImageView(Figure.BKingImage));
                }
            }
        }
    }

    @FXML
    private void onWonButtonClicked (MouseEvent m) {
        chessboard.setVisible(false);
        wonButton.setVisible(false);
        buttonHumanVSHuman.setVisible(true);
        buttonHumanVSComputer.setVisible(true);
        buttonComputerVSComputer.setVisible(true);
    }

    @FXML
    private void onPromotionPaneClicked (MouseEvent m){
        /*Pane pane = (Pane) m.getSource();
        newField.getChildren().remove(0);
        System.out.println("hä");
        if (whiteMove){
            System.out.println("joa");
            if (whiteQueenPromotionPane.equals(pane)){
                System.out.println("wq");
                //newField.getChildren().add(wq);
                backendChessboard[newFieldIndex] = 9;
            }
            else if (pane.equals(whiteRookPromotionPane)) backendChessboard[newFieldIndex] = 5;
            else if (pane.equals(whiteBishopPromotionPane)) backendChessboard[newFieldIndex] = 4;
            else if (pane.equals(whiteKnightPromotionPane)) backendChessboard[newFieldIndex] = 3;
        }
        else {
            if (pane.equals(blackQueenPromotionPane)) backendChessboard[newFieldIndex] = -9;
            else if (pane.equals(blackRookPromotionPane)) backendChessboard[newFieldIndex] = -5;
            else if (pane.equals(blackBishopPromotionPane)) backendChessboard[newFieldIndex] = -4;
            else if (pane.equals(blackKnightPromotionPane)) backendChessboard[newFieldIndex] = -3;
        }
        (pane).getParent().setVisible(false);

        */
    }

}
