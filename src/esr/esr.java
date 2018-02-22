
package esr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;


public class esr {
    private final File file;
    private String originalReport;
    private String reportName;
    public esr(String report) {
        this.originalReport = report;
        String[] nm = this.originalReport.split(".");
        reportName = nm[0];
        file = new File( "reportSettings/" + reportName + ".txt" );
        createSettingsFile();
        
    }
    /************************
     * replace data in word file
     ***********************/
    private void replaceData(ResultSet rs){
        try {
            TreeMap tm = new TreeMap();
            tm = getSettings();
            XWPFDocument document = new XWPFDocument(OPCPackage.open(originalReport));
            for ( XWPFParagraph p : document.getParagraphs() ) {
                List<XWPFRun> runs = p.getRuns();
                if ( runs != null ) {
                    for ( XWPFRun r : runs ) {
                        String text = r.getText(0);
                        while ( rs.next() ) {
                            Iterator it = tm.keySet().iterator();
                            while ( it.hasNext() ) {
                                String key = (String) it.next();
                                String value = (String) tm.get(key);
                                if ( text !=null && text.contains(key) ) {
                                    text = text.replace(key, String.valueOf(rs.getObject(value)));
                                    r.setText(text,0);
                                }
                            }
                        }
                    }
                }
            }
            FileOutputStream out = new FileOutputStream ( new File (reportName + "-show.docx") );
            document.write( out );
        } catch (IOException | InvalidFormatException | SQLException ex) {
            System.out.println(ex);
        }
    }
    
    /*************************
     * end replace data
     ************************/
    
    
    
/*********************
 * settings control
 *********************/
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
    // replace settings with the treemap values
    private void replaceSettings(TreeMap tr){
        FileWriter fw = null;
        PrintWriter pw = null;
        try {
            fw = new FileWriter(file);
            pw = new PrintWriter(fw);
            Iterator iterator = tr.keySet().iterator();
            while ( iterator.hasNext() ) {
                String thek = (String) iterator.next();
                String dt = thek  + "=>" +  (String) tr.get( thek );
                pw.println(dt);
            }
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
    private TreeMap getSettings() {
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
        
        TreeMap tree = getSettings();
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
    // delete key from settings
    public boolean delKey( String key ){
        boolean result = false;
        try {
            TreeMap tree = getSettings();
            tree.remove( key );
            replaceSettings( tree );
            result = true;
        }catch ( Exception ex ){
            
        }
        return result;
    }
    // update key from settings
        public boolean edtKey( String oldKey , String key , String val){
        boolean result = false;
        try {
            TreeMap tree = getSettings();
            if ( checkSettings( oldKey + "=>" + val) ) {
                tree.remove( oldKey );
                tree.put(key, val);
                replaceSettings( tree );
                result = true;
            }else {
                JOptionPane.showMessageDialog(null, "not found");
            }
            
            
            
        }catch ( Exception ex ){
            
        }
        return result;
    }
        /****************************
       * end settings control
       **************************/
}
