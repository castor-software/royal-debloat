package SJmp3.Threads;

import SJmp3.Actions;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Next_Thread extends Thread {

    public Next_Thread() {
        start();
    }
    private static Logger julLog = Logger.getLogger(Next_Thread.class.getName());

    @Override
    public void run() {
        do {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Next_Thread.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (Actions.SPT > Actions.duration) {
                Actions.PlayNext();
                julLog.info("@@@@@@@@@@@@@@@@@@@@@@@ Next_Thread JUL - next song @@@@@@@@@@@@@@@@@@@@@@@@@");
            }
        } while (true);
    }
}
