package SJmp3.LoadSave;

import SJmp3.Actions;
import SJmp3.Filters.AudioFileFilter;
import SJmp3.SJmp3gui;
import java.io.File;
import javax.swing.JFileChooser;

public class LoadFileMp3 {

    public static String putf = "", putd = "";

    public static void Select() {
        JFileChooser myf = new JFileChooser();
        myf.addChoosableFileFilter(new AudioFileFilter());
        myf.setAcceptAllFileFilterUsed(false);
        switch (myf.showDialog(SJmp3gui.frame, "Open File")) {
            case JFileChooser.APPROVE_OPTION:
                Actions.currentFrame = 0;
                putf = myf.getSelectedFile().getPath();
                putd = myf.getSelectedFile() + "";
                System.out.println(putf);
                System.out.println(putd);
                File d = myf.getCurrentDirectory();
                LoadFolderMp3.putf = d.getAbsolutePath();
                LoadFolderMp3.putd = d.getAbsolutePath();
                Actions.currentList3m.add(putf);
                //SJmp3gui.LST.setListData(Actions.zero);
                Actions.List3mToBuferList();
                SJmp3gui.currentLST.setListData(Actions.currentBuferList);
                Actions.currentTrack=putf;
                break;
            case JFileChooser.CANCEL_OPTION:
                break;
        }
    }// select
}
