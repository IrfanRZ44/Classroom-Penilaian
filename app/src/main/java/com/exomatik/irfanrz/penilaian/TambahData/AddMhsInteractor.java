package com.exomatik.irfanrz.penilaian.TambahData;

import android.content.Context;
import android.support.annotation.NonNull;

import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by IrfanRZ on 06/09/2018.
 */

public class AddMhsInteractor implements AddMhsContract.Interactor  {
    private AddMhsContract.OnMhsDatabaseListener mOnMhsDatabaseListener;
    private Context ctx;

    public AddMhsInteractor(AddMhsContract.OnMhsDatabaseListener onMhsDatabaseListener) {
        this.mOnMhsDatabaseListener = onMhsDatabaseListener;
    }

    @Override
    public void addMhsToDatabase(final Context context, Mhs mhs) {
        ctx = context;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        Mhs data = new Mhs(mhs.idMhs, mhs.namaMhs, mhs.nimMhs, mhs.idKelas, mhs.uidPengajar
        );
        database.child("authentication")
                .child("kelas_"+mhs.idKelas)
                .child("id_"+mhs.idMhs)
                .setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mOnMhsDatabaseListener.onSuccess("Succes");

                        } else {
                            mOnMhsDatabaseListener.onFailure("Unable to add");
                        }
                    }
                });
    }
}
