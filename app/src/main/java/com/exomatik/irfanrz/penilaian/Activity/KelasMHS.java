package com.exomatik.irfanrz.penilaian.Activity;

import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.irfanrz.penilaian.Authentication.login.loginUser;
import com.exomatik.irfanrz.penilaian.CustomDialog.CustomDialogExcel;
import com.exomatik.irfanrz.penilaian.CustomDialog.CustomDialogTambahKelas;
import com.exomatik.irfanrz.penilaian.DataModel.Pertemuan;
import com.exomatik.irfanrz.penilaian.DataModel.Final;
import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.DataModel.Laporan;
import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.exomatik.irfanrz.penilaian.DataModel.Mid;
import com.exomatik.irfanrz.penilaian.DataModel.Quiz2;
import com.exomatik.irfanrz.penilaian.DataModel.Quiz1;
import com.exomatik.irfanrz.penilaian.DataModel.Tugas;
import com.exomatik.irfanrz.penilaian.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;

import io.supercharge.shimmerlayout.ShimmerLayout;

import static android.provider.ContactsContract.Directory.PACKAGE_NAME;

public class KelasMHS extends AppCompatActivity {
    public static Kelas dataKelas;
    public static boolean act = true;
    public static int openFile = 0;
    public static String path;
    public RelativeLayout rlPenilaian, rlExport, rlQr, rlMain;
    public ShimmerLayout shimmerLoad, shimmerLoad2;
    public Context context;
    public TextView textKelas, textDesc, textJumlah;
    public ImageView imgBack, imgHapus;
    private TableLayout tablePenilaian;
    private ArrayList<Mhs> listMhs = new ArrayList<Mhs>();
    int nPertemuan, nLaporan, nQuiz1, nQuiz2, nTugas, nMid, nFinal;
    int tempQuiz1, tempQuiz2, tempTugas, tempMid, tempFinal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kelas);

        textKelas = (TextView) findViewById(R.id.text_kelas);
        textDesc = (TextView) findViewById(R.id.text_desc);
        textJumlah = (TextView) findViewById(R.id.et_jumlah);
        imgBack = (ImageView) findViewById(R.id.back);
        imgHapus = (ImageView) findViewById(R.id.hapus);
        rlPenilaian = (RelativeLayout) findViewById(R.id.rl_penilaian);
        rlExport = (RelativeLayout) findViewById(R.id.rl_export);
        rlQr = (RelativeLayout) findViewById(R.id.rl_qr);
        rlMain = (RelativeLayout) findViewById(R.id.rl_main);
        tablePenilaian = (TableLayout) findViewById(R.id.table_layout_table);
        shimmerLoad = (ShimmerLayout) findViewById(R.id.shimmer_text);
        shimmerLoad2 = (ShimmerLayout) findViewById(R.id.shimmer_text2);

        rlExport.setVisibility(View.VISIBLE);
        rlPenilaian.setVisibility(View.VISIBLE);
        rlQr.setVisibility(View.VISIBLE);
        imgHapus.setVisibility(View.VISIBLE);
        shimmerLoad.startShimmerAnimation();
        shimmerLoad2.startShimmerAnimation();
        context = getApplicationContext();
        textKelas.setText(dataKelas.namaKelas);
        textDesc.setText(dataKelas.descKelas);
        getJumlahMhs();

        imgHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(v);
            }
        });
        rlPenilaian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahPenilaian();
            }
        });
        rlQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerateQr.dataKelas = new Kelas(dataKelas.namaKelas, dataKelas.pengajarKelas, dataKelas.uidPengajar
                        , dataKelas.idKelas, dataKelas.descKelas
                );
                startActivity(new Intent(KelasMHS.this, GenerateQr.class));
                finish();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(KelasMHS.this, Menu.class));
                finish();
            }
        });
    }

    public void onDeleteClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(KelasMHS.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                try {
                    DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().child("kelas")
                            .child(dataKelas.pengajarKelas).child("id_" + dataKelas.idKelas);
                    db_node.removeValue();
                    db_node = FirebaseDatabase.getInstance().getReference().child("mahasiswa")
                            .child("kelas_" + dataKelas.idKelas);
                    db_node.removeValue();
                    db_node = FirebaseDatabase.getInstance().getReference().child("mahasiswa")
                            .child("kelas_" + dataKelas.idKelas);
                    db_node.removeValue();
                    db_node = FirebaseDatabase.getInstance().getReference().child("nilaiLaporan")
                            .child("kelas_" + dataKelas.idKelas);
                    db_node.removeValue();
                    db_node = FirebaseDatabase.getInstance().getReference().child("nilaiMid")
                            .child("kelas_" + dataKelas.idKelas);
                    db_node.removeValue();
                    db_node = FirebaseDatabase.getInstance().getReference().child("nilaiPertemuan")
                            .child("kelas_" + dataKelas.idKelas);
                    db_node.removeValue();
                    db_node = FirebaseDatabase.getInstance().getReference().child("nilaiQuiz1")
                            .child("kelas_" + dataKelas.idKelas);
                    db_node.removeValue();
                    db_node = FirebaseDatabase.getInstance().getReference().child("nilaiQuiz2")
                            .child("kelas_" + dataKelas.idKelas);
                    db_node.removeValue();
                    db_node = FirebaseDatabase.getInstance().getReference().child("nilaiTugas")
                            .child("kelas_" + dataKelas.idKelas);
                    db_node.removeValue();
                }catch (Exception e){

                }
                startActivity(new Intent(KelasMHS.this, Menu.class));
                finish();
                Toast.makeText(KelasMHS.this, "Berhasil Menghapus", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }

    private void exportToExcel(final ArrayList<Pertemuan> pertemuans, final ArrayList<Laporan> laporans
            , final ArrayList<Quiz1> quiz1s, final ArrayList<Quiz2> quiz2s, final ArrayList<Mid> mids, ArrayList<Final> finals
            , ArrayList<Tugas> tugases, ArrayList<String> akhirs, ArrayList<String> hurufs
    ) {
        DialogFragment newFragment = CustomDialogExcel
                .newInstance();

        CustomDialogExcel.listMhs = listMhs;
        CustomDialogExcel.pertemuans = pertemuans;
        CustomDialogExcel.laporans = laporans;
        CustomDialogExcel.quiz1s = quiz1s;
        CustomDialogExcel.quiz2s = quiz2s;
        CustomDialogExcel.tugases = tugases;
        CustomDialogExcel.mids = mids;
        CustomDialogExcel.fin = finals;
        CustomDialogExcel.nilaiAkhir = akhirs;
        CustomDialogExcel.hurufAkhir = hurufs;
        newFragment.setCancelable(false);
        newFragment.show(getFragmentManager(), "dialog");

        handler();
    }

    private void handler (){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (openFile == 1){
                    open_file(path);
                    openFile = 2;
                }
                else if (openFile == 2){
                }
                else{
                    handler();
                }
            }
        }, 3000);
        openFile = 0;

    }

    private void open_file(String filename) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "";
        try {
            type = "application/wps";
        }catch (Exception e){
            type = "application/msword";
        }
        intent.setDataAndType(Uri.parse(filename), type);
        startActivity(intent);
    }

    private void tambahPenilaian() {
        TambahPenilaian.listMhs = listMhs;
        startActivity(new Intent(KelasMHS.this, TambahPenilaian.class));
        finish();
    }

    private void setDataTabel(final ArrayList<Pertemuan> pertemuans, final ArrayList<Laporan> laporans
            , final ArrayList<Quiz1> quiz1s, final ArrayList<Quiz2> quiz2s, final ArrayList<Mid> mids, final ArrayList<Final> finals
            , final ArrayList<Tugas> tugases
    ) {

        shimmerLoad2.stopShimmerAnimation();
        shimmerLoad2.setVisibility(View.GONE);

        int nomor = 1;
        tempQuiz1 = 0;
        tempQuiz2 = 0;
        tempTugas = 0;
        tempMid = 0;
        tempFinal = 0;
        final ArrayList<String> akhirs = new ArrayList<String>();
        final ArrayList<String> hurufs = new ArrayList<String>();
        for (int a = 0; a < listMhs.size(); a++) {
            TableRow tableRow = new TableRow(this);
//            TableRow tableRow = (TableRow) tablePenilaian.getChildAt(1);
//            tableRow.removeAllViews();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                tableRow.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
            }

            // Set new table row layout parameters.
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            // Add a TextView in the first column.
            TextView textNo = new TextView(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textNo.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
            }
            textNo.setText("  " + Integer.toString(nomor));
            nomor++;
            String nim = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String nimWarna = nim.replace("@uin-alauddin.ac.id", "");

            textNo.setTextColor(getResources().getColor(R.color.hitam));
            textNo.setTextSize(14);
            tableRow.addView(textNo, 0);

            TextView textNIM = new TextView(context);
            textNIM.setText("  " + listMhs.get(a).nimMhs + "   ");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textNIM.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
            }
            textNIM.setTextColor(getResources().getColor(R.color.hitam));
            textNIM.setTextSize(14);

            tableRow.addView(textNIM, 1);

            TextView textNama = new TextView(context);
            textNama.setText("  " + listMhs.get(a).namaMhs);
            textNama.setTextColor(getResources().getColor(R.color.hitam));
            textNama.setTextSize(14);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                textNama.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
            }
            if (listMhs.get(a).nimMhs.equals(nimWarna)) {
                textNo.setBackgroundColor(getResources().getColor(R.color.green));
                textNIM.setBackgroundColor(getResources().getColor(R.color.green));
                textNama.setBackgroundColor(getResources().getColor(R.color.green));
            }
            tableRow.addView(textNama, 2);

            int row = 3;
            for (int b = 0; b < 23; b++) {
                TextView textLaporan = new TextView(context);
                textLaporan.setText("");
                textLaporan.setTextColor(getResources().getColor(R.color.hitam));
                textLaporan.setTextSize(14);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textLaporan.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
                }
                if (listMhs.get(a).nimMhs.equals(nimWarna)) {
                    textLaporan.setBackgroundColor(getResources().getColor(R.color.green));
                }
                tableRow.addView(textLaporan, row);
                row++;
            }

            //tabel pertemuan
            row = 3;
            for (int ke = 0; ke < 10; ke++) {
                TextView textPertemuan = new TextView(context);
                if (pertemuans.size() != 0) {
                    for (int x = 0; x < pertemuans.size(); x++) {
                        if (ke == pertemuans.get(x).ke) {
                            if ((listMhs.get(a).idMhs.equals(pertemuans.get(x).idMhs))
                                    && (listMhs.get(a).idKelas == pertemuans.get(x).idKelas)) {
                                textPertemuan.setText(Integer.toString(pertemuans.get(x).Pertemuan));
                            }
                        }
                    }
                }
                textPertemuan.setTextColor(getResources().getColor(R.color.hitam));
                textPertemuan.setTextSize(14);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textPertemuan.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
                }
                if (listMhs.get(a).nimMhs.equals(nimWarna)) {
                    textPertemuan.setBackgroundColor(getResources().getColor(R.color.green));
                }
                tableRow.addView(textPertemuan, row);
                row++;
            }

            //tabel laporan
            row = 13;
            for (int ke = 0; ke < 8; ke++) {
                TextView textLaporan = new TextView(context);

                if (laporans.size() != 0) {
                    for (int x = 0; x < laporans.size(); x++) {
                        if (ke == laporans.get(x).ke) {
                            if ((listMhs.get(a).idMhs.equals(laporans.get(x).idMhs))
                                    && (listMhs.get(a).idKelas == laporans.get(x).idKelas)) {
                                textLaporan.setText(Integer.toString(laporans.get(x).Laporan));
                            }
                        }
                    }

                    textLaporan.setTextColor(getResources().getColor(R.color.hitam));
                    textLaporan.setTextSize(14);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        textLaporan.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
                    }
                    if (listMhs.get(a).nimMhs.equals(nimWarna)) {
                        textLaporan.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                    tableRow.addView(textLaporan, row);
                    row++;
                }
            }

                //tabel Quiz 1
                TextView textQuiz1 = new TextView(context);
                if (quiz1s.size() != 0) {
                    for (int x = tempQuiz1; x < quiz1s.size(); x++) {
                        if ((listMhs.get(a).idMhs.equals(quiz1s.get(x).idMhs))
                                && (listMhs.get(a).idKelas == quiz1s.get(x).idKelas)) {
                            textQuiz1.setText(Integer.toString(quiz1s.get(x).Quiz1));
                            tempQuiz1++;
                            if (tempQuiz1 == quiz1s.size()){
                                tempQuiz1=0;
                            }
                        }
                    }
                }
                textQuiz1.setTextColor(getResources().getColor(R.color.hitam));
                textQuiz1.setTextSize(14);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textQuiz1.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
                }
                tableRow.addView(textQuiz1, 21);

                //tabel Quiz 2
                TextView textQuiz2 = new TextView(context);
                if (quiz2s.size() != 0) {
                    for (int x = tempQuiz2; x < quiz2s.size(); x++) {
                        if ((listMhs.get(a).idMhs.equals(quiz2s.get(x).idMhs))
                                && (listMhs.get(a).idKelas == quiz2s.get(x).idKelas)) {
                            textQuiz2.setText(Integer.toString(quiz2s.get(x).Quiz2));
                            tempQuiz2++;
                            if (tempQuiz2 == quiz2s.size()){
                                tempQuiz2=0;
                            }
                        }
                    }
                }
                textQuiz2.setTextColor(getResources().getColor(R.color.hitam));
                textQuiz2.setTextSize(14);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textQuiz2.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
                }
                tableRow.addView(textQuiz2, 22);

                //tabel Tugas
                TextView textTugas = new TextView(context);
                if (tugases.size() != 0) {
                    for (int x = tempTugas; x < tugases.size(); x++) {
                        if ((listMhs.get(a).idMhs.equals(tugases.get(x).idMhs))
                                && (listMhs.get(a).idKelas == tugases.get(x).idKelas)) {
                            textTugas.setText(Integer.toString(tugases.get(x).Tugas));
                            tempTugas++;
                            if (tempTugas == tugases.size()){
                                tempTugas=0;
                            }
                        }
                    }
                }
                textTugas.setTextColor(getResources().getColor(R.color.hitam));
                textTugas.setTextSize(14);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textTugas.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
                }
                tableRow.addView(textTugas, 23);

                //tabel Mid
                TextView textMid = new TextView(context);
                if (mids.size() != 0) {
                    for (int x = tempMid; x < mids.size(); x++) {
                        if ((listMhs.get(a).idMhs.equals(mids.get(x).idMhs))
                                && (listMhs.get(a).idKelas == mids.get(x).idKelas)) {
                            textMid.setText(Integer.toString(mids.get(x).Mid));
                            tempMid++;
                            if (tempMid == mids.size()){
                                tempMid=0;
                            }
                        }
                    }
                }
                textMid.setTextColor(getResources().getColor(R.color.hitam));
                textMid.setTextSize(14);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textMid.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
                }
                tableRow.addView(textMid, 24);

                //tabel Final
                TextView textFinal = new TextView(context);
                if (finals.size() != 0) {
                    for (int x = tempFinal; x < finals.size(); x++) {
                        if ((listMhs.get(a).idMhs.equals(finals.get(x).idMhs))
                                && (listMhs.get(a).idKelas == finals.get(x).idKelas)) {
                            textFinal.setText(Integer.toString(finals.get(x).Final));
                            tempFinal++;
                            if (tempFinal == finals.size()){
                                tempFinal=0;
                            }
                        }
                    }
                }
                textFinal.setTextColor(getResources().getColor(R.color.hitam));
                textFinal.setTextSize(14);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textFinal.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
                }
                tableRow.addView(textFinal, 25);

                //text Nilai Akhir
                TextView textNilai = new TextView(context);

                int rRow = 3;
                nPertemuan = 0;
                for (int z = 0; z < 10; z++) {
                    TextView textF = (TextView) tableRow.getChildAt(rRow);
                    String text = textF.getText().toString();
                    if (!text.isEmpty()) {
                        nPertemuan = nPertemuan + Integer.parseInt(text);
                    }
                    rRow++;
                }
                rRow = 13;
                nLaporan = 0;
                for (int z = 0; z < 8; z++) {
                    TextView textF = (TextView) tableRow.getChildAt(rRow);
                    String text = textF.getText().toString();
                    if (!text.isEmpty()) {
                        nLaporan = nLaporan + Integer.parseInt(text);
                    }
                    rRow++;
                }
                TextView textF = (TextView) tableRow.getChildAt(rRow);
                String text = textF.getText().toString();
                if (!text.isEmpty()) {
                    nLaporan = nLaporan + Integer.parseInt(text);
                }
                TextView textQ1 = (TextView) tableRow.getChildAt(21);
                TextView textQ2 = (TextView) tableRow.getChildAt(22);
                TextView textTgs = (TextView) tableRow.getChildAt(23);
                TextView textMl = (TextView) tableRow.getChildAt(24);
                TextView textFl = (TextView) tableRow.getChildAt(25);
                String nQ1 = textQ1.getText().toString();
                String nQ2 = textQ2.getText().toString();
                String nTgs = textTgs.getText().toString();
                String nMl = textMl.getText().toString();
                String nFl = textFl.getText().toString();
                if (!nQ1.isEmpty()) {
                    nQuiz1 = Integer.parseInt(nQ1);
                }
                if (!nQ2.isEmpty()) {
                    nQuiz2 = Integer.parseInt(nQ2);
                }
                if (!nTgs.isEmpty()) {
                    nTugas = Integer.parseInt(nTgs);
                }
                if (!nMl.isEmpty())
                if (!nFl.isEmpty()) {
                    nFinal = Integer.parseInt(nFl);
                }

                int per = nPertemuan * 100;
                int p = per / 10;
                double perAkh = p * 0.2;
                double lapAkh = (nLaporan + nTugas) / 9;
                double quizAkh = nQuiz1 + nQuiz2 / 2 * 0.1;
                double midAkh = nMid * 0.1;
                double finalAkh = nFinal * 0.2;

                final double akhir = perAkh + (lapAkh * 0.4) + (quizAkh * 0.1) + midAkh + finalAkh;
                textNilai.setText(Double.toString(akhir));

                textNilai.setTextColor(getResources().getColor(R.color.hitam));
                textNilai.setTextSize(14);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textNilai.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
                }
                tableRow.addView(textNilai, 26);

                TextView textHuruf = new TextView(context);
                final String hurufAkhir;
                if ((akhir >= 80) && (akhir <= 100)) {
                    textHuruf.setText("A");
                    hurufAkhir = "A";
                } else if ((akhir >= 75) && (akhir <= 79)) {
                    textHuruf.setText("B");
                    hurufAkhir = "B";
                } else if ((akhir >= 60) && (akhir <= 74)) {
                    textHuruf.setText("C");
                    hurufAkhir = "C";
                } else if ((akhir >= 40) && (akhir <= 59)) {
                    textHuruf.setText("D");
                    hurufAkhir = "D";
                } else {
                    textHuruf.setText("E");
                    hurufAkhir = "E";
                }

                akhirs.add(Double.toString(akhir));
                hurufs.add(hurufAkhir);

                textHuruf.setTextColor(getResources().getColor(R.color.hitam));
                textHuruf.setTextSize(14);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textHuruf.setBackground(getResources().getDrawable(R.drawable.border_tabel_hitam));
                }
                if (listMhs.get(a).nimMhs.equals(nimWarna)) {
                    textQuiz1.setBackgroundColor(getResources().getColor(R.color.green));
                    textQuiz2.setBackgroundColor(getResources().getColor(R.color.green));
                    textTugas.setBackgroundColor(getResources().getColor(R.color.green));
                    textMid.setBackgroundColor(getResources().getColor(R.color.green));
                    textFinal.setBackgroundColor(getResources().getColor(R.color.green));
                    textNilai.setBackgroundColor(getResources().getColor(R.color.green));
                    textHuruf.setBackgroundColor(getResources().getColor(R.color.green));
                }
                tableRow.addView(textHuruf, 27);

                tablePenilaian.addView(tableRow);

                rlExport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exportToExcel(pertemuans, laporans, quiz1s, quiz2s, mids, finals, tugases, akhirs, hurufs);
                    }
                });
            }
        }
