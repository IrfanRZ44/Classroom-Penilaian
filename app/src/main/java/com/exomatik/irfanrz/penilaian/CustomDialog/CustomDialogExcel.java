package com.exomatik.irfanrz.penilaian.CustomDialog;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.irfanrz.penilaian.Activity.KelasMHS;
import com.exomatik.irfanrz.penilaian.DataModel.Final;
import com.exomatik.irfanrz.penilaian.DataModel.Laporan;
import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.exomatik.irfanrz.penilaian.DataModel.Mid;
import com.exomatik.irfanrz.penilaian.DataModel.Pertemuan;
import com.exomatik.irfanrz.penilaian.DataModel.Quiz1;
import com.exomatik.irfanrz.penilaian.DataModel.Quiz2;
import com.exomatik.irfanrz.penilaian.DataModel.Tugas;
import com.exomatik.irfanrz.penilaian.R;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IrfanRZ on 03/11/2018.
 */

public class CustomDialogExcel extends DialogFragment {
    private Button customDialog_Dismiss, buttonTambah;
    private EditText etNama;
    private View v;
    static String TAG = "ExelLog";
    public static String path;
    public static ArrayList<Mhs> listMhs;
    public static ArrayList<Pertemuan> pertemuans;
    public static ArrayList<Laporan> laporans;
    public static ArrayList<Quiz1> quiz1s;
    public static ArrayList<Quiz2> quiz2s;
    public static ArrayList<Mid> mids;
    public static ArrayList<Final> fin;
    public static ArrayList<Tugas> tugases;
    public static ArrayList<String> nilaiAkhir;
    public static ArrayList<String> hurufAkhir;
    static int tempLaporan, tempPertemuan, tempQuiz1, tempQuiz2, tempMid, tempTugas, tempFinal, tempNilai, tempHuruf;

    public static CustomDialogExcel newInstance() {
        return new CustomDialogExcel();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_excel, container, false);

        customDialog_Dismiss = (Button) dialogView.findViewById(R.id.dialog_dismiss);
        buttonTambah = (Button) dialogView.findViewById(R.id.dialog_tambah);
        etNama = (EditText) dialogView.findViewById(R.id.et_tambah);

        v = dialogView;

        buttonTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etNama.getText().toString();

