package com.exomatik.irfanrz.penilaian.DataModel;

/**
 * Created by IrfanRZ on 02/12/2018.
 */

public class Kelas {
    public String namaKelas;
    public String pengajarKelas;
    public String uidPengajar;
    public int idKelas;
    public String descKelas;

    public Kelas() {
    }

    public Kelas(String namaKelas, String pengajarKelas, String uidPengajar, int idKelas, String descKelas) {
        this.namaKelas = namaKelas;
        this.pengajarKelas = pengajarKelas;
        this.uidPengajar = uidPengajar;
        this.idKelas = idKelas;
        this.descKelas = descKelas;
    }
}
