package com.example.financieraapp.ui.client

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.financieraapp.data.models.client.Client
import com.example.financieraapp.databinding.ItemClientBinding

class ClientViewHolder (view: View): RecyclerView.ViewHolder(view){

    private val binding = ItemClientBinding.bind(view)

    fun bind(client: Client, onItemSelected:(Int) -> Unit, getCredits:(Int) -> Unit){
        binding.tvName.text = "${client.nombre} ${client.paterno} ${client.materno}"
        binding.tvNoClient.text = client.numero_cliente
        binding.ivAddCredit.setOnClickListener { onItemSelected(client.id) }
        binding.ivgetCredits.setOnClickListener { getCredits(client.id) }
    }
}