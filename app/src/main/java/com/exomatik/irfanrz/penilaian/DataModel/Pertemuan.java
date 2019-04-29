package com.exomatik.irfanrz.penilaian.DataModel;

/**
 * Created by IrfanRZ on 04/12/2018.
 */

public class Pertemuan {
    public String idMhs;
    public int idKelas;
    public int Pertemuan;
    public int ke;

    public Pertemuan() {
    }

    public Pertemuan(String idMhs, int idKelas, int Pertemuan, int ke) {
        this.idMhs = idMhs;
        this.idKelas = idKelas;
        this.Pertemuan = Pertemuan;
        this.ke = ke;
    }
}
