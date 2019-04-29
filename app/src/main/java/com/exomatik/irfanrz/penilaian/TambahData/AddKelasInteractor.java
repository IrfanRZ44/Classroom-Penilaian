package com.exomatik.irfanrz.penilaian.TambahData;

import android.content.Context;
import android.support.annotation.NonNull;

import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IrfanRZ on 06/09/2018.
 */

public class AddKelasInteractor implements AddKelasContract.Interactor  {
    private AddKelasContract.OnKelasDatabaseListener mOnKelasDatabaseListener;
    private Context ctx;
    private int idKelas;

    public AddKelasInteractor(AddKelasContract.OnKelasDatabaseListener onKelasDatabaseListener) {
        this.mOnKelasDatabaseListener = onKelasDatabaseListener;
    }

    @Override
    public void addKelasToDatabase(final Context context, final Kelas kelas) {
        FirebaseDatabase.getInstance().getReference().child("kelas").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                ArrayList<Kelas> listKelas = new ArrayList<Kelas>();
                int id = 0;
                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Kelas data = (Kelas) ((DataSnapshot) dataDS).getValue(Kelas.class);
                        listKelas.add(new Kelas(data.namaKelas, data.pengajarKelas, data.uidPengajar, data.idKelas, data.descKelas
                        ));
                    }
                }
                id = listKelas.size();
                idKelas = id++;

                ctx = context;
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                Kelas data = new Kelas(kelas.namaKelas, kelas.pengajarKelas, kelas.uidPengajar, idKelas, kelas.descKelas
                );
                database.child("kelas")
                        .child(kelas.pengajarKelas)
                        .child("id_"+idKelas)
                        .setValue(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mOnKelasDatabaseListener.onSuccess("Succes");

                                } else {
                                    mOnKelasDatabaseListener.onFailure("Unable to add");
                                }
                            }
                        });
                }});
    }
}
