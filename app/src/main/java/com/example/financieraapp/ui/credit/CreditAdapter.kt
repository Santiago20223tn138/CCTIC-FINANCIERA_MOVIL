package com.example.financieraapp.ui.credit

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financieraapp.R
import com.example.financieraapp.data.models.credit.Credit

class CreditAdapter(var creditList:List<Credit> = emptyList(), private val onItemSelected:(Int, String) -> Unit, private val context: Context) :
RecyclerView.Adapter<CreditViewHolder>() {

    fun updateList(creditList: List<Credit>){
        this.creditList = creditList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreditViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CreditViewHolder(
            layoutInflater.inflate(R.layout.item_credit, parent, false)
        )
    }

    override fun getItemCount() = creditList.size

    override fun onBindViewHolder(holder: CreditViewHolder, position: Int) {
        holder.bind(creditList[position], onItemSelected, context)
    }
}