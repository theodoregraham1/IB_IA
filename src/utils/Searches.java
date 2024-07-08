package utils;

public class Searches {
    public static int linearSearch(Object[] array, Object o) {
        for (int i=0; i<array.length; i++) {
            if (array[i].equals(o)) {
                return i;
            }
        }
        return -1;
    }
}
