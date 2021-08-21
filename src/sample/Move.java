package sample;

public class Move {

    int from;
    int to;
    int distance;

    public Move (){
        this(0,0);
    }

    public Move (int from, int to){
        this.from = from;
        this.to = to;
        this.distance = to - from;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return from == move.from && to == move.to;
    }

    @Override
    public int hashCode() {
        return this.from + 100 * this.distance;
    }
}
