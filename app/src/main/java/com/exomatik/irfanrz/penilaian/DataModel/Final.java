package com.exomatik.irfanrz.penilaian.DataModel;

/**
 * Created by IrfanRZ on 05/12/2018.
 */

public class Final {
    public String idMhs;
    public int idKelas;
    public int Final;

    public Final() {
    }

    public Final(String idMhs, int idKelas, int Final) {
        this.idMhs = idMhs;
        this.idKelas = idKelas;
        this.Final = Final;
    }
}
