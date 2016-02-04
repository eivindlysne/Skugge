package me.lysne.game.world;

public class Coord {

    public final int x;
    public final int z;

    public Coord(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + x;
        hash = hash * 31 + z;
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if ((o instanceof Coord) && ((Coord) o).x == x && ((Coord) o).z == z) {
            return true;
        } else {
            return false;
        }
    }
}