package com.exomatik.irfanrz.penilaian.TambahData;

import android.content.Context;
import com.exomatik.irfanrz.penilaian.DataModel.Kelas;

/**
 * Created by IrfanRZ on 06/09/2018.
 */

public class AddKelasPresenter implements AddKelasContract.Presenter, AddKelasContract.OnKelasDatabaseListener{
    private AddKelasContract.View mView;
    private AddKelasInteractor mAddKelasInteractor;

    public AddKelasPresenter(AddKelasContract.View view) {
        this.mView = view;
        mAddKelasInteractor = new AddKelasInteractor(this);
    }

    @Override
    public void addKelas(Context context, Kelas modelDataAngkatan) {
        mAddKelasInteractor.addKelasToDatabase(context, modelDataAngkatan);
    }

    @Override
    public void onSuccess(String message) {
        mView.onAddKelasSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        mView.onAddKelasFailure(message);
    }

}
