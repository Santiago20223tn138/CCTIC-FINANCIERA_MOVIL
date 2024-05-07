package com.example.financieraapp.ui.credit

import android.content.pm.PackageManager
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem.OnMenuItemClickListener
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financieraapp.R
import com.example.financieraapp.databinding.ActivityCreditListBinding
import com.example.financieraapp.domain.repository.contract.ContractRepository
import com.example.financieraapp.domain.repository.credit.CreditRepository
import com.example.financieraapp.domain.repository.pagare.PagareRepository
import com.example.financieraapp.domain.repository.paymentRecord.PaymentRecordRepository
import com.example.financieraapp.domain.repository.responsiva.ResponsivaRepository
import com.example.financieraapp.ui.client.ClientAdapter
import com.example.financieraapp.ui.core.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreditListActivity : AppCompatActivity() {
    lateinit var binding: ActivityCreditListBinding
    private lateinit var adapter: CreditAdapter
    var idClient : Int = 0
    lateinit var creditRepository: CreditRepository
    val loadingDialog = LoadingDialog(this@CreditListActivity)
    private lateinit var pagareRepository: PagareRepository
    private lateinit var contractRepository: ContractRepository
    private lateinit var responsivaRepository: ResponsivaRepository
    private lateinit var paymentRecordRepository: PaymentRecordRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreditListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idClient = intent.getIntExtra("idClient", 0)
        initUi()
    }

    private fun initUi(){
        creditRepository = CreditRepository(this)
        pagareRepository = PagareRepository(this)
        contractRepository = ContractRepository(this)
        responsivaRepository = ResponsivaRepository(this)
        paymentRecordRepository = PaymentRecordRepository(this)
        adapter = CreditAdapter (onItemSelected = {id, contract -> getPdfs(id, contract)}, context = this)

        binding.rvCredits.setHasFixedSize(true)
        binding.rvCredits.layoutManager = LinearLayoutManager(this)
        binding.rvCredits.adapter = adapter
        getCredits()
    }



    private fun getCredits(){
        loadingDialog.startLoadingDialog()
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val credits = creditRepository.getCreditsByClient(idClient)
                runOnUiThread {
                    if (credits.isEmpty()) {
                        binding.msgCredits.text = "No hay créditos registrados"
                        binding.msgCredits.visibility = View.VISIBLE
                    } else {
                        adapter.updateList(credits)
                    }
                }
            } catch (e: Exception){
                withContext(Dispatchers.Main){
                    binding.msgCredits.text = "No hay créditos para mostrar"
                    binding.msgCredits.visibility = View.VISIBLE
                    Toast.makeText(this@CreditListActivity, "Error al cargar los créditos", Toast.LENGTH_SHORT).show()
                }
            } finally {
                loadingDialog.dismissDialog()
            }
        }
    }


    private fun getPdfs (id : Int, contract: String){

        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle("Confirmación de descarga")
            setMessage("¿Deseas descargar los documentos de este crédito?")
            setPositiveButton("Sí"){ dialog, _ ->
                dialog.dismiss()

                CoroutineScope(Dispatchers.IO).launch {
                    var flag = verificarPermisos()
                    withContext(Dispatchers.Main){
                        if (flag){
                            var successPdf = downloadPdfs(id, contract)
                            if (successPdf){
                                Toast.makeText(this@CreditListActivity, "Documentos descargados", Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(this@CreditListActivity, "Error al descargar los documentos", Toast.LENGTH_SHORT).show()
                            }
                        }else{
                            //Toast.makeText(this@CreditListActivity, "Se necesitan permisos para descargar los documentos", Toast.LENGTH_SHORT).show()
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

    private suspend fun downloadPdfs(id : Int, contract : String): Boolean {
        try {
            loadingDialog.startLoadingDialog()
            pagareRepository.savePagare(id, contract)
            contractRepository.saveContractPdf(id, contract)
            responsivaRepository.savePagare(id, contract)
            paymentRecordRepository.savePaymentRecord(id, contract)
            return true
        }catch (e : Exception){
            return false
        }finally {
            loadingDialog.dismissDialog()
        }
    }


}