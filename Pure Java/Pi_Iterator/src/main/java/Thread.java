import java.io.IOException;

public class Thread extends java.lang.Thread {

    @Override
    public void run() {
        Utils utils = new Utils();
        while (true) {
            boolean indexResult = false;
            try {
                indexResult = utils.checkIndex(Utils.iteration);
            } catch (IOException | InterruptedException e) {
                System.out.println("RAN OUT OF MEMORY!");
            }

            if(indexResult) {
                System.out.println("FOUND INDEX WITH 1 MILLION PI DIGITS!!!");
                System.out.println("http://3.141592653589793238462643383279502884197169399375105820974944592.com/index" + Utils.iteration + ".html");

                for (Thread thread : Main.runningThreads) {
                    thread.stop();
                }

                break;
            }

            System.out.println("index" + Utils.iteration + ".html");
            Utils.iteration++;
        }
    }
}
