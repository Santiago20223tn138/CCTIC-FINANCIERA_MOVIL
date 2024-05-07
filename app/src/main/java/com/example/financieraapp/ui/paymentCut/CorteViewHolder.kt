package com.example.financieraapp.ui.paymentCut

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.financieraapp.data.models.paymentCut.DataPaymentCut
import com.example.financieraapp.databinding.ItemCorteBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class CorteViewHolder(view:View) : RecyclerView.ViewHolder(view) {

    private val binding = ItemCorteBinding.bind(view)
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)

    fun bind(paymentCut : DataPaymentCut, onItemSelected:(Int) -> Unit){
        binding.tvAmount.text = formatter.format(paymentCut.monto)
        binding.tvDate.text = formatDate(paymentCut.fecha)
        binding.btnInfo.setOnClickListener { onItemSelected(paymentCut.id) }
    }
    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        val outputFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date)
    }
}