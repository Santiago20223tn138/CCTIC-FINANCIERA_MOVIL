package com.example.financieraapp.ui.payment

import android.content.Intent
import android.opengl.Visibility
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.financieraapp.R
import com.example.financieraapp.data.models.payment.Payment
import com.example.financieraapp.databinding.ItemWeeklyPaymentsBinding

class ChargesViewHolder (view: View): RecyclerView.ViewHolder(view){

    private val binding = ItemWeeklyPaymentsBinding.bind(view)

    fun bind(charge: Payment, goToPay : (Int, Int, String) -> Unit, cantPay:() -> Unit){
        binding.tvDay.text = charge.dia_pago
        binding.tvContract.text = "${charge.contrato_id} - ${charge.contrato}"
        binding.tvAddress.text = charge.domicilio
        binding.tvName.text = charge.cliente
        binding.tvPhone.text = charge.telefono_1
        binding.tvWeeklyPayment.text = "$${charge.pago_semanal}"
        binding.tvPendingPaymnet.text = "$${charge.pendiente}"
        binding.root.setOnClickListener {
            if (charge.isEs_fecha_valida == 1){
                cantPay()
            }else{
                goToPay(charge.contrato_id, charge.pago_semanal, charge.cliente)
            }
        }
        if (charge.isEs_fecha_valida != 1) binding.ivCheck.visibility = View.GONE
    }
}