                if (name.isEmpty()) {
                    Snackbar snackbar = Snackbar
                            .make(v, "Mohon isi data", Snackbar.LENGTH_LONG);

                    snackbar.show();
                } else {
                    if (saveExcelFile(getContext(), name+".xlsx")){
                        KelasMHS.openFile = 1;
                        KelasMHS.path = path;
                        Toast.makeText(getContext(), "Berhasil Menyimpan " + path, Toast.LENGTH_LONG).show();
                        dismiss();
                    }
                    else {
                        Toast.makeText(getContext(), "Gagal Menyimpan", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        customDialog_Dismiss.setOnClickListener(customDialog_DismissOnClickListener);

        return dialogView;
    }

    private static boolean saveExcelFile(Context context, String fileName) {
        tempLaporan = 0;
        tempPertemuan = 0;
        tempQuiz1 = 0;
        tempQuiz2 = 0;
        tempMid = 0;
        tempFinal = 0;
        tempTugas = 0;
        tempNilai = 0;
        tempHuruf = 0;

        // check if available and not read only
        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
//            Toast.makeText(context, "Storage Failed", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Storage not available or read only");
            return false;
        }

        boolean success = false;

        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setAlignment(CellStyle.ALIGN_CENTER);
        cs.setFillForegroundColor(HSSFColor.AQUA.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cs.setBorderBottom(CellStyle.BORDER_THIN);
        cs.setBorderTop(CellStyle.BORDER_THIN);
        cs.setBorderLeft(CellStyle.BORDER_THIN);
        cs.setBorderRight(CellStyle.BORDER_THIN);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Penilaian");
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 3, 12));
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 13, 20));
        sheet1.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        sheet1.addMergedRegion(new CellRangeAddress(0, 1, 1, 1));
        sheet1.addMergedRegion(new CellRangeAddress(0, 1, 2, 2));

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("No. ");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("NIM");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Nama");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Pertemuan");
        c.setCellStyle(cs);

        c = row.createCell(13);
        c.setCellValue("Laporan");
        c.setCellStyle(cs);


        c = row.createCell(21);
        c.setCellValue("Quiz 1");
        c.setCellStyle(cs);

        c = row.createCell(22);
        c.setCellValue("Quiz 2");
        c.setCellStyle(cs);

        c = row.createCell(23);
        c.setCellValue("Tugas");
        c.setCellStyle(cs);

        c = row.createCell(24);
        c.setCellValue("Mid");
        c.setCellStyle(cs);

        c = row.createCell(25);
        c.setCellValue("Final");
        c.setCellStyle(cs);

        c = row.createCell(26);
        c.setCellValue("Nilai Akhir");
        c.setCellStyle(cs);

        c = row.createCell(27);
        c.setCellValue("Nilai Huruf");
        c.setCellStyle(cs);

        cs.setFillForegroundColor(HSSFColor.WHITE.index);

        Row row2 = sheet1.createRow(1);
        int nomor = 1;
        for (int a = 3; a <=12; a++){
            c = row2.createCell(a);
            c.setCellValue(nomor);
            c.setCellStyle(cs);
            nomor++;
        }
        nomor = 1;
        for (int a = 13; a <=20; a++){
            c = row2.createCell(a);
            c.setCellValue(nomor);
            c.setCellStyle(cs);
            nomor++;
        }

        int kolom = 2;
        int baris = 0;
        nomor = 1;
        for (int a=0; a<listMhs.size(); a++){
            Row row3 = sheet1.createRow(kolom);

            c = row3.createCell(0);
            c.setCellValue(nomor);
            c.setCellStyle(cs);
            nomor++;

            c = row3.createCell(1);
            c.setCellValue(listMhs.get(a).nimMhs);
            c.setCellStyle(cs);

            c = row3.createCell(2);
            c.setCellValue(listMhs.get(a).namaMhs);
            c.setCellStyle(cs);

            //tabel pertemuan
            baris = 3;
            for (int ke = 0; ke < 10; ke++) {
                if (pertemuans.size() != 0) {
                    for (int x = 0; x < pertemuans.size(); x++) {
                        if (ke == pertemuans.get(x).ke) {
                            if ((listMhs.get(a).idMhs.equals(pertemuans.get(x).idMhs))
                                    && (listMhs.get(a).idKelas == pertemuans.get(x).idKelas)) {
                                c = row3.createCell(baris);
                                c.setCellValue(Integer.toString(pertemuans.get(x).Pertemuan));
                                c.setCellStyle(cs);
                            }
                        }
                    }
                }
                baris++;
            }
            //tabel laporan
            baris = 13;
            for (int ke = 0; ke < 8; ke++) {
                if (laporans.size() != 0) {
                    for (int x = 0; x < laporans.size(); x++) {
                        if (ke == laporans.get(x).ke) {
                            if ((listMhs.get(a).idMhs.equals(laporans.get(x).idMhs))
                                    && (listMhs.get(a).idKelas == laporans.get(x).idKelas)) {
                                c = row3.createCell(baris);
                                c.setCellValue(Integer.toString(laporans.get(x).Laporan));
                                c.setCellStyle(cs);
                            }
                        }
                    }
                }
                baris++;
            }

            baris = 21;
            //tabel Quiz 1
            if (quiz1s.size() != 0) {
                if ((listMhs.get(a).idMhs.equals(quiz1s.get(tempQuiz1).idMhs))
                        && (listMhs.get(a).idKelas == quiz1s.get(tempQuiz1).idKelas)) {
                    c = row3.createCell(baris);
                    c.setCellValue(Integer.toString(quiz1s.get(tempQuiz1).Quiz1));
                    c.setCellStyle(cs);
                    tempQuiz1++;
                    if (tempQuiz1 == quiz1s.size()) {
                        tempQuiz1 = 0;
                    }
                }
            }

            baris = 22;
            //tabel Quiz 2
            if (quiz2s.size() != 0) {
                if ((listMhs.get(a).idMhs.equals(quiz2s.get(tempQuiz2).idMhs))
                        && (listMhs.get(a).idKelas == quiz2s.get(tempQuiz2).idKelas)) {
                    c = row3.createCell(baris);
                    c.setCellValue(Integer.toString(quiz2s.get(tempQuiz2).Quiz2));
                    c.setCellStyle(cs);
                    tempQuiz2++;
                    if (tempQuiz2 == quiz2s.size()) {
                        tempQuiz2 = 0;
                    }
                }
            }

            baris = 23;
            //tabel Tugas
            if (tugases.size() != 0) {
                for (int x = tempTugas; x < tugases.size(); x++) {
                    if ((listMhs.get(a).idMhs.equals(tugases.get(x).idMhs))
                            && (listMhs.get(a).idKelas == tugases.get(x).idKelas)) {
                        c = row3.createCell(baris);
                        c.setCellValue(Integer.toString(tugases.get(x).Tugas));
                        c.setCellStyle(cs);
                    }
                }
            }

            baris = 24;
            //tabel Mid
            if (mids.size() != 0) {
                for (int x = tempMid; x < mids.size(); x++) {
                    if ((listMhs.get(a).idMhs.equals(mids.get(x).idMhs))
                            && (listMhs.get(a).idKelas == mids.get(x).idKelas)) {
                        c = row3.createCell(baris);
                        c.setCellValue(Integer.toString(mids.get(x).Mid));
                        c.setCellStyle(cs);
                        tempMid++;
                        if (tempMid == mids.size()) {
                            tempMid = 0;
                        }
                    }
                }
            }

            baris = 25;
            //tabel Final
            if (fin.size() != 0) {
                if ((listMhs.get(a).idMhs.equals(fin.get(tempFinal).idMhs))
                        && (listMhs.get(a).idKelas == fin.get(tempFinal).idKelas)) {
                    c = row3.createCell(baris);
                    c.setCellValue(Integer.toString(fin.get(tempFinal).Final));
                    c.setCellStyle(cs);
                    tempFinal++;
                    if (tempFinal == fin.size()) {
                        tempFinal = 0;
                    }
                }
            }

            baris = 26;
            //tabel nilai Akhir
            if (nilaiAkhir.size() != 0) {
                    c = row3.createCell(baris);
                    c.setCellValue(nilaiAkhir.get(tempNilai));
                    c.setCellStyle(cs);
                    tempNilai++;
                    if (tempNilai == nilaiAkhir.size()) {
                        tempNilai = 0;
                    }
            }

            baris = 27;
            //tabel huruf Akhir
            if (hurufAkhir.size() != 0) {
                c = row3.createCell(baris);
                c.setCellValue(hurufAkhir.get(tempHuruf));
                c.setCellStyle(cs);
                tempHuruf++;
                if (tempHuruf == hurufAkhir.size()) {
                    tempHuruf = 0;
                }
            }


            kolom++;
        }


        sheet1.setColumnWidth(0, (15 * 70));
        sheet1.setColumnWidth(1, (15 * 250));
        sheet1.setColumnWidth(2, (15 * 300));
        for (int a = 3; a <= 25; a++){
            sheet1.setColumnWidth(a, (15 * 100));
        }
        sheet1.setColumnWidth(26, (15 * 200));
        sheet1.setColumnWidth(27, (15 * 200));
        // Create a path where we will place our List of objects on external storage
        File file = new File(context.getExternalFilesDir(null), fileName);
        FileOutputStream os = null;

        try {
            os = new FileOutputStream(file);
            wb.write(os);
            Log.w("FileUtils", "Writing file" + file);
            success = true;
            path = file.toString();
        } catch (IOException e) {
            Log.w("FileUtils", "Error writing " + file, e);
        } catch (Exception e) {
            Log.w("FileUtils", "Failed to save file", e);
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        return success;
    }
    private static void readExcelFile(Context context, String filename) {

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly())
        {
            Log.e(TAG, "Storage not available or read only");
            Toast.makeText(context, "Storage not available or read only", Toast.LENGTH_SHORT).show();
            return;
        }

        try{
            // Creating Input Stream
            File file = new File(context.getExternalFilesDir(null), filename);
            FileInputStream myInput = new FileInputStream(file);

            // Create a POIFSFileSystem object
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);

            // Create a workbook using the File System
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);

            // Get the first sheet from workbook
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);

            /** We now need something to iterate through the cells.**/
            Iterator rowIter = mySheet.rowIterator();

            while(rowIter.hasNext()){
                HSSFRow myRow = (HSSFRow) rowIter.next();
                Iterator cellIter = myRow.cellIterator();
                while(cellIter.hasNext()){
                    HSSFCell myCell = (HSSFCell) cellIter.next();
//                    Log.d(TAG, "Cell Value: " +  myCell.toString());
                    Toast.makeText(context, "cell Value: " + myCell.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){e.printStackTrace(); }

        return;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }



    private Button.OnClickListener customDialog_DismissOnClickListener
            = new Button.OnClickListener() {

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            dismiss();
            KelasMHS.openFile = 2;
        }
    };
}
