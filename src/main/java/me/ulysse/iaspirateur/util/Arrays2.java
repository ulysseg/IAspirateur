package me.ulysse.iaspirateur.util;

public final class Arrays2 {

    private Arrays2() {
        throw new AssertionError();
    }

    /**
     * Convert a Double[] array to a primitive array
     */
    public static double[] toPrimitive(Double[] in) {
        double[] result = new double[in.length];
        for (int i = 0; i < in.length; i++) {
            result[i] = in[i];
        }
        return result;
    }
}