//    }

    public void getJumlahMhs() {
        FirebaseDatabase.getInstance().getReference().child("mahasiswa").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                listMhs = new ArrayList<Mhs>();
                int jumlah = 0;
                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Mhs data = (Mhs) ((DataSnapshot) dataDS).getValue(Mhs.class);
                        if (data.idKelas == dataKelas.idKelas) {
                            listMhs.add(new Mhs(data.idMhs, data.namaMhs, data.nimMhs, data.idKelas, data.uidPengajar
                            ));
                        }
                    }
                }
                jumlah = listMhs.size();
                textJumlah.setText(Integer.toString(jumlah));
                shimmerLoad.stopShimmerAnimation();
                shimmerLoad.setVisibility(View.GONE);
                rlMain.setVisibility(View.VISIBLE);

                getNilaiPertemuan();
            }
        });
    }

    private void getNilaiPertemuan() {
        FirebaseDatabase.getInstance().getReference().child("nilaiPertemuan").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                ArrayList<Pertemuan> dataAsis = new ArrayList<Pertemuan>();

                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Iterator localIterator2 = dataDS.getChildren().iterator();
                        while (localIterator2.hasNext()) {
                            DataSnapshot dataDS2 = (DataSnapshot) localIterator2.next();
                            Pertemuan data = (Pertemuan) ((DataSnapshot) dataDS2).getValue(Pertemuan.class);
                            dataAsis.add(new Pertemuan(data.idMhs, data.idKelas, data.Pertemuan, data.ke));
                        }
                    }
                }
                getNilaiLaporan(dataAsis);

            }
        });
    }

    private void getNilaiLaporan(final ArrayList<Pertemuan> pertemuans) {
        FirebaseDatabase.getInstance().getReference().child("nilaiLaporan").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                ArrayList<Laporan> dataAsis = new ArrayList<Laporan>();

                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Iterator localIterator2 = dataDS.getChildren().iterator();
                        while (localIterator2.hasNext()) {
                            DataSnapshot dataDS2 = (DataSnapshot) localIterator2.next();
                            Laporan data = (Laporan) ((DataSnapshot) dataDS2).getValue(Laporan.class);
                            dataAsis.add(new Laporan(data.idMhs, data.idKelas, data.Laporan, data.ke));
                        }
                    }
                }
                getNilaiQuiz1(pertemuans, dataAsis);
            }
        });
    }

    private void getNilaiQuiz1(final ArrayList<Pertemuan> pertemuans, final ArrayList<Laporan> laporans) {
        FirebaseDatabase.getInstance().getReference().child("nilaiQuiz1").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                ArrayList<Quiz1> dataAsis = new ArrayList<Quiz1>();

                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Quiz1 data = (Quiz1) ((DataSnapshot) dataDS).getValue(Quiz1.class);
                        dataAsis.add(new Quiz1(data.idMhs, data.idKelas, data.Quiz1));
                    }
                }
                getNilaiQuiz2(pertemuans, laporans, dataAsis);
            }
        });
    }

    private void getNilaiQuiz2(final ArrayList<Pertemuan> pertemuans, final ArrayList<Laporan> laporans, final ArrayList<Quiz1> quiz1s) {
        FirebaseDatabase.getInstance().getReference().child("nilaiQuiz2").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                ArrayList<Quiz2> dataAsis = new ArrayList<Quiz2>();

                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Quiz2 data = (Quiz2) ((DataSnapshot) dataDS).getValue(Quiz2.class);
                        dataAsis.add(new Quiz2(data.idMhs, data.idKelas, data.Quiz2));
                    }
                }
                getNilaiMid(pertemuans, laporans, quiz1s, dataAsis);
            }
        });
    }

    private void getNilaiMid(final ArrayList<Pertemuan> pertemuans, final ArrayList<Laporan> laporans
            , final ArrayList<Quiz1> quiz1s, final ArrayList<Quiz2> quiz2s) {
        FirebaseDatabase.getInstance().getReference().child("nilaiMid").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                ArrayList<Mid> dataAsis = new ArrayList<Mid>();

                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Mid data = (Mid) ((DataSnapshot) dataDS).getValue(Mid.class);
                        dataAsis.add(new Mid(data.idMhs, data.idKelas, data.Mid));
                    }
                }
                getNilaiFinal(pertemuans, laporans, quiz1s, quiz2s, dataAsis);
            }
        });
    }

    private void getNilaiFinal(final ArrayList<Pertemuan> pertemuans, final ArrayList<Laporan> laporans
            , final ArrayList<Quiz1> quiz1s, final ArrayList<Quiz2> quiz2s, final ArrayList<Mid> mids) {
        FirebaseDatabase.getInstance().getReference().child("nilaiFinal").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                ArrayList<Final> dataAsis = new ArrayList<Final>();

                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Final data = (Final) ((DataSnapshot) dataDS).getValue(Final.class);
                        dataAsis.add(new Final(data.idMhs, data.idKelas, data.Final));
                    }
                }
                getNilaiTugas(pertemuans, laporans, quiz1s, quiz2s, mids, dataAsis);
            }
        });
    }

    private void getNilaiTugas(final ArrayList<Pertemuan> pertemuans, final ArrayList<Laporan> laporans
            , final ArrayList<Quiz1> quiz1s, final ArrayList<Quiz2> quiz2s, final ArrayList<Mid> mids, final ArrayList<Final> finals) {
        FirebaseDatabase.getInstance().getReference().child("nilaiTugas").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                ArrayList<Tugas> dataAsis = new ArrayList<Tugas>();

                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Tugas data = (Tugas) ((DataSnapshot) dataDS).getValue(Tugas.class);
                        dataAsis.add(new Tugas(data.idMhs, data.idKelas, data.Tugas));
                    }
                }
                setDataTabel(pertemuans, laporans, quiz1s, quiz2s, mids, finals, dataAsis);
            }
        });
    }


//    private void tambahMahasiswa() {
//        TambahMahasiswa.dataKelasMHS = new Kelas(dataKelas.namaKelas, dataKelas.pengajarKelas, dataKelas.uidPengajar,
//                dataKelas.idKelas, dataKelas.descKelas
//        );
//        TambahMahasiswa.idMhs = Integer.parseInt(textJumlah.getText().toString());
//        startActivity(new Intent(KelasMHS.this, TambahMahasiswa.class));
//        finish();
//    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(KelasMHS.this, Menu.class));
        finish();
    }
}
