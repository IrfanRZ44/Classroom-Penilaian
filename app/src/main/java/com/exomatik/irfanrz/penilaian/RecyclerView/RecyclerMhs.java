package com.exomatik.irfanrz.penilaian.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.exomatik.irfanrz.penilaian.R;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 17/09/2018.
 */

public class RecyclerMhs extends RecyclerView.Adapter<RecyclerMhs.bidangViewHolder> {
    private ArrayList<Mhs> dataList;
    private Context context;

    public RecyclerMhs(ArrayList<Mhs> dataList) {
        this.dataList = dataList;
    }

    @Override
    public bidangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_mhs, parent, false);
        this.context = parent.getContext();
        return new bidangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(bidangViewHolder holder, int position) {
        holder.txtNama.setText(dataList.get(position).namaMhs);
        holder.txtNim.setText(dataList.get(position).nimMhs);
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class bidangViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNama, txtNim;

        public bidangViewHolder(View itemView) {
            super(itemView);
            txtNama = (TextView) itemView.findViewById(R.id.text_title);
            txtNim = (TextView) itemView.findViewById(R.id.text_desc);
        }
    }
}
