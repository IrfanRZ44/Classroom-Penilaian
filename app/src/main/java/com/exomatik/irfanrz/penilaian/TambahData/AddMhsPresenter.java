package com.exomatik.irfanrz.penilaian.TambahData;

import android.content.Context;

import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.DataModel.Mhs;

/**
 * Created by IrfanRZ on 06/09/2018.
 */

public class AddMhsPresenter implements AddMhsContract.Presenter, AddMhsContract.OnMhsDatabaseListener{
    private AddMhsContract.View mView;
    private AddMhsInteractor mAddMhsInteractor;

    public AddMhsPresenter(AddMhsContract.View view) {
        this.mView = view;
        mAddMhsInteractor = new AddMhsInteractor(this);
    }

    @Override
    public void addMhs(Context context, Mhs mhs) {
        mAddMhsInteractor.addMhsToDatabase(context, mhs);
    }

    @Override
    public void onSuccess(String message) {
        mView.onAddMhsSuccess(message);
    }

    @Override
    public void onFailure(String message) {
        mView.onAddMhsFailure(message);
    }

}
