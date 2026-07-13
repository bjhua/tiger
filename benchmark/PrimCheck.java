class PrimeCheck {
    public static void main(String[] args) {
        System.out.println(new PrimeTester().isPrime(13));
    }
}

class PrimeTester {
    public int isPrime(int n) {
        int d;
        int rem;
        int prime;
        d = 2;
        prime = 1;
        while (d < n) {
            rem = n;
            while (d < rem) {
                rem = rem - d;
            }
            if (rem < 1)
                prime = 0;
            else
                prime = prime;
            d = d + 1;
        }
        return prime;
    }
}
