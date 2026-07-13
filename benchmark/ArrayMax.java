class ArrayMax {
    public static void main(String[] args) {
        System.out.println(new MaxFinder().find());
    }
}

class MaxFinder {
    public int find() {
        int[] arr;
        int i;
        int max;
        arr = new int[6];
        arr[0] = 3;
        arr[1] = 8;
        arr[2] = 2;
        arr[3] = 10;
        arr[4] = 5;
        arr[5] = 7;
        i = 1;
        max = arr[0];
        while (i < arr.length) {
            if (max < arr[i])
                max = arr[i];
            else
                max = max;
            i = i + 1;
        }
        return max;
    }
}
