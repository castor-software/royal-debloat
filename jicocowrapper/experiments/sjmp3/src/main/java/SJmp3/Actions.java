package SJmp3;

import SJmp3.ClipBoard.ClipboardTextTransfer;
import SJmp3.LoadSave.LoadFileMp3;
import SJmp3.Threads.Play_Thread;
import SJmp3.Threads.Next_Thread;
import SJmp3.LoadSave.LoadPlayList;
//import SJmp3.LoadSave.LoadFileMp3;
import SJmp3.LoadSave.LoadFolderMp3;
import SJmp3.Threads.Slider_Thread;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.animation.AnimationFacet;

public class Actions {

    public static String date;
    public static String title;
    public static int channels;
    public static int bitrate;
    public static int freq;
    public static int sizeFile;
    public static int sizeFrame;
    public static int sizeSampleBit;
    public static int frames;
    public static long duration;
    public static String author;
    public static String album;
    public static Boolean vbr;
    public static Boolean crc;
    public static String encoding;
    public static String layer;
    public static String mpeg;
    public static String track;
    public static String genre;
    public static String affType;
    public static Play_Thread PL;
    public static Next_Thread NP;
    public static Slider_Thread ST;
    public static int LSTindex;
    public static int currentFrame = 0;
    public static String[] zero = {};
    public static boolean NextFlag = false;
    public static boolean PlayFlag = false;
    public static float fps;
    public static long SPT = 0;  // Song Play Time !!
    public static String currentMute;
    public static String currentLAF = "org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel";
    public static String currentM3u;
    public static String currentDir;
    public static int currentMixer;
    public static String currentMode;
    public static Random mygen = new Random(new Date().getTime());
    public static Vector<String> currentList3m = new Vector<String>();
    public static Vector<String> currentBuferList = new Vector<String>();
    public static Vector<String> list3m1 = new Vector<String>();
    public static Vector<String> BuferList1 = new Vector<String>();
    public static String[] M3uArray = new String[1];
    public static String[] DirArray = new String[1];
    public ImageIcon mp3icon = new javax.swing.ImageIcon(getClass().getResource("/SJmp3/img/file-info-48.png"));
    public static String currentTrack="";

