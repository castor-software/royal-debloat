package SJmp3.LoadSave;
import SJmp3.Actions;
import SJmp3.Filters.MFileFilter;
import SJmp3.SJmp3gui;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
public class SavePlayList {
    public static String putf="",putd="";
    public static FileInputStream fis;
    public static BufferedReader br;
    public static String bufer="",txt="";
    public static String fullFileName;
    public static FileWriter writeFile = null;
    public static void Save () {  
        JFileChooser fc=new JFileChooser();
        fc.addChoosableFileFilter(new MFileFilter(".m3u"));
        //fc.addChoosableFileFilter(new MFileFilter(".M3U"));        
        fc.setAcceptAllFileFilterUsed(false);
        fc.setDialogType(JFileChooser.SAVE_DIALOG);
        if (fc.showDialog(SJmp3gui.frame,"Save PlayList") == JFileChooser.APPROVE_OPTION)
           {
           try 
           {
                fullFileName=fc.getSelectedFile().getPath();
                File outputfile = new File(fullFileName + ".m3u");
                int jmax;
                jmax=Actions.currentList3m.size();
                System.out.println(jmax);
                writeFile = new FileWriter(outputfile);
                for (int j=0; j<jmax; j++)
                {
                  LoadFileMp3.putf=Actions.currentList3m.get(j);
                  System.out.println(LoadFileMp3.putf);            
                  writeFile.append(LoadFileMp3.putf+"\n");
                }
            } 
            catch (IOException ex) 
            {
                Logger.getLogger(SavePlayList.class.getName()).log(Level.SEVERE, null, ex);
            }
            finally 
            {
                if(writeFile != null) 
                {
                 try { writeFile.close(); } 
                 catch (IOException e) { e.printStackTrace(); }
                }
            }            
           }
        
    }  
    
}
