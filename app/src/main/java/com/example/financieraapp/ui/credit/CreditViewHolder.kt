package com.example.financieraapp.ui.credit

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.financieraapp.R
import com.example.financieraapp.data.models.credit.Credit
import com.example.financieraapp.databinding.ItemCreditBinding
import com.example.financieraapp.ui.payment.RegisterPaymentActivity
import java.text.SimpleDateFormat
import java.util.Locale

class CreditViewHolder (view: View):RecyclerView.ViewHolder(view) {

    private val binding = ItemCreditBinding.bind(view)

    fun bind(credit: Credit, onItemSelected: (Int, String) -> Unit, context: Context){
        binding.tvContrato.text = credit.contrato
        binding.tvFolio.text= credit.folio
        binding.tvDate.text = formatDate(credit.fecha)
        binding.tvAmount.text = "$${credit.monto}"
        binding.btnPay.setOnClickListener { showPopup(context, credit) }
        binding.btnDownload.setOnClickListener { onItemSelected(credit.id, credit.contrato) }
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyMMdd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }

    private fun showPopup(context: Context, credit: Credit) {
        val popup = PopupMenu(context, binding.btnPay)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.semanal -> {
                    val intent = Intent(context, RegisterPaymentActivity::class.java)
                    intent.putExtra("idCredito", credit.id)
                    intent.putExtra("semanal", credit.pago_semanal)
                    intent.putExtra("client", "${credit.cliente.nombre} ${credit.cliente.materno} ${credit.cliente.paterno}")
                    context.startActivity(intent)
                    true
                }
                R.id.inicial -> {
                    val intent = Intent(context, RegisterPaymentActivity::class.java)
                    intent.putExtra("idCredito", credit.id)
                    intent.putExtra("inicial", credit.pago_inicial)
                    intent.putExtra("client", "${credit.cliente.nombre} ${credit.cliente.materno} ${credit.cliente.paterno}")
                    context.startActivity(intent)
                    true
                }
                else -> false
            }
        }
        popup.inflate(R.menu.popup_menu_pay)
        popup.show()
    }
}