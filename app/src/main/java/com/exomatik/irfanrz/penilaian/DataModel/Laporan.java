package com.exomatik.irfanrz.penilaian.DataModel;

/**
 * Created by IrfanRZ on 05/12/2018.
 */

public class Laporan {
    public String idMhs;
    public int idKelas;
    public int Laporan;
    public int ke;

    public Laporan() {
    }

    public Laporan(String idMhs, int idKelas, int Laporan, int ke) {
        this.idMhs = idMhs;
        this.idKelas = idKelas;
        this.Laporan = Laporan;
        this.ke = ke;
    }
}
