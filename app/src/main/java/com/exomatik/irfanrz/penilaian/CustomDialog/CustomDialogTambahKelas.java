package com.exomatik.irfanrz.penilaian.CustomDialog;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.exomatik.irfanrz.penilaian.Activity.Menu;
import com.exomatik.irfanrz.penilaian.Activity.ScanQR;
import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.DataModel.User;
import com.exomatik.irfanrz.penilaian.R;
import com.exomatik.irfanrz.penilaian.TambahData.AddKelasContract;
import com.exomatik.irfanrz.penilaian.TambahData.AddKelasPresenter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IrfanRZ on 03/11/2018.
 */

public class CustomDialogTambahKelas extends DialogFragment implements AddKelasContract.View {
    private Button customDialog_Dismiss, buttonTambah;
    private EditText etNama, etDesc;
    private AddKelasPresenter mAddKelasPresenter;
    private View v;
    public static String nama;
    public static Context context;
    int idKelas;

    public static CustomDialogTambahKelas newInstance() {
        return new CustomDialogTambahKelas();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_kelas, container, false);

        mAddKelasPresenter = new AddKelasPresenter(this);

        customDialog_Dismiss = (Button) dialogView.findViewById(R.id.dialog_dismiss);
        buttonTambah = (Button) dialogView.findViewById(R.id.dialog_tambah);
        etNama = (EditText) dialogView.findViewById(R.id.et_tambah);
        etDesc = (EditText) dialogView.findViewById(R.id.et_desc);

        v = dialogView;

        idKelas();

        customDialog_Dismiss.setOnClickListener(customDialog_DismissOnClickListener);

        return dialogView;
    }

    private void idKelas() {
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

                buttonTambah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = etNama.getText().toString();
                        String desc = etDesc.getText().toString();

                        if ((name.isEmpty()) || (desc.isEmpty())) {
                            Snackbar snackbar = Snackbar
                                    .make(v, "Mohon isi data", Snackbar.LENGTH_LONG);

                            snackbar.show();
                        } else {
                            Kelas modelDataAngkatan = new Kelas(etNama.getText().toString(), nama
                                    , FirebaseAuth.getInstance().getCurrentUser().getUid(), idKelas, desc);
                            mAddKelasPresenter.addKelas(getActivity(), modelDataAngkatan);
                        }
                    }
                });

            }
        });
    }
    private void getData(){
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
                    }
                }
                dismiss();
                Menu.getKelasBuat(context, dataUser);
            }
        });
    }

    private Button.OnClickListener customDialog_DismissOnClickListener
            = new Button.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            dismiss();
        }
    };

    @Override
    public void onAddKelasSuccess(String message) {
        getData();
    }

    @Override
    public void onAddKelasFailure(String message) {
        dismiss();
    }
}
