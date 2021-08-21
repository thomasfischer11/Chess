package sample;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;

public class Figure {

    public static final int None = 0;
    public static final int WPawn = 10;
    public static final int BPawn = -10;
    public static final int WKnight = 30;
    public static final int BKnight = -30;
    public static final int WBishop = 32;
    public static final int BBishop = -32;
    public static final int WRook = 50;
    public static final int BRook = -50;
    public static final int WQueen = 90;
    public static final int BQueen = -90;
    public static final int WKing = 1024;
    public static final int BKing = -1024;

    public static final int [] KnightMoves = {6,10,15,17};
    public static final int [] DiagonalMoves = {7,9,-7,-9};
    public static final int [] StraightMoves = {1,8,-1,-8};


    public static final Image WPawnImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\wp.png").toURI().toString(), 100, 100, false, false);
    public static final Image BPawnImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\bp.png").toURI().toString(), 100, 100, false, false);
    public static final Image WKnightImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\wn.png").toURI().toString(), 100, 100, false, false);
    public static final Image BKnightImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\bn.png").toURI().toString(), 100, 100, false, false);
    public static final Image WBishopImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\wb.png").toURI().toString(), 100, 100, false, false);
    public static final Image BBishopImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\bb.png").toURI().toString(), 100, 100, false, false);
    public static final Image WRookImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\wr.png").toURI().toString(), 100, 100, false, false);
    public static final Image BRookImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\br.png").toURI().toString(), 100, 100, false, false);
    public static final Image WQueenImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\wq.png").toURI().toString(), 100, 100, false, false);
    public static final Image BQueenImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\bq.png").toURI().toString(), 100, 100, false, false);
    public static final Image WKingImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\wk.png").toURI().toString(), 100, 100, false, false);
    public static final Image BKingImage = new Image(new File("C:\\Users\\Tomate\\Pictures\\schach_figuren\\bk.png").toURI().toString(), 100, 100, false, false);

}
