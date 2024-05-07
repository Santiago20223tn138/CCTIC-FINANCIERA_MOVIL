package com.example.financieraapp.ui.paymentCut

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financieraapp.R
import com.example.financieraapp.data.models.paymentCut.CortepagoDetail
import com.example.financieraapp.data.models.paymentCut.PaymentInfo
import com.example.financieraapp.ui.credit.CreditViewHolder

class PaymentCutAdapter(var paymentInfo:List<CortepagoDetail> = emptyList()) :
    RecyclerView.Adapter<PaymentCutViewHolder>(){

    fun updateList(clientList: List<CortepagoDetail>){
        this.paymentInfo = clientList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentCutViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return PaymentCutViewHolder(
            layoutInflater.inflate(R.layout.item_client_payment, parent, false)
        )
    }

    override fun getItemCount() = paymentInfo.size

    override fun onBindViewHolder(holder: PaymentCutViewHolder, position: Int) {
        holder.bind(paymentInfo[position])
    }

}