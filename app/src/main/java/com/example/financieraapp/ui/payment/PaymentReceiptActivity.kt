package com.example.financieraapp.ui.payment

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.financieraapp.R
import com.example.financieraapp.databinding.ActivityPaymentReceiptBinding
import com.example.financieraapp.domain.repository.payment.PaymentRepository
import com.example.financieraapp.ui.core.LoadingDialog
import com.example.financieraapp.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentReceiptActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentReceiptBinding
    var monto = 0
    var credito = ""
    var cliente = ""
    var folio = ""
    var idPayment = 0
    var fecha = ""
    var idCredito = 0
    lateinit var paymentRepository: PaymentRepository
    val loadingDialog = LoadingDialog(this@PaymentReceiptActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentReceiptBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi(){
        paymentRepository = PaymentRepository(this)
        monto = intent.getIntExtra("monto", 0)
        credito = intent.getStringExtra("credito").toString()
        cliente = intent.getStringExtra("cliente").toString()
        folio = intent.getStringExtra("folio").toString()
        idPayment = intent.getIntExtra("idPago", 0)
        fecha = intent.getStringExtra("fecha").toString()
        idCredito = intent.getIntExtra("idCredito", 0)

        binding.tvClient.text = cliente
        binding.tvCredit.text = credito
        binding.tvFolio.text = folio
        binding.tvAmount.text = "$$monto.00"
        binding.btnReceipt.setOnClickListener {
            getPdfs(idPayment, idCredito , cliente , fecha )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, ChargesListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun getPdfs (id: Int, idCredito : Int, cliente : String, fecha : String){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Confirmación de descarga")
            setMessage("¿Deseas descargar el recibo de pago?")
            setPositiveButton("Sí"){ dialog, _ ->
                dialog.dismiss()

                CoroutineScope(Dispatchers.IO).launch {
                    var flag = verificarPermisos()
                    withContext(Dispatchers.Main){
                        if (flag){
                            var successPdf = downloadPdfs(id, idCredito, cliente, fecha)
                            if (successPdf){
                                Toast.makeText(this@PaymentReceiptActivity, "Documento descargado", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this@PaymentReceiptActivity, "Error al descargar el documento", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            setNegativeButton("No"){dialog, _ ->
                dialog.dismiss()
            }
        }

        val alertDialogBuilder = alertDialog.create()
        alertDialogBuilder.show()


    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isAceptado ->
        if (isAceptado) Toast.makeText(this, "PERMISOS CONCEDIDOS", Toast.LENGTH_SHORT).show()
        else Toast.makeText(this, "PERMISOS DENEGADOS", Toast.LENGTH_SHORT).show()
    }

    private suspend fun verificarPermisos(): Boolean {
        return when {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Los permisos ya están concedidos
                true
            }
            else -> {
                // Lanzar la solicitud de permiso
                requestPermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                false
            }
        }
    }



    private suspend fun downloadPdfs(id: Int, idCredito : Int, cliente : String, fecha : String): Boolean {
        try {
            loadingDialog.startLoadingDialog()
            paymentRepository.saveReceiptPdf(id, idCredito, cliente, fecha)
            return true
        }catch (e : Exception){
            return false
        }finally {
            loadingDialog.dismissDialog()
        }
    }
}