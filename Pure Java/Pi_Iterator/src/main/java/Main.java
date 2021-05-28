import java.util.ArrayList;
import java.util.List;

public class Main {
    public static List<Thread> runningThreads = new ArrayList<>();

    public static void main(String[] args) {
        for(int i = 0; i < 20; i++) {
            try {
                Thread thread = new Thread();
                thread.start();
                runningThreads.add(thread);
                Thread.sleep(75);
            } catch (InterruptedException e) {
                System.out.println("OTHER ERROR HAS OCCURRED");
            }
        }
    }

}
