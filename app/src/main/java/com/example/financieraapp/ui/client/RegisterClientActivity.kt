package com.example.financieraapp.ui.client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import com.example.financieraapp.R
import com.example.financieraapp.data.models.client.ClientBody
import com.example.financieraapp.databinding.ActivityHomeBinding
import com.example.financieraapp.databinding.ActivityRegisterClientBinding
import com.example.financieraapp.domain.repository.client.ClientRepository

class RegisterClientActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterClientBinding

    lateinit var clientRepository: ClientRepository
    private var client: ClientBody = ClientBody(
        nombre = "",
        numero_cliente = "",
        paterno = "",
        materno = "",
        edad = 0,
        ingreso_semanal = null,
        telefono_1 = "",
        telefono_2 = null,
        correo = "",
        domicilio = "",
        domicilio_detalle = "",
        cn_nombre = null,
        cn_telefono_1 = null,
        cn_telefono_2 = null,
        parentezco = null
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    private fun initUI(){
        clientRepository = ClientRepository(this)
        binding.btnRegisterClient.setOnClickListener {
            if (validateForm()){
                client = ClientBody(
                    nombre = binding.etName.text.toString().trim(),
                    numero_cliente = "",
                    paterno = binding.etLastName.text.toString().trim(),
                    materno = binding.etSurName.text.toString().trim(),
                    edad = if ( binding.etAge.text.isEmpty()) 0 else binding.etAge.text.toString().toInt(),
                    ingreso_semanal = binding.etIngresoSem.text.toString().toDoubleOrNull(),
                    telefono_1 = binding.etTel1.text.toString(),
                    telefono_2 = if (binding.etTel1.text.isEmpty()) "" else binding.etTel1.text.toString(),
                    correo = if (binding.etEmail.text.isEmpty()) "" else binding.etEmail.text.toString(),
                    domicilio = binding.etAddress.text.toString(),
                    domicilio_detalle = binding.etAddressDetail.text.toString(),
                    cn_nombre = null,
                    cn_telefono_1 = null,
                    cn_telefono_2 = null,
                    parentezco = null
                )
                val intent = Intent(this, RegisterContactClientActivity::class.java)
                intent.putExtra("cliente", client)
                startActivity(intent)
            }
        }
    }

    private fun validateForm() : Boolean{
        val regexTel = Regex("^\\d{10}\$")

        if (binding.etName.text.isEmpty()){
            binding.etName.error = "El nombre es obligatorio"
            binding.etName.requestFocus()
            return false
        } else if (binding.etLastName.text.isEmpty()){
            binding.etLastName.error = "El apellido es obligatorio"
            binding.etLastName.requestFocus()
            return false
        } else if (binding.etSurName.text.isEmpty()){
            binding.etSurName.error = "El apellido es obligatorio"
            binding.etSurName.requestFocus()
            return false
        }else if (binding.etEmail.text.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text).matches()){
            binding.etEmail.error = "Correo electrónico no válido"
            binding.etEmail.requestFocus()
            return false
        }else if (binding.etTel1.text.isEmpty()){
            binding.etTel1.error = "El teléfono es obligatorio"
            binding.etTel1.requestFocus()
            return false
        }else if (!regexTel.matches(binding.etTel1.text)){
            binding.etTel1.error = "Teléfono inválido"
            binding.etTel1.requestFocus()
            return false
        }else if (binding.etTel2.text.isNotEmpty() && !regexTel.matches(binding.etTel2.text)){
            binding.etTel2.error = "Teléfono inválido"
            binding.etTel2.requestFocus()
            return false
        }else if (binding.etAddress.text.isEmpty()){
            binding.etAddress.error = "El domicilio es obligatorio"
            binding.etAddress.requestFocus()
            return false
        }else if (binding.etAddressDetail.text.isEmpty()){
            binding.etAddressDetail.error = "El detalle del domicilio es obligatorio"
            binding.etAddressDetail.requestFocus()
            return false
        }

        return true
    }
}

