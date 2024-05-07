package com.example.financieraapp.ui.paymentCut

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financieraapp.R
import com.example.financieraapp.databinding.ActivityPaymentCutBinding
import com.example.financieraapp.domain.repository.paymentCut.PaymentCutRepository
import com.example.financieraapp.ui.core.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentCutActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentCutBinding
    lateinit var paymentCutRepository: PaymentCutRepository
    private lateinit var adapterPaid : PaymentCutAdapter
    private lateinit var adapterNotPaid : PaymentCutAdapter
    var corte = 0

    val loadingDialog = LoadingDialog(this@PaymentCutActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentCutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        corte = intent.getIntExtra("idCorte", 0)
        initUI()
    }

    private fun initUI(){
        paymentCutRepository = PaymentCutRepository(this)
        adapterPaid = PaymentCutAdapter()
        binding.rvPaid.setHasFixedSize(true)
        binding.rvPaid.layoutManager = LinearLayoutManager(this)
        binding.rvPaid.adapter = adapterPaid
        binding.btnDownload.setOnClickListener {
            getPdfs(corte)
        }

        adapterNotPaid = PaymentCutAdapter()
        binding.rvNotPaid.setHasFixedSize(true)
        binding.rvNotPaid.layoutManager = LinearLayoutManager(this)
        binding.rvNotPaid.adapter = adapterNotPaid

        getPaymentCutInfo(corte)

    }

    private fun getPaymentCutInfo(idCorte : Int){
        loadingDialog.startLoadingDialog()
        CoroutineScope(Dispatchers.IO).launch{
            val cut = paymentCutRepository.getByPaymentCut(idCorte)
            try {
                withContext(Dispatchers.Main){
                    adapterPaid.updateList(paymentCutRepository.getPaid())
                    adapterNotPaid.updateList(paymentCutRepository.getNotPaid())
                    if (paymentCutRepository.getNotPaid().isEmpty()){
                        binding.tvMsgNotPaid.visibility = View.VISIBLE
                    }
                    if (paymentCutRepository.getPaid().isEmpty()){
                        binding.tvMsgPaid.visibility = View.VISIBLE
                    }
                    binding.tvMonto.text = "$${paymentCutRepository.getMonto()}.00"
                    binding.tvDate.text = paymentCutRepository.getDate()
                }


            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@PaymentCutActivity, "Error al cargar los detalles del corte", Toast.LENGTH_SHORT).show()
                }
            }finally {
                loadingDialog.dismissDialog()
            }
        }
    }

    private fun getPdfs (idCorte : Int){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Confirmación de descarga")
            setMessage("¿Deseas descargar la información del corte?")
            setPositiveButton("Sí"){ dialog, _ ->
                dialog.dismiss()

                CoroutineScope(Dispatchers.IO).launch {
                    var flag = verificarPermisos()
                    withContext(Dispatchers.Main){
                        if (flag){
                            var successPdf = downloadPdfs(idCorte)
                            if (successPdf){
                                Toast.makeText(this@PaymentCutActivity, "Documento descargado", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this@PaymentCutActivity, "Error al descargar el documento", Toast.LENGTH_SHORT).show()
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

    private suspend fun downloadPdfs(idCorte : Int): Boolean {
        try {
            loadingDialog.startLoadingDialog()
            paymentCutRepository.saveCorteDetalles(idCorte)
            return true
        }catch (e : Exception){
            return false
        }finally {
            loadingDialog.dismissDialog()
        }
    }


}