package com.exomatik.irfanrz.penilaian.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.exomatik.irfanrz.penilaian.FirebaseClass.ItemClickSupport;
import com.exomatik.irfanrz.penilaian.R;
import com.exomatik.irfanrz.penilaian.RecyclerView.RecyclerMhs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class TambahPenilaian extends AppCompatActivity implements ItemClickSupport.OnItemClickListener{
    private ImageView back, imgDelete;
    private RecyclerView rcMahasiswa;
    private RelativeLayout rlNama, rlNilai;
    private TextView textNama, textNim, textNothing;
    private Spinner spinnerNilai, spinnerKe;
    private EditText etNilai, etNama;
    private Button btnTambahNilai;
    private int position;
    private View view;
    private boolean etPertemuan = false;
    public static ArrayList<Mhs> listMhs = new ArrayList<Mhs>();
    private ArrayList<Mhs> list = new ArrayList<Mhs>();
    private ProgressDialog progressDialog = null;
    private boolean recyclerPosition = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_penilaian);

        back = (ImageView) findViewById(R.id.back);
        imgDelete = (ImageView) findViewById(R.id.btn_delet_mahasiswa);
        rcMahasiswa = (RecyclerView) findViewById(R.id.rc_mahasiswa);
        rlNama = (RelativeLayout) findViewById(R.id.rl_nama);
        rlNilai = (RelativeLayout) findViewById(R.id.rl_nilai);
        textNama = (TextView) findViewById(R.id.text_nama);
        textNim = (TextView) findViewById(R.id.text_nim);
        textNothing = (TextView) findViewById(R.id.text_nothing);
        spinnerNilai = (Spinner) findViewById(R.id.spinner_nilai);
        spinnerKe = (Spinner) findViewById(R.id.spinner_ke);
        etNilai = (EditText) findViewById(R.id.et_nilai);
        etNama = (EditText) findViewById(R.id.et_nama);
        btnTambahNilai = (Button) findViewById(R.id.btn_tambah_nilai);
        view = findViewById(android.R.id.content);

        setRecyclerView(listMhs);
        setSpinnerNilai();
        setSpinnerKe("Laporan");

        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDeleteClick(v);
            }
        });

        etNama.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                recyclerPosition = false;
                String nama = etNama.getText().toString();
                list.removeAll(list);
                list = new ArrayList<>();
                for (int a=0; a<listMhs.size(); a++){
                    if (listMhs.get(a).namaMhs.contains(nama)){
                        list.add(new Mhs(listMhs.get(a).idMhs, listMhs.get(a).namaMhs, listMhs.get(a).nimMhs
                                        , listMhs.get(a).idKelas, listMhs.get(a).uidPengajar
                        ));
                    }
                }
                rcMahasiswa.removeAllViews();
                setRecyclerView(list);
                btnTambahNilai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String spinNilai = spinnerNilai.getSelectedItem().toString();
                        String spinKe = spinnerKe.getSelectedItem().toString();
                        String nil = etNilai.getText().toString();
                        if (spinnerKe.getVisibility() == View.GONE) {
                            spinKe = "";
                        }
                        if (nil.isEmpty()) {
                            Snackbar snackbar = Snackbar
                                    .make(view, "Nilai tidak boleh Kosong ", Snackbar.LENGTH_LONG);

                            snackbar.show();
                        } else {
                            progressDialog = new ProgressDialog(TambahPenilaian.this);
                            progressDialog.setMessage("Mohon Tunggu...");
                            progressDialog.setTitle("Proses");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            if (spinnerKe.getVisibility() == View.GONE) {
                                tambahNilai(spinNilai, spinKe, Integer.parseInt(nil), list);
                            }
                            else {
                                int a = Integer.parseInt(spinKe);
                                a--;
                                tambahNilai(spinNilai, Integer.toString(a), Integer.parseInt(nil), list);
                            }
                        }
                    }
                });
            }
        });

        String etNm = etNama.getText().toString();
        if (etNm.isEmpty()){
            btnTambahNilai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String spinNilai = spinnerNilai.getSelectedItem().toString();
                    String spinKe = spinnerKe.getSelectedItem().toString();
                    String nil = etNilai.getText().toString();
                    if (spinnerKe.getVisibility() == View.GONE) {
                        spinKe = "";
                    }
                    if (nil.isEmpty()) {
                        Snackbar snackbar = Snackbar
                                .make(view, "Nilai tidak boleh Kosong ", Snackbar.LENGTH_LONG);

                        snackbar.show();
                    } else {
                        progressDialog = new ProgressDialog(TambahPenilaian.this);
                        progressDialog.setMessage("Mohon Tunggu...");
                        progressDialog.setTitle("Proses");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        if (spinnerKe.getVisibility() == View.GONE) {
                            tambahNilai(spinNilai, spinKe, Integer.parseInt(nil), listMhs);
                        }
                        else {
                            int a = Integer.parseInt(spinKe);
                            a--;
                            tambahNilai(spinNilai, Integer.toString(a), Integer.parseInt(nil), listMhs);
                        }
                    }
                }
            });
        }

        etNilai.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String et = etNilai.getText().toString();
                if (!et.isEmpty()){
                    if (Integer.parseInt(etNilai.getText().toString()) > 100){
                        etNilai.setText("100");
                    }
                    if (etPertemuan){
                        if (Integer.parseInt(etNilai.getText().toString()) > 1){
                            etNilai.setText("1");
                        }
                    }
                }

            }
        });

        spinnerNilai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSpinnerKe(spinnerNilai.getSelectedItem().toString());
                if (position >= 2) {
                    spinnerKe.setVisibility(View.GONE);
                } else {
                    spinnerKe.setVisibility(View.VISIBLE);
                }
                if (position == 1){
                    etPertemuan = true;
                }
                else {
                    etPertemuan = false;
                }
                if (etPertemuan){
                    String et = etNilai.getText().toString();
                    if (!et.isEmpty()){
                        if (Integer.parseInt(etNilai.getText().toString()) > 1){
                            etNilai.setText("1");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rlNama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcMahasiswa.setVisibility(View.VISIBLE);
                rlNilai.setVisibility(View.GONE);
                etNama.setVisibility(View.VISIBLE);
                imgDelete.setVisibility(View.GONE);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TambahPenilaian.this, KelasMHS.class));
                finish();
            }
        });
    }

    public void onDeleteClick(View v) {
        AlertDialog.Builder alert = new AlertDialog.Builder(TambahPenilaian.this);
        alert.setTitle("Delete");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().child("mahasiswa")
                        .child("kelas_" + listMhs.get(position).idKelas).child("id_" + listMhs.get(position).idMhs);
                db_node.removeValue();
                startActivity(new Intent(TambahPenilaian.this, KelasMHS.class));
                finish();
                Toast.makeText(TambahPenilaian.this, "Berhasil Menghapus", Toast.LENGTH_SHORT).show();
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

    private void tambahNilai(final String nilai, String ke, final int n, ArrayList<Mhs> dataMhs) {
        if (nilai.equals("Laporan")){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child("nilaiLaporan")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai + ke)
                    .child("idKelas")
                    .setValue(dataMhs.get(position).idKelas);
            database.child("nilaiLaporan")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai + ke)
                    .child("idMhs")
                    .setValue(dataMhs.get(position).idMhs);
            database.child("nilaiLaporan")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai + ke)
                    .child("ke")
                    .setValue(Integer.parseInt(ke));
            database.child("nilaiLaporan")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai + ke)
                    .child(nilai)
                    .setValue(n)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TambahPenilaian.this, "Berhasil Upload Data", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TambahPenilaian.this, KelasMHS.class));
                                finish();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG);

                                snackbar.show();
                            }
                        }
                    });
        }
        else if (nilai.equals("Pertemuan")){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child("nilaiPertemuan")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai + ke)
                    .child("idKelas")
                    .setValue(dataMhs.get(position).idKelas);
            database.child("nilaiPertemuan")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai + ke)
                    .child("idMhs")
                    .setValue(dataMhs.get(position).idMhs);
            database.child("nilaiPertemuan")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai + ke)
                    .child("ke")
                    .setValue(Integer.parseInt(ke));
            database.child("nilaiPertemuan")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai + ke)
                    .child(nilai)
                    .setValue(n)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TambahPenilaian.this, "Berhasil Upload Data", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TambahPenilaian.this, KelasMHS.class));
                                finish();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG);

                                snackbar.show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
        else if (nilai.equals("Quiz 1")){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child("nilaiQuiz1")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("idKelas" + ke)
                    .setValue(dataMhs.get(position).idKelas);
            database.child("nilaiQuiz1")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("idMhs" + ke)
                    .setValue(dataMhs.get(position).idMhs);
            database.child("nilaiQuiz1")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("Quiz1")
                    .setValue(n)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TambahPenilaian.this, "Berhasil Upload Data", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TambahPenilaian.this, KelasMHS.class));
                                finish();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG);

                                snackbar.show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
        else if (nilai.equals("Quiz 2")){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child("nilaiQuiz2")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("idKelas" + ke)
                    .setValue(dataMhs.get(position).idKelas);
            database.child("nilaiQuiz2")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("idMhs" + ke)
                    .setValue(dataMhs.get(position).idMhs);
            database.child("nilaiQuiz2")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("Quiz2")
                    .setValue(n)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TambahPenilaian.this, "Berhasil Upload Data", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TambahPenilaian.this, KelasMHS.class));
                                finish();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG);

                                snackbar.show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
        else if (nilai.equals("Tugas")){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child("nilaiTugas")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("idKelas" + ke)
                    .setValue(dataMhs.get(position).idKelas);
            database.child("nilaiTugas")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("idMhs" + ke)
                    .setValue(dataMhs.get(position).idMhs);
            database.child("nilaiTugas")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai)
                    .setValue(n)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TambahPenilaian.this, "Berhasil Upload Data", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TambahPenilaian.this, KelasMHS.class));
                                finish();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG);

                                snackbar.show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
        else if (nilai.equals("Mid")){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child("nilaiMid")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("idKelas" + ke)
                    .setValue(dataMhs.get(position).idKelas);
            database.child("nilaiMid")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("idMhs" + ke)
                    .setValue(dataMhs.get(position).idMhs);
            database.child("nilaiMid")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai)
                    .setValue(n)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TambahPenilaian.this, "Berhasil Upload Data", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TambahPenilaian.this, KelasMHS.class));
                                finish();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG);

                                snackbar.show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
        else if (nilai.equals("Final")){
            DatabaseReference database = FirebaseDatabase.getInstance().getReference();
            database.child("nilaiFinal")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("idKelas" + ke)
                    .setValue(dataMhs.get(position).idKelas);
            database.child("nilaiFinal")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child("idMhs" + ke)
                    .setValue(dataMhs.get(position).idMhs);
            database.child("nilaiFinal")
                    .child("kelas_" + dataMhs.get(position).idKelas)
                    .child("id_" + dataMhs.get(position).idMhs)
                    .child(nilai)
                    .setValue(n)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(TambahPenilaian.this, "Berhasil Upload Data", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(TambahPenilaian.this, KelasMHS.class));
                                finish();
                            } else {
                                Snackbar snackbar = Snackbar
                                        .make(view, "Gagal Upload Data", Snackbar.LENGTH_LONG);

                                snackbar.show();
                                progressDialog.dismiss();
                            }
                        }
                    });
        }
    }

    private void setRecyclerView(ArrayList<Mhs> list) {
        RecyclerMhs adapterAgenda = new RecyclerMhs(list);
        RecyclerView.LayoutManager layoutAgenda = new LinearLayoutManager(TambahPenilaian.this, LinearLayoutManager.VERTICAL, false);
        rcMahasiswa.setLayoutManager(layoutAgenda);
        rcMahasiswa.setNestedScrollingEnabled(false);
        rcMahasiswa.setAdapter(adapterAgenda);

        ItemClickSupport.addTo(rcMahasiswa)
                .setOnItemClickListener(this);

        if (listMhs.isEmpty()){
            textNothing.setVisibility(View.VISIBLE);
        }
    }

    private void setSpinnerNilai() {
        ArrayList<String> listNilai = new ArrayList<String>();
        listNilai.add("Laporan");
        listNilai.add("Pertemuan");
        listNilai.add("Quiz 1");
        listNilai.add("Quiz 2");
        listNilai.add("Tugas");
        listNilai.add("Mid");
        listNilai.add("Final");

        ArrayAdapter<String> dataNilai = new ArrayAdapter<String>(TambahPenilaian.this,
                R.layout.spinner_nilai, listNilai);
        spinnerNilai.setAdapter(dataNilai);
    }

    private void setSpinnerKe(String nilai) {
        if (nilai.equals("Pertemuan")){
            ArrayList<String> listKe = new ArrayList<String>();

            listKe.add("1");
            listKe.add("2");
            listKe.add("3");
            listKe.add("4");
            listKe.add("5");
            listKe.add("6");
            listKe.add("7");
            listKe.add("8");
            listKe.add("9");
            listKe.add("10");
            ArrayAdapter<String> dataKe = new ArrayAdapter<String>(TambahPenilaian.this,
                    R.layout.spinner_ke, listKe);
            spinnerKe.setAdapter(dataKe);
        }
        else if (nilai.equals("Laporan")){
            ArrayList<String> listKe = new ArrayList<String>();

            listKe.add("1");
            listKe.add("2");
            listKe.add("3");
            listKe.add("4");
            listKe.add("5");
            listKe.add("6");
            listKe.add("7");
            listKe.add("8");
            ArrayAdapter<String> dataKe = new ArrayAdapter<String>(TambahPenilaian.this,
                    R.layout.spinner_ke, listKe);
            spinnerKe.setAdapter(dataKe);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TambahPenilaian.this, KelasMHS.class));
        finish();
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        rcMahasiswa.setVisibility(View.GONE);
        rlNilai.setVisibility(View.VISIBLE);
        etNama.setVisibility(View.GONE);
        if (recyclerPosition){
            textNama.setText("Nama  : " + listMhs.get(position).namaMhs);
            textNim.setText("NIM Mahasiswa     : " + listMhs.get(position).nimMhs);
        }
        else {
            textNama.setText("Nama Mahasiswa  : " + list.get(position).namaMhs);
            textNim.setText("NIM Mahasiswa     : " + list.get(position).nimMhs);
        }
        imgDelete.setVisibility(View.VISIBLE);
        this.position = position;
    }
}
