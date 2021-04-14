import utils.Utils;

import java.io.File;

public class Main {
    public static int counter = 0;
    public static void main(String[] args) {
        Utils utils = new Utils();
        System.out.println("Please select your combo list.");
        String combo = utils.comboList();
        File comboList = new File(combo);
        if (combo.endsWith(".txt")) {
            ThreadedShit shit1 = new ThreadedShit(), shit2 = new ThreadedShit(), shit3 = new ThreadedShit(),
                    shit4 = new ThreadedShit(), shit5 = new ThreadedShit(), shit6 = new ThreadedShit();
            shit1.start(comboList);
            shit2.start(comboList);
            shit3.start(comboList);
            shit4.start(comboList);
            shit5.start(comboList);
            shit6.start(comboList);
        } else {
            System.out.println("Invalid file type");
        }
    }
}
