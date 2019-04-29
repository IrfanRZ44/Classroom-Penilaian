package com.exomatik.irfanrz.penilaian.DataModel;

/**
 * Created by IrfanRZ on 04/12/2018.
 */

public class Mhs {
    public String idMhs;
    public String namaMhs;
    public String nimMhs;
    public int idKelas;
    public String uidPengajar;

    public Mhs() {
    }

    public Mhs(String idMhs, String namaMhs, String nimMhs, int idKelas, String uidPengajar  ) {
        this.idMhs = idMhs;
        this.namaMhs = namaMhs;
        this.nimMhs = nimMhs;
        this.idKelas = idKelas;
        this.uidPengajar = uidPengajar;
    }
}
