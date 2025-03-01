package net.pl3x.map.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * In iterator that spirals around center coordinates in a clockwise pattern
 * <p>
 * Example using a radius of 3:
 * <pre>
 *   30 31 32 33 34 35 36
 *   29 12 13 14 15 16 37
 *   28 11 02 03 04 17 38
 *   27 10 01 00 05 18 39
 *   26 09 08 07 06 19 40
 *   25 24 23 22 21 20 41
 *   48 47 46 45 44 43 42
 * </pre>
 *
 * @param <T> The type of coordinates returned by this iterator
 */
public abstract class SpiralIterator<T> implements Iterator<T> {
    protected final long totalSteps;

    protected int currentX;
    protected int currentZ;
    protected long currentStep;

    protected long totalStepsInLeg;
    protected long currentStepInLeg;
    protected long legAxis;

    protected Direction direction = Direction.WEST;

    /**
     * Constructs a new SpiralIterator with given center coordinates and radius
     *
     * @param centerX center x coordinate
     * @param centerZ center z coordinate
     * @param radius  radius around center
     */
    public SpiralIterator(int centerX, int centerZ, int radius) {
        this.currentX = centerX;
        this.currentZ = centerZ;
        this.totalSteps = (radius * 2L + 1) * (radius * 2L + 1);
    }

    @Override
    public boolean hasNext() {
        return this.currentStep < this.totalSteps;
    }

    /**
     * Get a coordinate of specified values
     *
     * @param x X coordinate
     * @param z Z coordinate
     * @return Coordinate
     */
    protected abstract T getCoordinate(int x, int z);

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // get current coordinate
        T coordinate = this.getCoordinate(this.currentX, this.currentZ);

        // set up for the next coordinate
        switch (this.direction) {
            case SOUTH -> this.currentZ += 1;
            case WEST -> this.currentX -= 1;
            case NORTH -> this.currentZ -= 1;
            default -> this.currentX += 1;
        }

        // calculate where we are in the spiral
        this.currentStep++;
        this.currentStepInLeg++;
        if (this.currentStepInLeg > this.totalStepsInLeg) {
            this.direction = this.direction.next();
            this.currentStepInLeg = 0;
            this.legAxis++;
            if (this.legAxis > 1) {
                this.legAxis = 0;
                this.totalStepsInLeg++;
            }
        }

        // return the coordinate
        return coordinate;
    }

    /**
     * Represents the cardinal directions
     */
    public enum Direction {
        EAST, SOUTH, WEST, NORTH;

        private static final Direction[] VALUES = values();

        /**
         * Get the next direction in a clockwise pattern
         *
         * @return next direction
         */
        public Direction next() {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }
    }
}
