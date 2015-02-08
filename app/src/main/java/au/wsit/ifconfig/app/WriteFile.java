package au.wsit.ifconfig.app;
import android.app.Activity;
import android.os.Environment;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;


/**
 * Created by guyb on 10/05/2014.
 */
public class WriteFile extends Activity {

    FileOutputStream outputStream;
    FileInputStream inputStream;

    public boolean writeData(String filedata, String filename) {
        try {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/ifconfig");
            if (dir.exists() == false) {
                dir.mkdirs();
            }

            File file = new File(dir, filename);
            FileOutputStream FOS = new FileOutputStream(file);
            byte[] filebytes = filedata.getBytes();
            FOS.write(filebytes);
            FOS.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    return true;
    }




   public String readData(String filename)
    {
       File root = Environment.getExternalStorageDirectory();
       //File dir = new File(root.getAbsolutePath() + "/ifconfig/");
        String ret = "";
        try {
            FileInputStream IS = new FileInputStream(root.toString()+ "/ifconfig/" + filename);


                InputStreamReader ISR = new InputStreamReader(IS);
                BufferedReader BR = new BufferedReader(ISR);
                String receivedString = "";
                StringBuilder SB = new StringBuilder();

                while ((receivedString = BR.readLine()) != null) {
                    SB.append(receivedString);
                }
                IS.close();
                ret = SB.toString();


        }
         catch(FileNotFoundException e)
         {
            e.printStackTrace();

         }
        catch (IOException e)
        {
            e.printStackTrace();
        }




    return ret;
    }

    // Check if the filename password exists - returns true or false
    public static boolean checkFileExists(String filename)
    {
        String ROOT_DIR;
        File root = Environment.getExternalStorageDirectory();
        ROOT_DIR = root.toString()+ "/ifconfig/";

        File dir = new File(ROOT_DIR + filename);

        return dir.exists();

    }

}








