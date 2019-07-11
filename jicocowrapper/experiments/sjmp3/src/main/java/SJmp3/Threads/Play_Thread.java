package SJmp3.Threads;

import SJmp3.Actions;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class Play_Thread extends Thread {

    public AdvancedPlayer player;
    public AudioFileFormat aff = null;
    public File fmp3;

    public Play_Thread() {
        start();
    }

    public void getId3tagFile() {
        Actions.date = (String) aff.properties().get("date");
        Actions.title = (String) aff.properties().get("title");
        Actions.channels = (Integer) aff.properties().get("mp3.channels");
        Actions.bitrate = (Integer) aff.properties().get("mp3.bitrate.nominal.bps");
        Actions.freq = (Integer) aff.properties().get("mp3.frequency.hz");
        Actions.sizeFile = (Integer) aff.properties().get("mp3.length.bytes");
        Actions.sizeFrame = (Integer) aff.properties().get("mp3.framesize.bytes");
        Actions.frames = (Integer) aff.properties().get("mp3.length.frames");
        Actions.author = (String) aff.properties().get("author");
        Actions.encoding = (String) aff.properties().get("mp3.version.encoding");
        Actions.layer = (String) aff.properties().get("mp3.version.layer");
        Actions.mpeg = (String) aff.properties().get("mp3.version.mpeg");
        Actions.track = (String) aff.properties().get("mp3.id3tag.track");
        Actions.genre = (String) aff.properties().get("mp3.id3tag.genre");
        Actions.album = (String) aff.properties().get("album");
        Actions.vbr = (Boolean) aff.properties().get("mp3.vbr");
        Actions.crc = (Boolean) aff.properties().get("mp3.crc");
        Actions.duration = (Long) aff.properties().get("duration");
        Actions.fps = (Float) aff.properties().get("mp3.framerate.fps");
        Actions.duration = Actions.duration / 1000;
    }

    public void playMp123(BufferedInputStream bis) {
        try {
            player = new AdvancedPlayer(bis);
        } catch (JavaLayerException ex) {
            Logger.getLogger(Play_Thread.class.getName()).log(Level.SEVERE, null, ex);
        }
        player.setPlayBackListener(new PlaybackListener() {
            @Override
            public void playbackFinished(PlaybackEvent event) {
                Actions.currentFrame = Math.round((Actions.SPT) * Actions.fps / 1000);
                //event.getFrame();
            }
        });
        Actions.NextFlag = false;
        try {
            player.play(Actions.currentFrame, Actions.frames);
        } catch (JavaLayerException ex) {
            Logger.getLogger(Play_Thread.class.getName()).log(Level.SEVERE, null, ex);
            Actions.WrongPathUrlException("Unsupported Audio !\nJavaLayerException");
        }
        player.close();
        Actions.NextFlag = true;
    }

    @Override
    public void run() {
        System.out.println("Play = GO !!!");
        String bufpu = Actions.currentTrack.toLowerCase();
        try (
                FileInputStream fis = new FileInputStream(Actions.currentTrack);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ) 
        {
            fmp3 = new File(Actions.currentTrack);
            aff = AudioSystem.getAudioFileFormat(fmp3);
            Actions.affType = aff.getType().toString().toLowerCase();
            //af = aff.getFormat();
            System.out.println("AFF = " + aff.getFormat());
            System.out.println("AF type = " + Actions.affType);
            System.out.println("AF prop = " + aff.properties());
            if (Actions.affType.contains("mp3")) {
                getId3tagFile();
                Actions.NetStreamGuiOn();
                playMp123(bis);
            }
            if ((Actions.NextFlag == true) & (Actions.PlayFlag == true)) {
                Actions.PlayNext();
                System.out.println("Play_Thread = Play Next !!!!!!!!!!!!");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Play_Thread.class.getName()).log(Level.SEVERE, null, ex);
            Actions.WrongPathUrlException("File Not Found !\nWrong Path !\nFileNotFoundException");
        } catch (IOException ex) {
            Logger.getLogger(Play_Thread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(Play_Thread.class.getName()).log(Level.SEVERE, null, ex);
            Actions.WrongPathUrlException("Unsupported Audio !\nUnsupportedAudioFileException");
        } catch (NullPointerException nn) {    }
        System.out.println("Play = STOP");
    } // run
    
}// class
