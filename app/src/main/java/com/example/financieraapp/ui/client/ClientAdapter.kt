package com.example.financieraapp.ui.client

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.financieraapp.R
import com.example.financieraapp.data.models.client.Client
import com.example.financieraapp.data.models.client.ClientEntity

class ClientAdapter(var clientList: List<Client> = emptyList(), private val onItemSelected:(Int) -> Unit, private val getCredits:(Int) -> Unit) :
RecyclerView.Adapter<ClientViewHolder>() {

    fun updateList(clientList: List<Client>){
        this.clientList = clientList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ClientViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_client,parent, false)
        )
    }

    override fun getItemCount() = clientList.size

    override fun onBindViewHolder(holder: ClientViewHolder, position: Int) {
       holder.bind(clientList[position], onItemSelected, getCredits)
    }


}