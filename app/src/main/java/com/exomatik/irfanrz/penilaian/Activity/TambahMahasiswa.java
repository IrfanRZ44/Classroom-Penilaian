package com.exomatik.irfanrz.penilaian.Activity;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.exomatik.irfanrz.penilaian.R;
import com.exomatik.irfanrz.penilaian.TambahData.AddMhsContract;
import com.exomatik.irfanrz.penilaian.TambahData.AddMhsPresenter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class TambahMahasiswa extends AppCompatActivity implements AddMhsContract.View {
    private ImageView back;
    private EditText etNama, etNim, etKontak;
    private Button btnTambah;
    private AddMhsPresenter mAddMhsPresenter;
    View view;
    public static int idMhs;
    public static Kelas dataKelasMHS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_mahasiswa);

        back = (ImageView) findViewById(R.id.back);
        etNama = (EditText) findViewById(R.id.mhs_nama);
        etNim = (EditText) findViewById(R.id.mhs_nim);
        etKontak = (EditText) findViewById(R.id.mhs_kontak);
        btnTambah = (Button) findViewById(R.id.tambah_mhs);

        view = findViewById(android.R.id.content);
        mAddMhsPresenter = new AddMhsPresenter(this);

        idKelas();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TambahMahasiswa.this, KelasMHS.class));
                finish();
            }
        });
    }

    private void idKelas() {
        FirebaseDatabase.getInstance().getReference().child("mahasiswa").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                ArrayList<Mhs> listMhs = new ArrayList<Mhs>();
                int id = 0;
                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Mhs data = (Mhs) ((DataSnapshot) dataDS).getValue(Mhs.class);
                        listMhs.add(new Mhs(data.idMhs, data.namaMhs, data.nimMhs, data.idKelas, data.uidPengajar
                        ));
                    }
                }
                id = listMhs.size();
                idMhs = id++;

                btnTambah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = etNama.getText().toString();
                        String nim = etNim.getText().toString();

                        if ((name.isEmpty()) || (nim.isEmpty())) {
                            Snackbar snackbar = Snackbar
                                    .make(v, "Mohon isi data", Snackbar.LENGTH_LONG);

                            snackbar.show();
                        } else {
                            String idM = Integer.toString(idMhs);
                            Mhs modelMhs = new Mhs(idM,  name, nim, dataKelasMHS.idKelas, dataKelasMHS.uidPengajar);
                            mAddMhsPresenter.addMhs(TambahMahasiswa.this, modelMhs);
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TambahMahasiswa.this, KelasMHS.class));
        finish();
    }

    @Override
    public void onAddMhsSuccess(String message) {
        Snackbar snackbar = Snackbar
                .make(view, "Berhasil Input Data Mahasiswa", Snackbar.LENGTH_LONG);

        snackbar.show();
        finish();
    }

    @Override
    public void onAddMhsFailure(String message) {
        Snackbar snackbar = Snackbar
                .make(view, "Error = " + message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
