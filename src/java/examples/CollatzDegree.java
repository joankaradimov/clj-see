package examples;

public class CollatzDegree {
    private final static int CACHE_SIZE = 100000000;
    private final static long[] CACHE = new long[CACHE_SIZE];

    static {
        CACHE[0] = 1;
    }

    public static long degree(long x) {
        if ((x & 1) == 1) {
            return degreeOdd(x, 0);
        } else {
            return degreeEven(x, 0);
        }
    }

    private static long degreeEven(long x, long degree) {
        int trailingZeroes = Long.numberOfTrailingZeros(x);
        return degreeOdd(x >> trailingZeroes, degree + trailingZeroes);
    }

    private static long degreeOdd(long x, long degree) {
        if ((x >> 1) < CACHE_SIZE && CACHE[(int) (x >> 1)] != 0) {
            return CACHE[(int) (x >> 1)] + degree;
        } else {
            long result = degreeEven(3 * x + 1, 1);
            if ((x >> 1) < CACHE_SIZE) {
                CACHE[(int) (x >> 1)] = result;
            }
            return result + degree;
        }
    }
}
