package com.exomatik.irfanrz.penilaian.Activity;

import android.os.Bundle;
import android.view.View;

/**
 * Created by IrfanRZ on 08/12/2018.
 */

public class KelasMasukMhs extends KelasMHS {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rlExport.setVisibility(View.GONE);
        rlPenilaian.setVisibility(View.GONE);
        rlQr.setVisibility(View.GONE);
        imgHapus.setVisibility(View.GONE);

        shimmerLoad.startShimmerAnimation();
        shimmerLoad2.startShimmerAnimation();
        context = getApplicationContext();
        textKelas.setText(dataKelas.namaKelas);
        textDesc.setText(dataKelas.descKelas);
//        act = false;
    }
}
