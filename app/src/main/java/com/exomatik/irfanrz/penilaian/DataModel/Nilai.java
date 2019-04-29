package com.exomatik.irfanrz.penilaian.DataModel;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 04/12/2018.
 */

public class Nilai {
    public ArrayList<Laporan> Laporan;
    public ArrayList<Pertemuan> Pertemuan;
    public ArrayList<Quiz1> Quiz1;
    public ArrayList<Quiz2> Quiz2;
    public ArrayList<Mid> Mid;
    public ArrayList<Final> Final;

    public Nilai() {
    }

    public Nilai(ArrayList<Laporan> Laporan, ArrayList<Pertemuan> Pertemuan, ArrayList<Quiz1> Quiz1
            , ArrayList<Quiz2> Quiz2, ArrayList<Mid> Mid, ArrayList<Final> Final) {
        this.Laporan = Laporan;
        this.Pertemuan = Pertemuan;
        this.Quiz1 = Quiz1;
        this.Quiz2 = Quiz2;
        this.Mid = Mid;
        this.Final = Final;
    }
}
