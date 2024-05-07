package com.example.financieraapp.ui.credit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import com.example.financieraapp.R
import com.example.financieraapp.data.models.credit.CreditBody
import com.example.financieraapp.databinding.ActivityRegisterCreditBinding
import com.example.financieraapp.domain.repository.client.ClientRepository
import com.example.financieraapp.domain.repository.credit.CreditRepository
import com.example.financieraapp.ui.core.LoadingDialog
import com.example.financieraapp.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterCreditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterCreditBinding
    var idClient : Int = 0
    lateinit var creditRepository: CreditRepository
    val loadingDialog = LoadingDialog(this@RegisterCreditActivity)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterCreditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idClient = intent.getIntExtra("idClient", 0)
        initUI()
    }

    private fun initUI(){
        creditRepository = CreditRepository(this@RegisterCreditActivity)

        binding.btnRegisterCredit.setOnClickListener {
            if (validarMonto()){
                val monto = binding.etAmount.text.toString().toInt()
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.apply {
                    setTitle("Confirmación de registro")
                    setMessage("¿Registrar crédito por $$monto?")
                    setPositiveButton("Sí"){ dialog, _ ->
                        dialog.dismiss()
                        val credit = CreditBody(
                            contrato = "",
                            diaPago = "",
                            folio = "",
                            monto = monto,
                            inicial = 0,
                            semanal = 0,
                            cliente = idClient,
                            fecha = creditRepository.getCurrentDate(),
                            promotora = creditRepository.getPromotora()
                        )
                        saveCredit(credit)
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
        } //else if (binding.etWeeksToPay.text.isEmpty()){
//            binding.etWeeksToPay.error = "Dato requerido"
//            binding.etWeeksToPay.requestFocus()
//        }
        return true
    }

    private fun saveCredit(creditBody: CreditBody){
        loadingDialog.startLoadingDialog()
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response = creditRepository.saveCredit(creditBody)
                if (response){
                    runOnUiThread{
                        Toast.makeText(this@RegisterCreditActivity, "Se ha registrado el crédito con éxito!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterCreditActivity, CreditListActivity::class.java)
                        intent.putExtra("idClient", idClient)
                        startActivity(intent)
                        finish()
                    }
                }else{
                    Toast.makeText(this@RegisterCreditActivity, "Error al registrar el crédito", Toast.LENGTH_SHORT).show()
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@RegisterCreditActivity, "Error al registrar el crédito", Toast.LENGTH_SHORT).show()
                }
            }finally {
                loadingDialog.dismissDialog()
            }
        }
    }
}