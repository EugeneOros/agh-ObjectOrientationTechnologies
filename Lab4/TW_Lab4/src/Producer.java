public class Producer extends Thread {
    private Buffer _buf;
    private final int M;

    public Producer(Buffer buf, int M)  {
        this._buf = buf;
        this.M = M;
    }

    public void run() {
        int a[] = new int[(int) (Math.random()*(M-1)+1)];
        for(int i=0; i<a.length; i++) {
            a[i] = (int)(Math.random() * 10) + 1;
        }

        while(true) {
            _buf.put(a);
            try {
                sleep((int) (Math.random() * 100));
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
