package com.example.financieraapp.ui.payment

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
import com.example.financieraapp.databinding.ActivityChargesListBinding
import com.example.financieraapp.domain.repository.payment.PaymentRepository
import com.example.financieraapp.domain.repository.paymentList.PaymentListRepository
import com.example.financieraapp.ui.core.LoadingDialog
import com.example.financieraapp.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChargesListActivity : AppCompatActivity() {
    lateinit var binding: ActivityChargesListBinding
    private lateinit var adapter: ChargesAdapter
    lateinit var paymenrRepository: PaymentRepository
    lateinit var paymentListRepository: PaymentListRepository

    val loadingDialog = LoadingDialog(this@ChargesListActivity)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargesListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI(){
        paymenrRepository = PaymentRepository(this)
        paymentListRepository = PaymentListRepository(this)
        adapter = ChargesAdapter(goToPay = {id, semanal, nombre -> goToPay(id, semanal, nombre)}, cantPay = {cantPay()})
        binding.rvCharges.setHasFixedSize(true)
        binding.rvCharges.layoutManager = LinearLayoutManager(this)
        binding.rvCharges.adapter = adapter
        binding.btnDownload.setOnClickListener {
            getPdfs()
        }
        getCharges()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun getCharges(){
        loadingDialog.startLoadingDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val charges = paymenrRepository.getCharges()
                runOnUiThread {
                    if (charges.isEmpty()){
                        binding.msgCredits.text = "No hay pagos por cobrar"
                        binding.msgCredits.visibility = View.VISIBLE
                    }else{
                        adapter.updateList(charges)
                    }
                }
            }catch (e: Exception){
                withContext(Dispatchers.Main){
                    binding.msgCredits.text = "No hay pagos por cobrar"
                    binding.msgCredits.visibility = View.VISIBLE
                    Toast.makeText(this@ChargesListActivity, "Error al cargar los pagos por hacer", Toast.LENGTH_SHORT).show()
                }
            } finally {
                loadingDialog.dismissDialog()
            }
        }
    }

    private fun getPdfs (){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Confirmación de descarga")
            setMessage("¿Deseas descargar la lista de pagos por hacer?")
            setPositiveButton("Sí"){ dialog, _ ->
                dialog.dismiss()

                CoroutineScope(Dispatchers.IO).launch {
                    var flag = verificarPermisos()
                    withContext(Dispatchers.Main){
                        if (flag){
                            var successPdf = downloadPdfs()
                            if (successPdf){
                                Toast.makeText(this@ChargesListActivity, "Documento descargado", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this@ChargesListActivity, "Error al descargar el documento", Toast.LENGTH_SHORT).show()
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

    private suspend fun downloadPdfs(): Boolean {
        try {
            loadingDialog.startLoadingDialog()
            paymentListRepository.savePaymentList()
            return true
        }catch (e : Exception){
            return false
        }finally {
            loadingDialog.dismissDialog()
        }
    }

    private fun goToPay(id : Int, semanal: Int, nombre:String){
        val intent = Intent(this, RegisterPaymentActivity::class.java)
        intent.putExtra("idCredito", id)
        intent.putExtra("semanal", semanal)
        intent.putExtra("client", nombre)

        startActivity(intent)
    }

    private fun cantPay(){
        Toast.makeText(this@ChargesListActivity, "Ya hay un pago registrado dentro del corte actual para este crédito", Toast.LENGTH_SHORT).show()
    }
}