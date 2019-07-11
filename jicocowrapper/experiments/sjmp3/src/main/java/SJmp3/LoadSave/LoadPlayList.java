package SJmp3.LoadSave;

import SJmp3.Actions;
import SJmp3.Filters.MFileFilter;
import SJmp3.SJmp3gui;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class LoadPlayList {

    public static String putf = "", putd = "";
    public static String bufer = "", txt = "";

    public static void Select() {
        JFileChooser myf = new JFileChooser();
        myf.addChoosableFileFilter(new MFileFilter(".m3u"));
        myf.setAcceptAllFileFilterUsed(false);
        switch (myf.showDialog(SJmp3gui.frame, "Load PlayList")) {
            case JFileChooser.APPROVE_OPTION:
                Actions.currentFrame = 0;
                putf = myf.getSelectedFile().getPath();
                putd = myf.getSelectedFile() + "";
                System.out.println(putf);
                Actions.M3uArray[0] = putf;
                Actions.currentM3u = putf;
                Actions.currentDir = "";
                Actions.DirArray[0] = "";
                try (FileInputStream fis = new FileInputStream(putf);
                        BufferedReader br = new BufferedReader(new InputStreamReader(fis));) {
                    int j = 0;
                    do {
                        if (br.ready()) {
                            bufer = br.readLine();
                        }
                        if (txt.equals(bufer)) {
                            break;
                        }
                        if (!bufer.startsWith("#EXT")) {
                            Actions.currentList3m.add(bufer);
                            System.out.println(Actions.currentList3m.elementAt(j));
                            j++;
                        }//if
                        txt = bufer;
                    } while (true);
                } catch (IOException ex) {
                    Logger.getLogger(LoadPlayList.class.getName()).log(Level.SEVERE, null, ex);
                } //catch
                Actions.List3mToBuferList();
                SJmp3gui.currentLST.setListData(Actions.currentBuferList);
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
        }//switch
    }// select
    
}
