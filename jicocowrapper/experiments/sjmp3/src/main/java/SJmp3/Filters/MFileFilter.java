package SJmp3.Filters;

import java.io.*;

public class MFileFilter extends javax.swing.filechooser.FileFilter {
    
    String ext;
    
    public MFileFilter(String txt){
        this.ext=txt;
    }
    
    @Override
    public boolean accept(File f){
        if(f==null)
          return false;
        if(f.isDirectory()){
          return true ;
        }
        else
          return (f.getName().endsWith(ext)||f.getName().endsWith(ext.toUpperCase()));
      }
    
    @Override
    public String getDescription(){
          return "Files:  *"+ext;
    }
    
}
