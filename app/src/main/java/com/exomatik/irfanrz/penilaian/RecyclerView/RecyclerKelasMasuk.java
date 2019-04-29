package com.exomatik.irfanrz.penilaian.RecyclerView;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.exomatik.irfanrz.penilaian.DataModel.User;
import com.exomatik.irfanrz.penilaian.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by IrfanRZ on 17/09/2018.
 */

public class RecyclerKelasMasuk extends RecyclerView.Adapter<RecyclerKelasMasuk.bidangViewHolder> {
    private ArrayList<Mhs> dataList;
    private Context context;
    public static ArrayList<Kelas> listKelasBuat;

    public RecyclerKelasMasuk(ArrayList<Mhs> dataList) {
        this.dataList = dataList;
    }

    @Override
    public bidangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_kelas, parent, false);
        this.context = parent.getContext();
        return new bidangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(bidangViewHolder holder, int position) {
        for (int a = 0; a < listKelasBuat.size(); a++){
            if (dataList.get(position).idKelas == listKelasBuat.get(a).idKelas){
                holder.img.setBackgroundResource(R.drawable.auth);
                holder.txtNama.setText(listKelasBuat.get(a).namaKelas);
                holder.txtDesc.setText(listKelasBuat.get(a).pengajarKelas);
            }
        }
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNama, txtDesc;
        private ImageView img;

        public bidangViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.listImage);
            txtNama = (TextView) itemView.findViewById(R.id.text_title);
            txtDesc = (TextView) itemView.findViewById(R.id.text_desc);
        }
    }
}
