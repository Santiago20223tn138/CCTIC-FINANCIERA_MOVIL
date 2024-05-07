package com.example.financieraapp.ui.client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.financieraapp.R
import com.example.financieraapp.data.models.client.ClientBody
import com.example.financieraapp.databinding.ActivityRegisterContactClientBinding
import com.example.financieraapp.domain.repository.client.ClientRepository
import com.example.financieraapp.ui.core.LoadingDialog
import com.example.financieraapp.ui.credit.RegisterCreditActivity
import com.example.financieraapp.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterContactClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterContactClientBinding
    private lateinit var client : ClientBody
    lateinit var clientRepository: ClientRepository
    val loadingDialog = LoadingDialog(this@RegisterContactClientActivity)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterContactClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        client = intent.getParcelableExtra("cliente")!!

        initUI()
    }

    private fun initUI(){
        clientRepository = ClientRepository(this)
        binding.btnRegisterClient.setOnClickListener {
            if (validateForm()){
                client = client.copy(
                    cn_nombre = binding.etNameReference.text.toString(),
                    cn_telefono_1 = if (binding.etTel1Reference.text.isEmpty()) "" else binding.etTel1Reference.text.toString(),
                    cn_telefono_2 = if (binding.etTel2Reference.text.isEmpty()) "" else binding.etTel2Reference.text.toString(),
                    parentezco = binding.etRelation.text.toString())

                saveClient(client)
            }
        }
    }

    private fun validateForm():Boolean{
        val regexTel = Regex("^\\d{10}\$")
        if (binding.etNameReference.text.isEmpty()){
            binding.etNameReference.error = "El nombre es obligatorio"
            binding.etNameReference.requestFocus()
            return false
        }else if (binding.etTel1Reference.text.isNotEmpty() && !regexTel.matches(binding.etTel1Reference.text)){
            binding.etTel1Reference.error = "Teléfono inválido"
            binding.etTel1Reference.requestFocus()
            return false
        }else if (binding.etTel2Reference.text.isNotEmpty() && !regexTel.matches(binding.etTel2Reference.text)){
            binding.etTel2Reference.error = "Teléfono inválido"
            binding.etTel2Reference.requestFocus()
            return false
        }else if (binding.etRelation.text.isEmpty()){
            binding.etRelation.error = "El parentezco es obligatorio"
            binding.etRelation.requestFocus()
            return false
        }
        return true
    }

    private fun saveClient(clientBody: ClientBody){
        loadingDialog.startLoadingDialog()
        CoroutineScope(Dispatchers.IO).launch{
            try {
                val response = clientRepository.saveClient(clientBody)
                if (response){
                    runOnUiThread{
                        Toast.makeText(this@RegisterContactClientActivity, "Se ha registrado al cliente con éxito!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterContactClientActivity, ClientListActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finishAffinity()
                    }
                }
            }catch (e: Exception) {
                withContext(Dispatchers.Main){
                    Toast.makeText(this@RegisterContactClientActivity, "Error al registrar el cliente", Toast.LENGTH_SHORT).show()
                }
            }finally {
                loadingDialog.dismissDialog()
            }
        }
    }
}