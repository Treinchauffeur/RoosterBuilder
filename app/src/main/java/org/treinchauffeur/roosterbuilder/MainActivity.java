package org.treinchauffeur.roosterbuilder;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.sip.SipAudioCall;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.treinchauffeur.roosterbuilder.io.FileReader;
import org.treinchauffeur.roosterbuilder.misc.Logger;
import org.treinchauffeur.roosterbuilder.obj.Mentor;
import org.treinchauffeur.roosterbuilder.obj.Pupil;
import org.treinchauffeur.roosterbuilder.obj.StoredPupil;
import org.treinchauffeur.roosterbuilder.ui.DatabaseDialog;
import org.treinchauffeur.roosterbuilder.ui.MentorDialog;
import org.treinchauffeur.roosterbuilder.ui.PupilDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    public static final int PICK_FILE_REQUEST = 1312;
    private static final String TAG = "MainActivity";

    public Button selectButton, saveButton, resetButton;
    private MaterialTextView weekText, pupilsText, mentorsText;
    private ScrollView pupilScrollView, mentorScrollView;
    private FlexboxLayout pupilsLayout, mentorsLayout;
    public LinearLayout bottomButtonsLayout;

    private MaterialCardView mainCardView;

    private FileReader fileReader;

    public final HashMap<String, Pupil> pupilsMap = new HashMap<>(); //Name as key

    public static final HashMap<String, Mentor> mentorsMap = new HashMap<>(); //Id as key.

    public HashMap<String, StoredPupil> savedPupils = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileReader = new FileReader(this, MainActivity.this);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        Context context = this;
        toolbar.getMenu().getItem(0).setOnMenuItemClickListener(item -> {
            DatabaseDialog dialog = new DatabaseDialog(context, MainActivity.this);
            dialog.show();
            return false;
        });

        bottomButtonsLayout = findViewById(R.id.bottomButtons);

        weekText = findViewById(R.id.weekText);
        pupilsText = findViewById(R.id.pupilsText);
        mentorsText = findViewById(R.id.mentorsText);

        pupilsLayout = findViewById(R.id.pupilsLayout);
        mentorsLayout = findViewById(R.id.mentorsLayout);

        pupilScrollView = findViewById(R.id.pupilScrollView);
        mentorScrollView = findViewById(R.id.mentorScrollView);

        saveButton = findViewById(R.id.saveButton);
        mainCardView = findViewById(R.id.mainCard);
        selectButton = findViewById(R.id.selectButton);
        resetButton = findViewById(R.id.resetButton);

        selectButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/*");
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });

        selectButton.setOnLongClickListener(v -> {
            reset();
            return false;
        });

        resetButton.setOnClickListener(v -> reset());

        Intent intent = getIntent();
        if (Objects.equals(intent.getAction(), Intent.ACTION_VIEW)) {
            Uri fileUri = intent.getData();
            fileReceived(fileUri);
        }

        loadData();
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
            Uri fileUri = intent.getData();
            fileReceived(fileUri);
        }
    }

    @SuppressLint("SetTextI18n")
    private void fileReceived(Uri uri) {
        Logger.debug(TAG, "File retrieved, loading.. ");
        fileReader.startReading(uri);
        syncSavedPupils();
        displayData();
    }

    @SuppressLint("SetTextI18n")
    public void displayData() {
        if(pupilsMap.size() > 0) {
            mainCardView.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            bottomButtonsLayout.setVisibility(View.VISIBLE);
            selectButton.setVisibility(View.GONE);

            if(fileReader.weekNumber != -1) {
                weekText.setText("Week " + fileReader.weekNumber + " van jaar " + fileReader.yearNumber);
                pupilsText.setText("Aspiranten (" + pupilsMap.size() + "):");
                mentorsText.setText("Mentoren (" + mentorsMap.size() + "):");
            }

            pupilsLayout.removeAllViews();
            TreeMap<String, Pupil> sortedPupilMap = new TreeMap<>(pupilsMap);

            for (Map.Entry<String, Pupil> set : sortedPupilMap.entrySet()) {
                Pupil pupil = set.getValue();

                Chip newChip = new Chip(this);
                newChip.setText(pupil.getNeatName());
                newChip.setChipStartPadding(0);
                newChip.setChipEndPadding(0);

                if(pupil.hasWeekOff())
                    newChip.setEnabled(false);

                newChip.setOnClickListener(v -> {
                    PupilDialog dialog = new PupilDialog(this, MainActivity.this, pupil);
                    dialog.show();
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(5, 0, 5, 0);
                newChip.setLayoutParams(params);

                if(savedPupils.containsKey(pupil.getName())) {
                    StoredPupil saved = savedPupils.get(pupil.getName());
                    if(saved.getPhone().equals("")) newChip.setError("");
                    if(saved.getEmail().equals("")) newChip.setError("");
                } else newChip.setError("");

                pupilsLayout.addView(newChip);
            }

            mentorsLayout.removeAllViews();
            TreeMap<String, Mentor> sortedMentorMap = new TreeMap<>(mentorsMap);

            for (Map.Entry<String, Mentor> set : sortedMentorMap.entrySet()) {
                Mentor mentor = set.getValue();

                Chip newChip = new Chip(this);
                if(mentor.getNeatName().equals(""))
                    newChip.setText(mentor.getName());
                else newChip.setText(mentor.getNeatName());

                newChip.setChipStartPadding(0);
                newChip.setChipEndPadding(0);

                newChip.setOnClickListener(v -> {
                    MentorDialog dialog = new MentorDialog(this, MainActivity.this, mentor);
                    Logger.debug(TAG, mentor.getId() + ": " + mentor.getName() + " - " + mentor.getEmail());
                    dialog.show();
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(5, 0, 5, 0);
                newChip.setLayoutParams(params);

                if(mentor.getPhoneNumber().equals("")) newChip.setError("");
                if(mentor.getEmail().equals("")) newChip.setError("");

                mentorsLayout.addView(newChip);
            }
        } else {
            Toast.makeText(this, "Er is een fout opgetreden! 0900-BEL-POLSKI", Toast.LENGTH_SHORT).show();
        }
    }

    public void syncSavedPupils() {
        SharedPreferences sharedPreferences = getSharedPreferences("RoosterBot", MODE_PRIVATE);

        loadData();

        String mentorValue = new Gson().toJson(new HashMap<String, Mentor>());
        String mentorJson = sharedPreferences.getString("SavedMentorMap",mentorValue);
        TypeToken<HashMap<String, Mentor>> mentorToken = new TypeToken<HashMap<String, Mentor>>() {};
        HashMap<String, Mentor> tempMap = new Gson().fromJson(mentorJson,mentorToken.getType());

        for (Map.Entry<String, Pupil> set : pupilsMap.entrySet()) {
            Pupil pupil = set.getValue();

            if(!savedPupils.containsKey(pupil.getName())) {
                StoredPupil toSave = new StoredPupil(pupil.getName(), "", "");
                savedPupils.put(toSave.getSavedName(), toSave);
            } else {
                pupil.setNeatName(Objects.requireNonNull(savedPupils.get(pupil.getName())).getNeatName());
                pupil.setEmail(Objects.requireNonNull(savedPupils.get(pupil.getName())).getEmail());
                pupil.setPhoneNumber(Objects.requireNonNull(savedPupils.get(pupil.getName())).getPhone());
            }
        }

        for (Map.Entry<String, Mentor> set : mentorsMap.entrySet()) {
            Mentor mentor = set.getValue();

            if(tempMap.containsKey(mentor.getId())) {
                    mentor.setNeatName(Objects.requireNonNull(tempMap.get(mentor.getId())).getNeatName());
                    mentor.setEmail(Objects.requireNonNull(tempMap.get(mentor.getId())).getEmail());
                    mentor.setPhoneNumber(Objects.requireNonNull(tempMap.get(mentor.getId())).getPhoneNumber());
            }
        }
    }

    /**
     *
     */
    public void saveData() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("RoosterBot", MODE_PRIVATE);
        String jsonString = new Gson().toJson(savedPupils);
        Logger.debug(TAG, "Saving pupils: " + jsonString);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SavedPupilsMap", jsonString);

        String mentorString = new Gson().toJson(mentorsMap);
        Logger.debug(TAG, "Saving mentors: " + mentorString);
        editor.putString("SavedMentorMap", mentorString);
        editor.apply();
    }

    /**
     * Resets the app to startup conditions.
     */
    public void reset() {
        pupilsMap.clear();
        savedPupils.clear();
        mentorsMap.clear();
        pupilsLayout.removeAllViews();
        mentorsLayout.removeAllViews();
        mainCardView.setVisibility(View.GONE);
        bottomButtonsLayout.setVisibility(View.GONE);
        selectButton.setVisibility(View.VISIBLE);

        fileReader.reset();

        loadData();
    }

    /**
     * Loads only the saved pupils for use of comparing the list of new pupil name lines.
     */
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("RoosterBot", MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, StoredPupil>());
        String json=sharedPreferences.getString("SavedPupilsMap",defValue);
        TypeToken<HashMap<String,StoredPupil>> token = new TypeToken<HashMap<String,StoredPupil>>() {};
        savedPupils = new Gson().fromJson(json,token.getType());

        String mentorValue = new Gson().toJson(new HashMap<String, Mentor>());
        String mentorJson = sharedPreferences.getString("SavedMentorMap",mentorValue);
        TypeToken<HashMap<String, Mentor>> mentorToken = new TypeToken<HashMap<String, Mentor>>() {};
        mentorsMap.putAll(new Gson().fromJson(mentorJson,mentorToken.getType()));
    }
}