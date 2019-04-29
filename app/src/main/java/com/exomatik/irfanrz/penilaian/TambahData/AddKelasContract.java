package com.exomatik.irfanrz.penilaian.TambahData;

import android.content.Context;

import com.exomatik.irfanrz.penilaian.DataModel.Kelas;

/**
 * Created by IrfanRZ on 06/09/2018.
 */

public interface AddKelasContract {
    interface View {
        void onAddKelasSuccess(String message);

        void onAddKelasFailure(String message);
    }

    interface Presenter {
        void addKelas(Context context, Kelas modelDataAngkatan);
    }

    interface Interactor {
        void addKelasToDatabase(Context context, Kelas modelDataAngkatan);
    }

    interface OnKelasDatabaseListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
