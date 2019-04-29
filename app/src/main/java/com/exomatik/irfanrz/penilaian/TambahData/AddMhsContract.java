package com.exomatik.irfanrz.penilaian.TambahData;

import android.content.Context;

import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.DataModel.Mhs;

/**
 * Created by IrfanRZ on 06/09/2018.
 */

public interface AddMhsContract {
    interface View {
        void onAddMhsSuccess(String message);

        void onAddMhsFailure(String message);
    }

    interface Presenter {
        void addMhs(Context context, Mhs mhs);
    }

    interface Interactor {
        void addMhsToDatabase(Context context, Mhs mhs);
    }

    interface OnMhsDatabaseListener {
        void onSuccess(String message);

        void onFailure(String message);
    }
}
