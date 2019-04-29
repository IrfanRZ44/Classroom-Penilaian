package com.exomatik.irfanrz.penilaian.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.exomatik.irfanrz.penilaian.DataModel.User;
import com.exomatik.irfanrz.penilaian.TambahData.AddMhsContract;
import com.exomatik.irfanrz.penilaian.TambahData.AddMhsPresenter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Iterator;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class ScanQR
        extends AppCompatActivity
        implements ZXingScannerView.ResultHandler, AddMhsContract.View  {
    private static final int REQUEST_CAMERA = 1;
    private static int camId = 0;
    private ProgressDialog progressDialog = null, progressDialog2 = null;
    private ZXingScannerView scannerView;
    public static User dataUser;
    private static ArrayList<Kelas> listKelas = new ArrayList<Kelas>();
    private AddMhsPresenter mAddMhsPresenter;
    private View view;

    private void showMessageOKCancel(String paramString, DialogInterface.OnClickListener paramOnClickListener) {
        new AlertDialog.Builder(this).setMessage(paramString).setPositiveButton("OK", paramOnClickListener).create().show();
    }

    private void getAllKelas(final Context context) {
        FirebaseDatabase.getInstance().getReference().child("kelas").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled(DatabaseError paramAnonymousDatabaseError) {
            }

            public void onDataChange(DataSnapshot paramAnonymousDataSnapshot) {
                listKelas = new ArrayList<Kelas>();
                Iterator localIterator1 = paramAnonymousDataSnapshot.getChildren().iterator();
                while (localIterator1.hasNext()) {
                    DataSnapshot localDataSnapshot = (DataSnapshot) localIterator1.next();
                    Iterator local = localDataSnapshot.getChildren().iterator();
                    while (local.hasNext()) {
                        DataSnapshot dataDS = (DataSnapshot) local.next();
                        Kelas data = (Kelas) ((DataSnapshot) dataDS).getValue(com.exomatik.irfanrz.penilaian.DataModel.Kelas.class);
                        listKelas.add(new Kelas(data.namaKelas, data.pengajarKelas, data.uidPengajar, data.idKelas, data.descKelas
                        ));
                    }
                }
                progressDialog.dismiss();

                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkPermission()) {
                        Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
                        scannerView.startCamera();
                        scannerView.setResultHandler(ScanQR.this);
                    } else {
                        requestPermission();
                    }
                } else {
                    requestPermission();
                }
            }
        });
    }

    @Override
    public void handleResult(Result paramResult) {
        progressDialog2 = new ProgressDialog(ScanQR.this);
        progressDialog2.setMessage("Mohon Tunggu...");
        progressDialog2.setTitle("Proses");
        progressDialog2.setCancelable(false);
        progressDialog2.show();

        paramResult.getText();
        String[] result = paramResult.getText().toString().split("___");
        cekQrCode(Integer.parseInt(result[0]), result[1]);

        progressDialog2.dismiss();
        Log.d("QRCodeScanner", paramResult.getText());
        Log.d("QRCodeScanner", paramResult.getBarcodeFormat().toString());
    }

    private void cekQrCode(int idKelas, String uidPengajar) {
        boolean kelasNone = false;
        for (int a=0; a < listKelas.size(); a++){
            if ((listKelas.get(a).uidPengajar.equals(uidPengajar) && (listKelas.get(a).idKelas == idKelas))){
                kelasNone = true;
                final Mhs modelMhs = new Mhs(dataUser.idMhs,  dataUser.nama, dataUser.nimMhs
                        , listKelas.get(a).idKelas, listKelas.get(a).uidPengajar);
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(ScanQR.this);
                localBuilder.setTitle("Scan Result");
                localBuilder.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                        mAddMhsPresenter.addMhs(ScanQR.this, modelMhs);
                        progressDialog = new ProgressDialog(ScanQR.this);
                        progressDialog.setMessage("Mohon Tunggu...");
                        progressDialog.setTitle("Proses");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                    }
                });
                scannerView.setResultHandler(ScanQR.this);
                scannerView.startCamera();
                String kelas = "Kelas : " + listKelas.get(a).namaKelas;
                String pengajar = "Pengajar Kelas : " + listKelas.get(a).pengajarKelas;
                localBuilder.setMessage(kelas + "\n" + pengajar);
                localBuilder.create().show();
            }
        }
        if (kelasNone == false){
            progressDialog.dismiss();
            scannerView.setResultHandler(ScanQR.this);
            scannerView.startCamera();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, Menu.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        view = findViewById(android.R.id.content);
        mAddMhsPresenter = new AddMhsPresenter(this);
        progressDialog = new ProgressDialog(ScanQR.this);
        progressDialog.setMessage("Mohon Tunggu...");
        progressDialog.setTitle("Proses");
        progressDialog.setCancelable(false);
        progressDialog.show();

        getAllKelas(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.scannerView.stopCamera();
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                        scannerView.setResultHandler(this);
                        scannerView.startCamera();
                        return;
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
        } else {
            requestPermission();
        }
    }

    @Override
    public void onAddMhsSuccess(String message) {
        Toast.makeText(this, "Berhasil Kirim Data Mahasiswa", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ScanQR.this, Menu.class));
        finish();
    }

    @Override
    public void onAddMhsFailure(String message) {
        Snackbar snackbar = Snackbar
                .make(view, "Error = " + message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}