package org.treinchauffeur.roosterbuilder.io;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.treinchauffeur.roosterbuilder.misc.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class FileReader {

    private static ArrayList<String> fileContents = new ArrayList<>();
    private static final String TAG = "FileReader";
    public static final int REASON_FAILED_READ = 1, REASON_FAILED_PROCESS = 2;
    public static Uri toRead;
    @SuppressLint("SimpleDateFormat")
    static SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    public Context context;

    public FileReader(Context context) {
        this.context = context;
    }

    public boolean startReading() {
        boolean success = false;
        int lines = 0;
        fileContents.clear();
        Logger.debug(TAG, "Started reading: "+toRead);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(toRead);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


            while (reader.readLine() != null) lines++;
            reader.close();
            assert inputStream != null;
            inputStream.close();
            Logger.debug(TAG, "File has amount of lines: " + lines);

            //Reopen those streams to actually assign the filecontent lines.
            inputStream = context.getContentResolver().openInputStream(toRead);

            reader = new BufferedReader(new InputStreamReader(inputStream));

            for (int i = 0; i < lines; i++) {
                fileContents.add(reader.readLine());
            }

            if(fileContents.size() > 9) {
                success = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "startReading: ", e);
            return false;
        }
        return success;
    }

    public boolean processData() {
        boolean success = false;
        
        return success;
    }

}
