package com.example.financieraapp.ui.paymentCut

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financieraapp.R
import com.example.financieraapp.databinding.ActivityPaymentCutListBinding
import com.example.financieraapp.domain.repository.paymentCut.PaymentCutRepository
import com.example.financieraapp.ui.core.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentCutListActivity : AppCompatActivity() {
    lateinit var binding: ActivityPaymentCutListBinding
    val loadingDialog = LoadingDialog(this@PaymentCutListActivity)
    private lateinit var paymentCutRepository: PaymentCutRepository
    private lateinit var adapter: CorteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentCutListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI(){
        paymentCutRepository = PaymentCutRepository(this)
        adapter = CorteAdapter { navigateToDetail( it ) }

        binding.rvCortes.setHasFixedSize(true)
        binding.rvCortes.layoutManager = LinearLayoutManager(this)
        binding.rvCortes.adapter = adapter
        getCortes()
    }

    private fun getCortes(){
        loadingDialog.startLoadingDialog()
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val cortes = paymentCutRepository.getPaymentsCut()
                runOnUiThread {
                    if (cortes.isEmpty()) {
                        binding.msgCredits.text = "No hay cortes registrados"
                        binding.msgCredits.visibility = View.VISIBLE
                    } else {
                        adapter.updateList(cortes)
                    }
                }
            }catch (e : Exception){
                withContext(Dispatchers.Main){
                    binding.msgCredits.text = "No hay cortes para mostrar"
                    binding.msgCredits.visibility = View.VISIBLE
                    Toast.makeText(this@PaymentCutListActivity, "Error al cargar los cortes", Toast.LENGTH_SHORT).show()
                }
            }finally {
                loadingDialog.dismissDialog()
            }
        }
    }

    private fun navigateToDetail(idCorte : Int){
        val intent = Intent(this@PaymentCutListActivity, PaymentCutActivity::class.java)
        intent.putExtra("idCorte", idCorte)
        startActivity(intent)
    }
}