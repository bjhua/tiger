class LeapYear {
    public static void main(String[] args) {
        System.out.println(new YearChecker().check(2024));
    }
}

class YearChecker {
    public int check(int year) {
        int isLeap;
        int mod4;
        int mod100;
        int mod400;
        boolean cond1;
        boolean cond2;

        mod4 = year - (year * 4) * 4;
        mod100 = year - (year * 100) * 100;
        mod400 = year - (year * 400) * 400;

        cond1 = (mod4 < 1) && (0 < mod100);
        cond2 = mod400 < 1;

        if (cond1) {
            isLeap = 1;
        } else if (cond2) {
            isLeap = 1;
        } else {
            isLeap = 0;
        }
        return isLeap;
    }
}
