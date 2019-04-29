package com.exomatik.irfanrz.penilaian.RecyclerView;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exomatik.irfanrz.penilaian.DataModel.Kelas;
import com.exomatik.irfanrz.penilaian.DataModel.Mhs;
import com.exomatik.irfanrz.penilaian.R;

import java.util.ArrayList;
import java.util.Random;
import android.os.Handler;

/**
 * Created by IrfanRZ on 17/09/2018.
 */

public class RecyclerKelasAuth extends RecyclerView.Adapter<RecyclerKelasAuth.bidangViewHolder> {
    private ArrayList<Mhs> dataList;
    private Context context;
//    SwipeRefreshLayout swipeRefreshLayout;


    public RecyclerKelasAuth(ArrayList<Mhs> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
//        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public bidangViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_auth, parent, false);
        this.context = parent.getContext();
        return new bidangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(bidangViewHolder holder, int position) {
        holder.txtNama.setText(dataList.get(position).namaMhs);
        holder.txtDesc.setText(dataList.get(position).nimMhs);

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                refresh();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

//    private void refresh()
//    {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
////                movies.add(0,movies.get(new Random().nextInt(movies.size())));
//                dataList.add(0, dataList.get(new Random().nextInt(dataList.size())));
//
//                RecyclerKelasAuth.this.notifyDataSetChanged();
//
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        },3000);
//    }

    public class bidangViewHolder extends RecyclerView.ViewHolder{
        private TextView txtNama, txtDesc;

        public bidangViewHolder(View itemView) {
            super(itemView);
            txtNama = (TextView) itemView.findViewById(R.id.text_title);
            txtDesc = (TextView) itemView.findViewById(R.id.text_desc);
        }
    }
}
