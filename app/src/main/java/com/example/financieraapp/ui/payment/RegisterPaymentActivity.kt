package com.example.financieraapp.ui.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.financieraapp.data.models.credit.CreditBody
import com.example.financieraapp.data.models.payment.PaymentBody
import com.example.financieraapp.data.models.payment.PaymentResponsePost
import com.example.financieraapp.data.models.payment.PutPayment
import com.example.financieraapp.databinding.ActivityRegisterPaymentBinding
import com.example.financieraapp.domain.repository.payment.PaymentRepository
import com.example.financieraapp.ui.core.LoadingDialog
import com.example.financieraapp.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterPaymentBinding
    val weekDays = listOf("LUNES", "MARTES", "MIÉRCOLES", "JUEVES", "VIERNES", "SÁBADO", "DOMINGO")
    var pagoInicial = 0
    var pagoSemanal = 0
    var idCredito = 0
    var client = ""
    var inicial : Boolean = false
    lateinit var paymentRepository: PaymentRepository
    val loadingDialog = LoadingDialog(this@RegisterPaymentActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idCredito = intent.getIntExtra("idCredito", 0)
        pagoInicial = intent.getIntExtra("inicial", 0)
        pagoSemanal = intent.getIntExtra("semanal", 0)
        client = intent.getStringExtra("client").toString()
        initUI()
    }

    private fun initUI(){
        paymentRepository = PaymentRepository(this)
        binding.tvNameClient.text = client
        if (pagoInicial != 0){
            val adapter = ArrayAdapter(this@RegisterPaymentActivity, android.R.layout.simple_list_item_1, weekDays)
            binding.spinnerDaysOfWeek.adapter = adapter
            binding.tvPayType.text = "Pago inicial"
            binding.tvAmount.text = "$$pagoInicial.00"
            inicial = true
        }else{
            binding.tvPayType.text = "Pago semanal"
            binding.tvAmount.text = "$$pagoSemanal.00"
            binding.spinnerDaysOfWeek.visibility = View.GONE
        }

        binding.btnRegisterCredit.setOnClickListener {
            if (validarMonto()){
                val monto = binding.etAmount.text.toString().toInt()
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.apply {
                    setTitle("Confirmación de registro")
                    setMessage("¿Registrar pago por $$monto?")
                    setPositiveButton("Sí"){ dialog, _ ->
                        dialog.dismiss()
                        val payment = PaymentBody(
                            fecha = paymentRepository.getCurrentDate(),
                            monto = monto,
                            folio= "",
                            credito= idCredito
                        )

                        if (inicial){
                            val updatePayment = PutPayment(
                                id = idCredito,
                                diaPago = binding.spinnerDaysOfWeek.selectedItem.toString()
                            )
                            saveInitialPayment(payment, updatePayment)
                        }else{
                            saveWeeklyPayment(payment)
                        }
                    }
                    setNegativeButton("No"){dialog, _ ->
                        dialog.dismiss()
                    }
                }
                val alertDialogBuilder = alertDialog.create()
                alertDialogBuilder.show()

            }
        }

    }

    private fun validarMonto() : Boolean{
        if (binding.etAmount.text.isEmpty()){
            binding.etAmount.error = "Monto requerido"
            binding.etAmount.requestFocus()
            return false
        }
        return true
    }

    private fun saveInitialPayment(paymentBody: PaymentBody, putPayment: PutPayment) {
        loadingDialog.startLoadingDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (response, paymentResponse) = paymentRepository.savePayment(paymentBody)
                val  response2 = paymentRepository.updateDay(putPayment)
                if (response && response2) {
                    showMessage("Éxito al registrar el pago!")
                    if (paymentResponse != null) {
                        navigateToReceipt(paymentResponse)
                    }
                } else {
                    showMessage("Error al registrar el pago1")
                }
            } catch (e: Exception) {
                showMessage("Error al registrar el pago $e")
            } finally {
                loadingDialog.dismissDialog()
            }
        }
    }

    private fun saveWeeklyPayment(paymentBody: PaymentBody) {
        loadingDialog.startLoadingDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (response, paymentResponse) = paymentRepository.savePayment(paymentBody)
                if (response) {
                    showMessage("Éxito al registrar el pago!")
                    if (paymentResponse != null) {
                        navigateToReceipt(paymentResponse)
                    }
                } else {
                    showMessage("Error al registrar el pago1")
                }
            } catch (e: Exception) {
                showMessage("Error al registrar el pago")
            } finally {
                loadingDialog.dismissDialog()
            }
        }
    }

    private fun showMessage(message: String) {
        runOnUiThread {
            Toast.makeText(this@RegisterPaymentActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToReceipt(payment: PaymentResponsePost) {
        val client = payment.data.credito.cliente
        val intent = Intent(this@RegisterPaymentActivity, PaymentReceiptActivity::class.java)
        intent.putExtra("monto", payment.data.monto)
        intent.putExtra("fecha", payment.data.fecha)
        intent.putExtra("idCredito", payment.data.credito.id)
        intent.putExtra("credito", payment.data.credito.contrato)
        intent.putExtra("cliente", "${client.nombre} ${client.paterno} ${client.materno}")
        intent.putExtra("folio", payment.data.folio)
        intent.putExtra("idPago", payment.data.id)
        startActivity(intent)
        finish()
    }
}