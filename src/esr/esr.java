
package esr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;
import javax.swing.JOptionPane;


public class esr {
    private File file = new File("settings.txt");

    public esr() {
        createSettingsFile();
        addSetting("!key2" , "value2");
    }

    // create settings file if not exists
    private void createSettingsFile() {
        if ( !file.exists() ) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    // add data to the settings file
    private void addSetting( String setKey , String setValue ){
        if ( checkSettings( setKey + "=>" + setValue ) == true ) {
                JOptionPane.showMessageDialog(null, "key or value exists before !");
                return;
            } 
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
              
            fw = new FileWriter(file,true);
            pw = new PrintWriter(fw);
            pw.println(setKey + "=>" + setValue);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                fw.close();
                pw.close();
            } catch (IOException ex) {
            
            }
        }
    }
    
    // get data from the settings file
    private TreeMap getData() {
        TreeMap tree = new TreeMap();
        
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String theline = "";
            while ( (theline = br.readLine()) != null ){
                String[] dt = theline.split("=>");
                tree.put(dt[0], dt[1]);
            }
            br.close();
            fr.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        
        return tree;
    }
    // check if data in settings file
    private boolean checkSettings(String v){
        boolean result = false;
        
        TreeMap tree = getData();
        if ( tree.isEmpty() ) {
            result = false;
        }else {
            String[] splitV = v.split("=>");
            if ( tree.containsKey( splitV[0] ) || tree.containsValue( splitV[1] ) ) {
                result = true;
            }else {
                result = false;
            }
        }
        return result;
    }
}
