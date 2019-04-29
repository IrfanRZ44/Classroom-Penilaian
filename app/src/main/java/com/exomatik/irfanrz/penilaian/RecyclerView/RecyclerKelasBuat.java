package com.exomatik.irfanrz.penilaian.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.R;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 17/09/2018.
 */

public class RecyclerKelasBuat extends RecyclerView.Adapter<RecyclerKelasBuat.bidangViewHolder> {
    private ArrayList<Kelas> dataList;
    private Context context;

    public RecyclerKelasBuat(ArrayList<Kelas> dataList) {
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
        holder.img.setBackgroundResource(R.drawable.tambah_class);
        holder.txtNama.setText(dataList.get(position).namaKelas);
        holder.txtDesc.setText(dataList.get(position).descKelas);
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
