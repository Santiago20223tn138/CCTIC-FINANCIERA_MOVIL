package com.example.financieraapp.ui.paymentCut

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financieraapp.R
import com.example.financieraapp.data.models.paymentCut.DataPaymentCut

class CorteAdapter(var paymentCut: List<DataPaymentCut> = emptyList(), private val onItemSelected:(Int) -> Unit) :
 RecyclerView.Adapter<CorteViewHolder>(){

    fun updateList(paymentCut: List<DataPaymentCut>){
        this.paymentCut = paymentCut
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CorteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CorteViewHolder(
            layoutInflater.inflate(R.layout.item_corte, parent, false)
        )
    }

    override fun getItemCount() = paymentCut.size

    override fun onBindViewHolder(holder: CorteViewHolder, position: Int) {
        holder.bind(paymentCut[position], onItemSelected)
    }
}