    public void mp3info() {
        if (Actions.affType == null) {
            JOptionPane.showMessageDialog(SJmp3gui.frame, "Track not load !", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(SJmp3gui.frame, "Date=" + date
                + "\nTitle=" + title
                + "\nChannels=" + channels
                + "\nBitrate=" + bitrate / 1000
                + " kbps\nFrequency=" + freq / 1000
                + " kHz\nSize=" + sizeFile / 1000
                + " Kb\nFrames=" + frames
                + "\nFPS=" + fps
                + " frames/sek\nEncoding=" + encoding
                + "\nLayer=Layer " + layer
                + "\nMpeg=MPEG-" + mpeg
                + "\nFrame Size=" + sizeFrame
                + " bytes\nDuration=" + duration / 1000
                + " sek\nVariable Bitrate=" + vbr
                + "\nCRC=" + crc
                + "\nTrack=" + track
                + "\nGenre=" + genre
                + "\nAuthor=" + author
                + "\nAlbum=" + album,
                "Track Info",
                JOptionPane.INFORMATION_MESSAGE,
                mp3icon);
    }

    public static void ClosePlaySliderThreads() {
        PlayFlag = false;
        if (PL != null) {
            PL.player.close();
            ST.stop();
        }
        NextFlag = false;
        PL = null;
        ST = null;
    }

    public static void SelectFile() {
        int index = SJmp3gui.currentLST.getSelectedIndex();
        LoadFileMp3.Select();
        SJmp3gui.currentLST.setSelectedIndex(index);
    }

    public static void SelectFolder() {
        int index = SJmp3gui.currentLST.getSelectedIndex();
        LoadFolderMp3.Select();
        SJmp3gui.currentLST.setSelectedIndex(index);
    }

    public static void GoPlay() {
        if (PlayFlag == true) {
            return;
        }
        try {
            ClosePlaySliderThreads();
            if (currentTrack != "") {
                PL = new Play_Thread();
                ST = new Slider_Thread("slider");
                PlayFlag = true;
                System.out.println("PlayFlag=" + PlayFlag);
            } else {
                JOptionPane.showMessageDialog(SJmp3gui.frame, "FileNotFound !", "Error !", JOptionPane.ERROR_MESSAGE);
            }
            System.out.println("Paused Frame = " + currentFrame);
        } catch (NullPointerException e) {
        }
    }//metod

    public static void pause() {
        if (PlayFlag == false) {
            return;
        }
        PlayFlag = false;
        if (PL != null) {
            PL.player.stop();
            ST.stop();
        }
        NextFlag = false;
        System.out.println("Paused on = " + currentFrame);
    }

    public static void AddList() {
        int index = SJmp3gui.currentLST.getSelectedIndex();
        LoadPlayList.Select();
        SJmp3gui.currentLST.setSelectedIndex(index);
    }

    public static void stopp() {
        SPT = 0;
        ClosePlaySliderThreads();
        currentFrame = 0;
    }

    public static void PlayNext() {
        PlayFlag = false;
        NextFlag = false;
        if (currentTrack != "") {
            try {
                SPT = 0;
                currentFrame = 0;
                if (currentMode.equals("shuffle")) {
                    LSTindex = mygen.nextInt(currentList3m.size());
                } else {
                    if (currentMode.equals("repeat")) {
                        LSTindex = SJmp3gui.currentLST.getSelectedIndex();
                    } else {
                        LSTindex = SJmp3gui.currentLST.getSelectedIndex() + 1;
                    }
                }
                SJmp3gui.currentLST.setSelectedIndex(LSTindex);
                System.out.println("LSTindex = " + LSTindex);
                currentTrack = currentList3m.get(LSTindex);
                try {
                    NP.sleep(200);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Actions.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println(currentTrack);
                stopp();
                GoPlay();
            } catch (ArrayIndexOutOfBoundsException e) {
                if (!currentMode.equals("loop")) {
                    LSTindex = SJmp3gui.currentLST.getSelectedIndex() - 1;
                } else {
                    SJmp3gui.currentLST.setSelectedIndex(0);
                    Actions.currentFrame = 0;
                    Actions.LSTindex = SJmp3gui.currentLST.getSelectedIndex();
                    currentTrack = currentList3m.get(Actions.LSTindex);
                    System.out.println(currentTrack);
                }
                stopp();
                GoPlay();
            }//catch//catch
        } else {
            JOptionPane.showMessageDialog(SJmp3gui.frame, "FileNotFound !", "Error !", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void PlayPrevious() {
        PlayFlag = false;
        NextFlag = false;
        if (currentTrack != "") {
            SPT = 0;
            currentFrame = 0;
            LSTindex = SJmp3gui.currentLST.getSelectedIndex();
            if (LSTindex > 0) {
                LSTindex = SJmp3gui.currentLST.getSelectedIndex() - 1;
            }
            SJmp3gui.currentLST.setSelectedIndex(LSTindex);
            System.out.println(LSTindex);
            currentTrack = currentList3m.get(LSTindex);
            System.out.println(currentTrack);
            System.out.println("LSTindex = " + LSTindex);
            stopp();
            Actions.GoPlay();
        } else {
            JOptionPane.showMessageDialog(SJmp3gui.frame, "FileNotFound !", "Error !", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void FastForward() {
        if (PlayFlag == true) {
            pause();
        }
        if (SPT < 0.94 * duration) {
            SPT = Math.round(SPT + duration / 20);
            Actions.currentFrame = Math.round((Actions.SPT) * Actions.fps / 1000);
            SJmp3gui.SongSlider.setValue(Math.round(1000 * Actions.SPT / Actions.duration));
            GoPlay();
        } else {
            PlayNext();
        }
    }

    public static void FastBackward() {
        if (PlayFlag == true) {
            pause();
        }
        if (SPT > 0.05 * duration) {
            SPT = Math.round(SPT - duration / 20);
            Actions.currentFrame = Math.round((Actions.SPT) * Actions.fps / 1000);
            SJmp3gui.SongSlider.setValue(Math.round(1000 * Actions.SPT / Actions.duration));
            GoPlay();
        } else {
            PlayPrevious();
        }
    }

    public static String TimeShow(long TTT) {
        String min, sek;
        if (Math.round(TTT / 60000) < 10) {
            min = "0" + Math.round(TTT / 60000);
        } else {
            min = "" + Math.round(TTT / 60000);
        }
        if (Math.round((TTT % 60000) / 1000) < 10) {
            sek = "0" + Math.round((TTT % 60000) / 1000);
        } else {
            sek = "" + Math.round((TTT % 60000) / 1000);
        }
        return min + ":" + sek;
    }

    public static void RemoveAll(JFrame frame) {
        int r = JOptionPane.showConfirmDialog(frame, "Clear All ?", "Remove All", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            ClosePlaySliderThreads();
            SJmp3gui.currentLST.setListData(Actions.zero);
            Actions.currentFrame = 0;
            Actions.SPT = 0;
            Actions.currentList3m.removeAllElements();
            Actions.currentM3u = "";
            Actions.currentDir = "";
            Actions.M3uArray[0] = "";
            Actions.DirArray[0] = "";
        }
    }

    public static void RemoveSong(JFrame frame) {
        int r = JOptionPane.showConfirmDialog(frame, "Remove this Track ?", "Remove Track", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            try {
                int bufer = SJmp3gui.currentLST.getSelectedIndex();
                Actions.currentList3m.removeElementAt(bufer);
                Actions.List3mToBuferList();
                SJmp3gui.currentLST.setListData(Actions.currentBuferList);
            } catch (ArrayIndexOutOfBoundsException ee) {
                JOptionPane.showMessageDialog(SJmp3gui.frame, "FileNotFound !", "Error !", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void SongUp() {
        try {
            int index = SJmp3gui.currentLST.getSelectedIndex();
            String buf = Actions.currentList3m.elementAt(index);
            if (index - 1 >= 0) {
                Actions.currentList3m.removeElementAt(index);
                Actions.currentList3m.insertElementAt(buf, index - 1);
                Actions.List3mToBuferList();
                SJmp3gui.currentLST.setListData(Actions.currentBuferList);
                SJmp3gui.currentLST.setSelectedIndex(index - 1);
            } else {
                JOptionPane.showMessageDialog(SJmp3gui.frame, "Top List");
            }
        } catch (ArrayIndexOutOfBoundsException ee) {
            JOptionPane.showMessageDialog(SJmp3gui.frame, "File Not Select !", "Error !", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void SongDown() {
        try {
            int index = SJmp3gui.currentLST.getSelectedIndex();
            int max = Actions.currentList3m.size() - 1;
            String buf = Actions.currentList3m.elementAt(index);
            if (index + 1 <= max) {
                Actions.currentList3m.removeElementAt(index);
                Actions.currentList3m.insertElementAt(buf, index + 1);
                Actions.List3mToBuferList();
                SJmp3gui.currentLST.setListData(Actions.currentBuferList);
                SJmp3gui.currentLST.setSelectedIndex(index + 1);
            } else {
                JOptionPane.showMessageDialog(SJmp3gui.frame, "End List");
            }
        } catch (ArrayIndexOutOfBoundsException ee) {
            JOptionPane.showMessageDialog(SJmp3gui.frame, "File Not Select !", "Error !", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void SliderMouse() {
        try {
            if (Actions.PlayFlag == true) {
                Actions.pause();
            }
            Actions.SPT = Actions.duration * SJmp3gui.SongSlider.getValue() / 1000;
            Actions.ST.stop(); //PL=null;
            Actions.currentFrame = Math.round((Actions.SPT) * Actions.fps / 1000);
            SJmp3gui.TimeTFcur.setText(Actions.TimeShow(Actions.SPT));
        } catch (NullPointerException ee) {
        }
    }

    public static void List3mToBuferList() {
        Actions.currentBuferList.removeAllElements();
        for (int j = 0; j < Actions.currentList3m.size(); j++) {
            Actions.currentBuferList.add(j + 1 + ") " + Actions.currentList3m.get(j));
        }
        Actions.BuferList1.removeAllElements();
        for (int j = 0; j < Actions.list3m1.size(); j++) {
            Actions.BuferList1.add(j + 1 + ") " + Actions.list3m1.get(j));
        }
    }

    public static void LSTmouseClick() {
        try {
            Actions.SPT = 0;
            Actions.currentFrame = 0;
            Actions.ClosePlaySliderThreads();
            Actions.PlayFlag = false;
            Actions.NextFlag = false;
            if (Actions.ST != null) {
                Actions.ST.stop();
            }
            Actions.LSTindex = SJmp3gui.currentLST.getSelectedIndex();
            System.out.println(Actions.LSTindex);
            currentTrack = Actions.currentList3m.get(Actions.LSTindex);
            System.out.println(currentTrack);
            System.out.println("LSTindex = " + Actions.LSTindex);
        } catch (ArrayIndexOutOfBoundsException e) {
            JOptionPane.showMessageDialog(SJmp3gui.frame, "FileNotFound !", "Error !", JOptionPane.ERROR_MESSAGE);
        } catch (NullPointerException e) {
        }
    }

    public static void DefCfg() {
        Actions.currentLAF = "org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel";
        Actions.currentM3u = "";
        Actions.currentMixer = 100;
        Actions.currentMute = "false";
        Actions.M3uArray[0] = "";
        Actions.DirArray[0] = "";
        SJmp3gui.currentLST = SJmp3gui.LST1;
        Actions.currentList3m = Actions.list3m1;
        Actions.currentBuferList = Actions.BuferList1;
        Actions.currentMode = "classic";
    }

    public static void LoadList3m() {
        SJmp3gui.currentLST = SJmp3gui.LST1;
        Actions.currentList3m = Actions.list3m1;
        Actions.currentBuferList = Actions.BuferList1;
        Actions.currentM3u = Actions.M3uArray[0];
        Actions.currentDir = Actions.DirArray[0];
        String bufer = "", txt = "";
        try {
            if (!Actions.M3uArray[0].isEmpty()) {
                Actions.DirArray[0] = "";
                FileInputStream fis = new FileInputStream(Actions.M3uArray[0]);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                int j = 0;
                do {
                    bufer = "";
                    txt = "";
                    if (br.ready()) {
                        bufer = br.readLine();
                    }
                    if (txt.equals(bufer)) {
                        break;
                    }
                    if (!bufer.startsWith("#EXT")) {
                        Actions.list3m1.add(bufer);
                        j++;
                    }//if
                    txt = bufer;
                } while (true);
                br.close();
                fis.close();
                Actions.List3mToBuferList();
                SJmp3gui.LST1.setListData(Actions.BuferList1);
            } else {
                if (!Actions.DirArray[0].isEmpty()) {
                    try {
                        frolovDirList(Actions.DirArray[0]);
                    } catch (NullPointerException nn) {
                        Actions.DirArray[0] = "";
                    }
                }
            }
            SJmp3gui.currentLST.setListData(Actions.currentBuferList);
        } catch (IOException e) {
        }

    }

    public static void frolovDirList(String ss) {
        File f = new File(ss);
        String[] sDirList = f.list();
        int i;
        for (i = 0; i < sDirList.length; i++) {
            File f1 = new File(ss + File.separator + sDirList[i]);
            if (f1.isFile()) {
                System.out.println(ss + File.separator + sDirList[i]);
                if (extFileCheck(f1)) {
                    Actions.list3m1.add(ss + File.separator + sDirList[i]);
                }
            } else {
                frolovDirList(ss + File.separator + sDirList[i]);
            }
        }
        Actions.List3mToBuferList();
        SJmp3gui.LST1.setListData(Actions.BuferList1);
    }

    public static void CopyToClipBoard(String cps) {
        ClipboardTextTransfer textTransfer = new ClipboardTextTransfer();
        textTransfer.setClipboardContents(cps);
    }

    public static String PasteFromClipBoard() {
        ClipboardTextTransfer textTransfer = new ClipboardTextTransfer();
        return textTransfer.getClipboardContents();
    }

    public static void CopyPathURLtoClipboard() {
        try {
            Actions.CopyToClipBoard(Actions.currentList3m.get(SJmp3gui.currentLST.getSelectedIndex()));
        } catch (ArrayIndexOutOfBoundsException aa) {
        }
    }

    public static void PastePathURLfromClipboard() {
        String bufer = Actions.PasteFromClipBoard();
        if (extStringCheck(bufer)) {
            Actions.currentList3m.add(bufer);
            Actions.List3mToBuferList();
            SJmp3gui.currentLST.setListData(Actions.currentBuferList);
        } else {
            JOptionPane.showMessageDialog(SJmp3gui.frame, "Not Support File format ! = " + bufer, "Error !", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void MyInstLF(String lf) {
        //UIManager.installLookAndFeel(lf,lf);  
        SJmp3gui.lookAndFeelsDisplay.add(lf);
        SJmp3gui.lookAndFeelsRealNames.add(lf);
    }

    public static void InstallLF() {
        //MyInstLF("javax.swing.plaf.metal.MetalLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceSaharaLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceAutumnLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceCremeLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceCremeCoffeeLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceMagellanLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceMistAquaLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceMistSilverLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceOfficeBlue2007LookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceOfficeBlack2007LookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceOfficeSilver2007LookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceNebulaLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceNebulaBrickWallLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceGeminiLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceDustCoffeeLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceDustLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceRavenLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceGraphiteLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceGraphiteAquaLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceGraphiteGlassLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceBusinessBlackSteelLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceBusinessBlueSteelLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceBusinessLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceMarinerLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceCeruleanLookAndFeel");
        MyInstLF("org.pushingpixels.substance.api.skin.SubstanceTwilightLookAndFeel");
    }

    public static void NetStreamGuiOn() {
        SJmp3gui.TimeTFend.setText(Actions.TimeShow(Actions.duration));
    }

    public static Boolean extFileCheck(File f1) {
        String buf = f1.getName().toLowerCase();
        return buf.endsWith(".mp1") || buf.endsWith(".mp2") || buf.endsWith(".mp3") || buf.endsWith(".mpga");
    }

    public static Boolean extStringCheck(String bufer) {
        String buf = bufer.toLowerCase();
        return buf.endsWith(".mp1") || buf.endsWith(".mp2") || buf.endsWith(".mp3") || buf.endsWith(".mpga");
    }

    public static void WrongPathUrlException(String mes) {
        Actions.ST.stop();
        Actions.PL = null;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            //java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(SJmp3gui.frame, mes, "Error !", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void ZeroAudioInfo() {
        Actions.date = "";
        Actions.title = "";
        Actions.channels = 0;
        Actions.bitrate = 0;
        Actions.freq = 0;
        Actions.sizeFile = 0;
        Actions.sizeFrame = 0;
        Actions.frames = 0;
        Actions.author = "";
        Actions.encoding = "";
        Actions.layer = "";
        Actions.mpeg = "";
        Actions.track = "";
        Actions.genre = "";
        Actions.album = "";
        Actions.vbr = false;
        Actions.crc = false;
        Actions.duration = 0;
        Actions.fps = 0;
        Actions.sizeSampleBit = 0;
    }

    public static void enableEffects() {
        AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.ARM);
        AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.FOCUS);
        AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.FOCUS_LOOP_ANIMATION);
        AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.GHOSTING_BUTTON_PRESS);
        AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.GHOSTING_ICON_ROLLOVER);
        AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.ICON_GLOW);
        AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.PRESS);
        AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.ROLLOVER);
        AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.SELECTION);
        //AnimationConfigurationManager.getInstance().setTimelineDuration(500);        
    }

}
