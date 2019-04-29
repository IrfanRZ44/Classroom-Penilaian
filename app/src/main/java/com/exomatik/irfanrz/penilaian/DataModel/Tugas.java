package com.exomatik.irfanrz.penilaian.DataModel;

/**
 * Created by IrfanRZ on 05/12/2018.
 */

public class Tugas {
    public String idMhs;
    public int idKelas;
    public int Tugas;

    public Tugas() {
    }

    public Tugas(String idMhs, int idKelas, int Tugas) {
        this.idMhs = idMhs;
        this.idKelas = idKelas;
        this.Tugas = Tugas;
    }
}
