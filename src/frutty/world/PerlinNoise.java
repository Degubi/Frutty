package frutty.world;

import java.util.*;

public final class PerlinNoise {

    public static int[] generatePermutation(Random rand) {
        var permutation = new int[512];

        for(var i = 0; i < 256; i++){
            permutation[i] = i;
        }

        for(var i = 255; i > 0; --i) {
            var index = (int) Math.round(rand.nextDouble() * (i - 1));
            var old = permutation[i];

            permutation[i] = permutation[index];
            permutation[index] = old;
        }

        System.arraycopy(permutation, 0, permutation, 256, 256);
        return permutation;
    }

    public static double generateNoise(double x, double y, int[] permutation) {
        var xFloor = Math.floor(x);
        var yFloor = Math.floor(y);
        var xMinusFloor = x - xFloor;
        var yMinusFloor = y - yFloor;
        var xIndex = ((int) xFloor) & 255;
        var yIndex = ((int) yFloor) & 255;
        var dotTopRight = dot(xMinusFloor - 1.0, yMinusFloor - 1.0, getConstantVector(permutation[permutation[xIndex + 1] + yIndex +1]));
        var dotTopLeft = dot(xMinusFloor, yMinusFloor - 1.0, getConstantVector(permutation[permutation[xIndex] + yIndex + 1]));
        var dotBottomRight = dot(xMinusFloor - 1.0, yMinusFloor, getConstantVector(permutation[permutation[xIndex + 1] + yIndex]));
        var dotBottomLeft = dot(xMinusFloor, yMinusFloor, getConstantVector(permutation[permutation[xIndex] + yIndex]));
        var u = fade(xMinusFloor);
        var v = fade(yMinusFloor);

        return (lerp(u, lerp(v, dotBottomLeft, dotTopLeft), lerp(v, dotBottomRight, dotTopRight)) + 1.0) * 0.5;
    }

    private static Vector2d getConstantVector(int value) {
        return switch(value & 3) {
            case 0  -> new Vector2d(1.0, 1.0);
            case 1  -> new Vector2d(-1.0, 1.0);
            case 2  -> new Vector2d(-1.0, -1.0);
            default -> new Vector2d(1.0, -1.0);
        };
    }

    private static double fade(double t) {
        return ((6 * t - 15) * t + 10) * t * t * t;
    }

    private static double lerp(double t, double a1, double a2) {
        return a1 + t * (a2 - a1);
    }

    private static double dot(double vec1X, double vec1Y, Vector2d vec2) {
        return vec1X * vec2.x + vec1Y * vec2.y;
    }

    private PerlinNoise() {}

    private static final class Vector2d {

        public final double x;
        public final double y;

        public Vector2d(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}