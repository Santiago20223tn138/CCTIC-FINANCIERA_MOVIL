package com.example.financieraapp.ui.payment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financieraapp.R
import com.example.financieraapp.data.models.payment.Payment

class ChargesAdapter(var chargesList: List<Payment> = emptyList(),
                     private val goToPay:(Int, Int, String) -> Unit, private val cantPay:() -> Unit) :
    RecyclerView.Adapter<ChargesViewHolder>() {

    fun updateList(chargesList: List<Payment>){
        this.chargesList = chargesList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChargesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ChargesViewHolder(
            layoutInflater.inflate(R.layout.item_weekly_payments, parent, false)
        )
    }

    override fun getItemCount() = chargesList.size

    override fun onBindViewHolder(holder: ChargesViewHolder, position: Int) {
        holder.bind(chargesList[position], goToPay, cantPay)
    }
}