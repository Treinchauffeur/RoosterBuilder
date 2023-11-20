package org.treinchauffeur.roosterbuilder.io;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.misc.Logger;
import org.treinchauffeur.roosterbuilder.obj.Mentor;
import org.treinchauffeur.roosterbuilder.obj.Pupil;
import org.treinchauffeur.roosterbuilder.obj.Shift;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class PdfFactory {

    public static final String TAG = "PdfFactory";

    private Context context;
    private MainActivity activity;
    private AssetManager assets;
    private final String path, fileName; //Initialized in constructor; environment-specific values.
    private TreeMap<String, Pupil> pupilsMap;
    private TreeMap<String, Mentor> mentorsMap;

    private PdfWriter pdfWriter;

    public PdfFactory(Context context, MainActivity activity, TreeMap<String, Pupil> pupils, TreeMap<String, Mentor> mentors) {
        this.context = context;
        this.activity = activity;
        this.pupilsMap = pupils;
        this.mentorsMap = mentors;
        path = Objects.requireNonNull(context.getExternalFilesDir(null)).getPath() + File.separator;
        fileName = "Weekrooster_aspiranten_" + activity.weekNumber + "_" + activity.yearNumber + ".pdf";
        this.assets = activity.getAssets();
        cleanDirectory();
    }

    private void cleanDirectory() {
        File file = new File(path+fileName);
        if(file.exists()) file.delete();
    }

    public void write() {
        Document document = new Document(PageSize.A4);
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 7);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 6);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);

        try {
            pdfWriter = PdfWriter.getInstance(document,
                    Files.newOutputStream(Paths.get(path + fileName)));

            document.open();

            float width = document.getPageSize().getWidth();
            float height = document.getPageSize().getHeight();
            // step 3
            document.open();

            // step 4
            float[] columnDefinitionSize = { 25, 9.51F, 9.51F, 9.51F, 9.51F, 9.51F, 9.51F, 9.51F };

            float pos = height / 2;
            PdfPTable table = null;
            PdfPCell cell = null;

            table = new PdfPTable(columnDefinitionSize);
            table.setHorizontalAlignment(0);
            table.setTotalWidth(width - 72);
            table.setLockedWidth(true);

            cell = new PdfPCell(new Phrase("Aspirantenrooster week "+activity.weekNumber, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPaddingBottom(10);
            cell.setColspan(columnDefinitionSize.length);
            cell.setBorder(0);
            table.addCell(cell);

            table.addCell(new Phrase("Naam", boldFont));

            PdfPCell dayCell;
            dayCell = new PdfPCell(new Phrase("Maandag", boldFont));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Dinsdag", boldFont));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Woensdag", boldFont));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Donderdag", boldFont));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Vrijdag", boldFont));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Zaterdag", boldFont));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);
            dayCell = new PdfPCell(new Phrase("Zondag", boldFont));
            dayCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(dayCell);

            //Every pupil has two lines
            for (Map.Entry<String, Pupil> set : pupilsMap.entrySet()) {
                Pupil pupil = set.getValue();

                if(!pupil.getShouldDisplay()) continue;

                //First line: pupil's name with shifts
                PdfPCell nameCell = new PdfPCell(new Phrase(pupil.getNeatName(), boldFont));
                table.addCell(nameCell);
                for(Shift shift : pupil.getShifts()) {
                    PdfPCell shiftNumberCell = new PdfPCell(new Phrase(shift.getNeatShiftNumber(), font));
                    shiftNumberCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(shiftNumberCell);
                }

                //Second line: mentor or extra information
                table.addCell(new Phrase("Mentor / extra informatie", font));
                for(Shift shift : pupil.getShifts()) {
                    PdfPCell infoCell;
                    if(shift.withMentor()) infoCell = new PdfPCell(new Phrase(shift.getMentor().getNeatName(), smallFont));
                    else if (shift.shouldNotDisplayExtraInfo()) infoCell = new PdfPCell(new Phrase("-", smallFont));
                    else if (shift.getExtraInfo().length() > 1) infoCell = new PdfPCell(new Phrase(shift.getExtraInfo(), smallFont));
                    else infoCell = new PdfPCell(new Phrase("-", font));

                    infoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(infoCell);
                }
            }



            document.add(table);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        document.close();

        File pdfFile = new File(path + fileName);
        if(pdfFile.exists()) {
            Logger.debug(TAG, "PDF created successfully at: " + pdfFile.getAbsolutePath());

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri1 = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", pdfFile);
            intent.setDataAndType(uri1, "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            activity.startActivity(intent);
        } else {
            Logger.debug(TAG, "PDF file doesn't exist!");
        }
    }

}
