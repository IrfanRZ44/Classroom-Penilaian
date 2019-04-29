package com.exomatik.irfanrz.penilaian.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.exomatik.irfanrz.penilaian.FirebaseClass.ItemClickSupport;
import com.exomatik.irfanrz.penilaian.R;
import com.exomatik.irfanrz.penilaian.RecyclerView.RecyclerKelasAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Iterator;

public class GenerateQr
        extends AppCompatActivity implements ItemClickSupport.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ImageView imageView;
    private ImageView imgBack;
    private TextView textTitel;
    private RecyclerView rcAuth;
    private View view;
    private ArrayList<Mhs> listAuth = new ArrayList<Mhs>();
    private RecyclerKelasAuth adapterAgenda;
    private SwipeRefreshLayout refresh;
    public static Kelas dataKelas;

    public void onBackPressed() {
        startActivity(new Intent(this, KelasMHS.class));
        finish();
    }
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        setContentView(R.layout.activity_generate_qr);
        view = findViewById(android.R.id.content);

        imageView = ((ImageView) findViewById(R.id.imageQR));
        imgBack = ((ImageView) findViewById(R.id.back));
        textTitel = (TextView) findViewById(R.id.titleGenerate);
        rcAuth = (RecyclerView) findViewById(R.id.rc_list_accept);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        rcAuth.setItemAnimator(new DefaultItemAnimator());
        refresh.setOnRefreshListener(this);

        textTitel.setText("Kelas " + dataKelas.namaKelas);

        getAuthUser();

        btn_generate();
        imgBack.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {

                Intent localIntent = new Intent(GenerateQr.this, KelasMHS.class);
                startActivity(localIntent);
                finish();
            }
        });
    }

    private void getAuthUser() {
        FirebaseDatabase.getInstance().getReference().child("authentication").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                listAuth = new ArrayList<Mhs>();
                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Mhs data = (Mhs) ((DataSnapshot) dataDS).getValue(Mhs.class);
                        if ((data.uidPengajar.equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())) &&
                                data.idKelas == dataKelas.idKelas) {
                            listAuth.add(new Mhs(data.idMhs, data.namaMhs, data.nimMhs, data.idKelas, data.uidPengajar));
                        }
                    }
                }

                if (!listAuth.isEmpty()) {
                    setRecyclerView(listAuth);
                }
            }
        });
    }

    private void setRecyclerView(ArrayList<Mhs> list){
        adapterAgenda = new RecyclerKelasAuth(list, getApplicationContext());

        RecyclerView.LayoutManager layoutAgenda = new LinearLayoutManager(GenerateQr.this);
        rcAuth.setLayoutManager(layoutAgenda);
        rcAuth.setAdapter(adapterAgenda);
        ItemClickSupport.addTo(rcAuth)
                .setOnItemClickListener(this);
    }

    private void btn_generate() {
        String str = Integer.toString(dataKelas.idKelas) + "___"+dataKelas.uidPengajar;
        MultiFormatWriter localMultiFormatWriter = new MultiFormatWriter();

        try {
            BitMatrix localBitMatrix = localMultiFormatWriter.encode(str, BarcodeFormat.QR_CODE, 200, 200);
            Bitmap localBitmap = new BarcodeEncoder().createBitmap(localBitMatrix);
            imageView.setImageBitmap(localBitmap);

            return;
        } catch (Exception localException) {
            Toast.makeText(GenerateQr.this, "Error " + localException, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        sendData(new Mhs(listAuth.get(position).idMhs, listAuth.get(position).namaMhs, listAuth.get(position).nimMhs
                    , listAuth.get(position).idKelas, listAuth.get(position).uidPengajar
        ));
        Snackbar snackbar = Snackbar
                .make(v, "Berhasil Tambah Mahasiswa", Snackbar.LENGTH_LONG);

        snackbar.show();
        refresh.setRefreshing(true);
    }

    private void sendData(Mhs mhs){
        DatabaseReference db_node = FirebaseDatabase.getInstance().getReference().child("authentication")
                .child("kelas_" + mhs.idKelas).child("id_"+mhs.idMhs);
        db_node.removeValue();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Mhs data = new Mhs(mhs.idMhs, mhs.namaMhs, mhs.nimMhs, mhs.idKelas, mhs.uidPengajar
        );
        database.child("mahasiswa")
                .child("kelas_"+mhs.idKelas)
                .child("id_"+mhs.idMhs)
                .setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar snackbar = Snackbar
                                    .make(view, "Mahasiswa Berhasil Di masukkan", Snackbar.LENGTH_LONG);

                            snackbar.show();
                            refresh.setRefreshing(true);
                            onRefresh();

                        } else {
                            Snackbar snackbar = Snackbar
                                    .make(view, "Gagal, periksa koneksi anda", Snackbar.LENGTH_LONG);

                            snackbar.show();
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        listAuth.removeAll(listAuth);
        rcAuth.removeAllViews();
        Toast.makeText(this, "Refresh", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getAuthUser();
                refresh.setRefreshing(false);
            }
        }, 2000);
    }
}