package org.treinchauffeur.roosterbuilder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.treinchauffeur.roosterbuilder.io.FileReader;
import org.treinchauffeur.roosterbuilder.io.PdfFactory;
import org.treinchauffeur.roosterbuilder.misc.Logger;
import org.treinchauffeur.roosterbuilder.obj.Mentor;
import org.treinchauffeur.roosterbuilder.obj.Pupil;
import org.treinchauffeur.roosterbuilder.obj.StoredPupil;
import org.treinchauffeur.roosterbuilder.ui.DatabaseDialog;
import org.treinchauffeur.roosterbuilder.ui.DeleteDialog;
import org.treinchauffeur.roosterbuilder.ui.InfoDialog;
import org.treinchauffeur.roosterbuilder.ui.MentorDialog;
import org.treinchauffeur.roosterbuilder.ui.PupilDialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    public static final int PICK_FILE_REQUEST = 1312;
    private static final String TAG = "MainActivity";
    public int weekNumber = -1, yearNumber = -1;
    public Button selectButton, saveButton, resetButton;
    private MaterialTextView weekText, pupilsText, mentorsText;
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

        saveButton = findViewById(R.id.saveButton);
        mainCardView = findViewById(R.id.mainCard);
        selectButton = findViewById(R.id.selectButton);
        resetButton = findViewById(R.id.resetButton);

        selectButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/*");

            //noinspection deprecation
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

        if(pupilsMap.size() == 0 && mentorsMap.size() == 0) {
            DatabaseDialog dialog = new DatabaseDialog(context, MainActivity.this);
            dialog.show();
            Toast.makeText(context, "Er zijn nog geen opgeslagen aspiranten en of mentoren gevonden!", Toast.LENGTH_SHORT).show();
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

            boolean unknownPupil = false, unknownMentor = false;

            if(weekNumber != -1) {
                weekText.setText("Week " + weekNumber + " van jaar " + yearNumber);
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
                newChip.setCheckable(true);
                newChip.setChecked(true);

                if(pupil.hasWeekOff()) {
                    newChip.setChecked(false);
                    pupil.shouldDisplay(false);
                }

                newChip.setOnClickListener(v -> {
                    PupilDialog dialog = new PupilDialog(this, MainActivity.this, pupil);
                    dialog.show();
                    newChip.setChecked(pupil.getShouldDisplay());
                });

                newChip.setOnLongClickListener( v -> {
                    if(newChip.isChecked()) {
                        newChip.setChecked(false);
                        pupil.shouldDisplay(false);
                    } else {
                        newChip.setChecked(true);
                        pupil.shouldDisplay(true);
                    }
                    return true;
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(5, 0, 5, 0);
                newChip.setLayoutParams(params);

                if(savedPupils.containsKey(pupil.getName())) {
                    StoredPupil saved = savedPupils.get(pupil.getName());
                    if(Objects.requireNonNull(saved).getPhone().equals("")) {
                        newChip.setError("");
                        unknownPupil = true;
                    }
                    if(saved.getEmail().equals("")) {
                        newChip.setError("");
                        unknownPupil = true;
                    }
                } else {
                    newChip.setError("");
                    unknownPupil = true;
                }

                pupilsLayout.addView(newChip);
            }

            mentorsLayout.removeAllViews();
            TreeMap<String, Mentor> sortedMentorMap = new TreeMap<>();

            //Sorting the mentors, since sorting them by id is a but useless..
            for (Map.Entry<String, Mentor> set : mentorsMap.entrySet()) {
                Mentor mentor = set.getValue();
                sortedMentorMap.put(mentor.getName(), mentor);
            }

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

                newChip.setOnLongClickListener(v -> {
                    DeleteDialog deleteDialog = new DeleteDialog(this) {
                        @Override
                        public void onDeletePressed() {
                            mentorsMap.remove(mentor.getId());
                            saveData();
                            displayData();
                            dismiss();
                        }
                    };

                    deleteDialog.setMainText("Wil je '" + mentor.getNeatName() + "' uit de lijst van opgeslagen mentoren wilt verwijderen?");
                    deleteDialog.show();
                    return false;
                });

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                params.setMargins(5, 0, 5, 0);
                newChip.setLayoutParams(params);

                if(mentor.getPhoneNumber().equals("")) {
                    newChip.setError("");
                    unknownMentor = true;
                }
                if(mentor.getEmail().equals("")) {
                    newChip.setError("");
                    unknownMentor = true;
                }

                mentorsLayout.addView(newChip);
            }

            if(unknownPupil || unknownMentor) {
                InfoDialog dialog = new InfoDialog(this);
                if(unknownPupil && unknownMentor)
                    dialog.setMainText("Er zijn in dit bestand zowel (nieuwe) aspiranten als mentoren" +
                            " gevonden waarvan geen gegevens bekend zijn of deze nog onvolledig zijn! \n\n" +
                            "Deze worden aangegeven middels een uitroepteken naast hun namen.");
                else if (unknownPupil)
                    dialog.setMainText("Er zijn in dit bestand (nieuwe) aspiranten gevonden waarvan " +
                            "geen gegevens bekend zijn of deze nog onvolledig zijn! \n\n" +
                            "Deze worden aangegeven middels een uitroepteken naast hun naam.");
                else
                    dialog.setMainText("Er zijn in dit bestand (nieuwe) mentoren gevonden waarvan " +
                            "geen gegevens bekend zijn of deze nog onvolledig zijn! \n\n" +
                            "Deze worden aangegeven middels een uitroepteken naast hun naam.");

                dialog.show();

                //We're doing all this here, because this way we're sure all the variables will have been set correctly.
                saveButton.setEnabled(sortedMentorMap.size() > 0 && sortedPupilMap.size() > 0);

                saveButton.setOnClickListener(v -> {
                    PdfFactory pdfFactory = new PdfFactory(this, MainActivity.this, sortedPupilMap, sortedMentorMap);
                    pdfFactory.write();
                });
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