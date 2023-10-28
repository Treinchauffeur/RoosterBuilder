package org.treinchauffeur.roosterbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import org.treinchauffeur.roosterbuilder.io.FileReader;
import org.treinchauffeur.roosterbuilder.misc.Logger;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    public static final int PICK_FILE_REQUEST = 1312;
    private static final String TAG = "MainActivity";

    public Button selectButton, saveButton;
    public TextView dataTextView;

    FileReader fileReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileReader = new FileReader(this, MainActivity.this);

        selectButton = findViewById(R.id.buttonSelectFile);
        dataTextView = findViewById(R.id.textDataView);
        selectButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/*");
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });

        Intent intent = getIntent();
        if (Objects.equals(intent.getAction(), Intent.ACTION_VIEW)) {
            Uri fileUri = intent.getData();
            Logger.debug(TAG, "File retrieved, loading.. " + intent);
            fileReader.startReading(fileUri);
        }

    }

    /**
     * Handling the incoming file after the user selected it.
     *
     * @param requestCode the code used to recognise the request
     * @param resultCode  success or not
     * @param intent      received intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && intent != null) {
            Logger.debug(TAG, "File retrieved, loading.. " + intent);
            Uri fileUri = intent.getData();
            fileReader.startReading(fileUri);
        }
    }
}