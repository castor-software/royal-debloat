package SJmp3.Threads;
import SJmp3.Actions;
import SJmp3.SJmp3gui;
public class Slider_Thread implements Runnable {
    private String msg;
    private Thread go;
    public Slider_Thread (String s) 
        {
            msg = s;
            go = new Thread(this);
            go.start();
        }
    @Override
    public void run () {
        Thread th = Thread.currentThread();
        long d1=0,d2=0,delta=0;
        while(go == th) 
         {
            d1=System.currentTimeMillis();
            try {  Thread.sleep(100);  }
            catch(InterruptedException ie) { }
            Actions.SPT=Actions.SPT + delta; 
            try 
            {
             ///if (Actions.urlFlag==false) 
                SJmp3gui.SongSlider.setValue(Math.round(1000*Actions.SPT/Actions.duration));
            }
            catch (ArithmeticException ae) {System.out.println("DELETE BY ZERO !!!!!!!!!!");}
            SJmp3gui.TimeTFcur.setText(Actions.TimeShow(Actions.SPT));
            Actions.currentFrame = Math.round((Actions.SPT)*Actions.fps/1000); 
            d2=System.currentTimeMillis();
            delta=d2-d1;
          }
        }
    public void stop () { go = null; }
}
