import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tw_lab4 {
    public static void main(String[] args) {
        int M = 100;
        int m = 100;
        int n = 100;

        Buffer buf = new Buffer(M);
        ExecutorService service = Executors.newFixedThreadPool(m + n);

        for(int i=1; i<=m; i++) {
            service.submit(new Producer(buf, M));
        }

        for(int i=1; i<=n; i++) {
            service.submit(new Consumer(buf, M));
        }

        service.shutdown();
    }
}
