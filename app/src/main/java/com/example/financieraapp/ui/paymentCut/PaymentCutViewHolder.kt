package com.example.financieraapp.ui.paymentCut

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.financieraapp.data.models.paymentCut.CortepagoDetail
import com.example.financieraapp.data.models.paymentCut.PaymentInfo
import com.example.financieraapp.databinding.ItemClientPaymentBinding
import java.text.NumberFormat
import java.util.Locale

class PaymentCutViewHolder(view: View):RecyclerView.ViewHolder(view) {

    private val binding = ItemClientPaymentBinding.bind(view)
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)

    fun bind(corteDetail : CortepagoDetail){
        binding.tvContract.text = "${corteDetail.idcredito.id} - ${corteDetail.idcredito.contrato}"
        binding.tvClient.text = "${corteDetail.idcredito.cliente.nombre} ${corteDetail.idcredito.cliente.paterno} ${corteDetail.idcredito.cliente.materno}"
        binding.tvMonto.text = if (corteDetail.id_hisotrico != null) formatter.format(corteDetail.id_hisotrico.monto) else "Sin pago"
    }
}