package sample;

public class EnPassant extends Move {

    public int beatenPawnIndex;

    public EnPassant(int from, int to, int beatenPawnIndex) {
        super(from, to);
        this.beatenPawnIndex = beatenPawnIndex;
    }



}
