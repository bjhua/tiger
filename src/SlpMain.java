
import control.Control;
import slp.Samples;

public class SlpMain {
    public static void main(String[] args) {
        slp.Main slpmain = new slp.Main();
        if (Control.ConSlp.div) {
            slpmain.doit(Samples.divideByZero);
            System.exit(0);
        }
        slpmain.doit(Samples.prog);
        System.exit(0);
    }
}
