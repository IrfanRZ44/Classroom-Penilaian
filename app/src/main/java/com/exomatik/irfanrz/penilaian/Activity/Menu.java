package com.exomatik.irfanrz.penilaian.Activity;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.exomatik.irfanrz.penilaian.Authentication.login.loginUser;
import com.exomatik.irfanrz.penilaian.CustomDialog.CustomDialogTambahKelas;
import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.exomatik.irfanrz.penilaian.DataModel.User;
import com.exomatik.irfanrz.penilaian.FirebaseClass.ItemClickSupport;
import com.exomatik.irfanrz.penilaian.R;
import com.exomatik.irfanrz.penilaian.RecyclerView.RecyclerKelasBuat;
import com.exomatik.irfanrz.penilaian.RecyclerView.RecyclerKelasMasuk;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class Menu extends AppCompatActivity implements ItemClickSupport.OnItemClickListener{
    private static RecyclerView rcKelasBuat, rcKelasMasuk;
    private static RelativeLayout rlDaftarMilikKelas, rlDaftarMasukKelas, rlBtnBuat, rlBtnMasuk, rlMain;
    private TextView textNama;
    private static TextView textJumlahKelas, textMasukKelas, textGagal;
    private boolean exit = false;
    private ImageView imgLogout;
    private static ArrayList<Kelas> listKelasBuat = new ArrayList<Kelas>();
    private static ArrayList<Mhs> listKelasMasuk = new ArrayList<Mhs>();
    private static ArrayList<Kelas> dataKelas = new ArrayList<Kelas>();
    private ShimmerLayout shimmerLoad, shimmerLoad2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        rcKelasBuat = (RecyclerView) findViewById(R.id.rc_kelas);
        rcKelasMasuk = (RecyclerView) findViewById(R.id.rc_kelas2);
        rlMain = (RelativeLayout) findViewById(R.id.rl_main);
        rlDaftarMilikKelas = (RelativeLayout) findViewById(R.id.rl_kelas);
        rlDaftarMasukKelas = (RelativeLayout) findViewById(R.id.rl_kelas2);
        rlBtnBuat = (RelativeLayout) findViewById(R.id.rl_jumlah_milik);
        rlBtnMasuk = (RelativeLayout) findViewById(R.id.rl_jumlah_masuk);
        textNama = (TextView) findViewById(R.id.et_nama);
        textJumlahKelas = (TextView) findViewById(R.id.et_jumlah);
        textMasukKelas = (TextView) findViewById(R.id.et_jumlah2);
        textGagal = (TextView) findViewById(R.id.text_gagal);
        imgLogout = (ImageView) findViewById(R.id.img_logout);
        shimmerLoad = (ShimmerLayout) findViewById(R.id.shimmer_text2);
        shimmerLoad2 = (ShimmerLayout) findViewById(R.id.shimmer_text);

        shimmerLoad.startShimmerAnimation();
        shimmerLoad2.startShimmerAnimation();
        getData(getApplicationContext());
        ItemClickSupport.addTo(rcKelasBuat)
                .setOnItemClickListener(this);
        ItemClickSupport.addTo(rcKelasMasuk)
                .setOnItemClickListener(this);
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar
                        .make(v, "Berhasil Keluar", Snackbar.LENGTH_LONG);

                snackbar.show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Menu.this, loginUser.class));
                finish();
            }
        });

        rlBtnBuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = CustomDialogTambahKelas
                        .newInstance();

                newFragment.setCancelable(false);
                CustomDialogTambahKelas.context = getApplicationContext();
                CustomDialogTambahKelas.nama = textNama.getText().toString();
                newFragment.show(getFragmentManager(), "dialog");
            }
        });
    }

    public static void getKelasBuat(final Context context, final User dataUser){
        FirebaseDatabase.getInstance().getReference().child("kelas").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }
            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                listKelasBuat = new ArrayList<Kelas>();
                ArrayList<Kelas> listAllKelas = new ArrayList<Kelas>();
                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()){
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Kelas data = (Kelas) ((DataSnapshot) dataDS).getValue(com.exomatik.irfanrz.penilaian.DataModel.Kelas.class);
                        if (data.uidPengajar.equals(dataUser.uid)) {
                            listKelasBuat.add(new Kelas(data.namaKelas, data.pengajarKelas, data.uidPengajar, data.idKelas, data.descKelas
                            ));
                        }
                        listAllKelas.add(new Kelas(data.namaKelas, data.pengajarKelas, data.uidPengajar, data.idKelas, data.descKelas));
                    }
                }

                if (!listKelasBuat.isEmpty()){
                    rlDaftarMilikKelas.setVisibility(View.VISIBLE);
                    if (listKelasBuat.size() == 0){
                        textJumlahKelas.setText("0");
                    }else {
                        textJumlahKelas.setText(Integer.toString(listKelasBuat.size()));
                    }
                    RecyclerKelasBuat adapterAgenda = new RecyclerKelasBuat(listKelasBuat);
                    RecyclerView.LayoutManager layoutAgenda = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    rcKelasBuat.setLayoutManager(layoutAgenda);
                    rcKelasBuat.setNestedScrollingEnabled(false);
                    rcKelasBuat.setAdapter(adapterAgenda);
                }
                if (rlDaftarMilikKelas.getVisibility() == View.GONE){
                    textJumlahKelas.setText("0");
                }
                dataKelas = listAllKelas;
                getKelasMasuk(context, dataUser, listAllKelas);
            }
        });
    }
    private static void getKelasMasuk(final Context context, final User dataUser, final ArrayList<Kelas> listAllKelas){
        FirebaseDatabase.getInstance().getReference().child("mahasiswa").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }
            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                listKelasMasuk = new ArrayList<Mhs>();
                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()){
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Mhs data = (Mhs) ((DataSnapshot) dataDS).getValue(Mhs.class);
                        if (data.idMhs.equals(dataUser.idMhs)){
                            listKelasMasuk.add(new Mhs(data.idMhs, data.namaMhs, data.nimMhs, data.idKelas, data.uidPengajar));
                        }
                    }
                }


                if (!listKelasMasuk.isEmpty()){
                    rlDaftarMasukKelas.setVisibility(View.VISIBLE);
                    if (listKelasMasuk.size() == 0){
                        textMasukKelas.setText("0");
                    }else {
                        textMasukKelas.setText(Integer.toString(listKelasMasuk.size()));
                    }
                    RecyclerKelasMasuk.listKelasBuat = listAllKelas;
                    RecyclerKelasMasuk adapterAgenda = new RecyclerKelasMasuk(listKelasMasuk);
                    RecyclerView.LayoutManager layoutAgenda = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                    rcKelasMasuk.setLayoutManager(layoutAgenda);
                    rcKelasMasuk.setNestedScrollingEnabled(false);
                    rcKelasMasuk.setAdapter(adapterAgenda);
                }
                if (rlDaftarMasukKelas.getVisibility() == View.GONE){
                    textMasukKelas.setText("0");
                }
            }
        });
    }

    private void getData(final Context context){
        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                Iterator localIterator = paramAnonymousDataSnapshot.getChildren().iterator();
                User dataUser = null;
                while (localIterator.hasNext()) {
                    final User localUser = (User) ((DataSnapshot) localIterator.next()).getValue(User.class);
                    if (localUser.uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())) {
                        dataUser = new User(localUser.uid, localUser.email, localUser.firebaseToken, localUser.typeUser
                                    , localUser.nama, localUser.nimMhs, localUser.idMhs
                        );
                        textNama.setText(localUser.nama);
                        shimmerLoad.stopShimmerAnimation();
                        shimmerLoad2.stopShimmerAnimation();
                        shimmerLoad.setVisibility(View.GONE);
                        shimmerLoad2.setVisibility(View.GONE);

                        rlMain.setVisibility(View.VISIBLE);
                        rlBtnMasuk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ScanQR.dataUser = new User(localUser.uid, localUser.email, localUser.firebaseToken
                                        , localUser.typeUser, localUser.nama, localUser.nimMhs, localUser.idMhs
                                );
                                startActivity(new Intent(Menu.this, ScanQR.class));
                                finish();
                            }
                        });

                    }
                }
                String nm = textNama.getText().toString();
                if (nm.isEmpty()){
                    textGagal.setVisibility(View.VISIBLE);
                }
                getKelasBuat(context, dataUser);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
            return;
        } else {
            Toast toast = Toast.makeText(Menu.this, "Tekan Cepat 2 Kali untuk Keluar", Toast.LENGTH_SHORT);
            toast.show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 2000);
        }
    }

    @Override
    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
        if (recyclerView == rcKelasMasuk){
            for (int kls=0; kls < dataKelas.size(); kls++){
                if (dataKelas.get(kls).idKelas == listKelasMasuk.get(position).idKelas){
                    KelasMHS.dataKelas = new Kelas(dataKelas.get(kls).namaKelas, dataKelas.get(kls).pengajarKelas, dataKelas.get(kls).uidPengajar
                                , dataKelas.get(kls).idKelas, dataKelas.get(kls).descKelas
                    );
                    startActivity(new Intent(Menu.this, KelasMasukMhs.class));
                    finish();
                }
            }
        }
        else if (recyclerView == rcKelasBuat){
            KelasMHS.dataKelas = new Kelas(listKelasBuat.get(position).namaKelas, listKelasBuat.get(position).pengajarKelas,
                    listKelasBuat.get(position).uidPengajar, listKelasBuat.get(position).idKelas, listKelasBuat.get(position).descKelas
            );
            startActivity(new Intent(Menu.this, KelasMHS.class));
            finish();
        }
    }
}
