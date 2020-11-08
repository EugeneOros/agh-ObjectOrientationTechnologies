public class Consumer extends Thread {
    private Buffer _buf;
    private final int M;

    public Consumer(Buffer buf, int M) {
        this._buf = buf;
        this.M = M;
    }

    public void run() {
        while(true) {
            _buf.get((int) (Math.random()*(M-1)+1));
            try {
                sleep((int) (Math.random() * 100));
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}