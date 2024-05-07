package com.example.financieraapp.ui.home

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.financieraapp.R
import com.example.financieraapp.data.models.payment.PaymentBody
import com.example.financieraapp.data.models.payment.PutPayment
import com.example.financieraapp.data.models.paymentCut.PaymentCut
import com.example.financieraapp.databinding.ActivityHomeBinding
import com.example.financieraapp.ui.client.ClientListActivity
import com.example.financieraapp.ui.client.RegisterClientActivity
import com.example.financieraapp.ui.client.RegisterContactClientActivity
import com.example.financieraapp.ui.credit.RegisterCreditActivity
import com.example.financieraapp.ui.login.MainActivity
import com.example.financieraapp.ui.payment.ChargesListActivity
import com.example.financieraapp.ui.paymentCut.PaymentCutActivity
import com.example.financieraapp.ui.paymentCut.PaymentCutListActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()

    }

    private fun initUI(){
        binding.tvUserName.text = getUsername()
        //Ir a cobros semanales
        binding.cvCortes.setOnClickListener {
            val intent = Intent(this, PaymentCutListActivity::class.java)
            startActivity(intent)
        }
        //Ir a registrar cliente
        binding.cvNewClient.setOnClickListener {
            val intent = Intent(this, RegisterClientActivity::class.java)
            startActivity(intent)
        }
        //Ir a registrar credito
        binding.cvNewCredit.setOnClickListener {
            val intent = Intent(this, ChargesListActivity::class.java)
            startActivity(intent)
        }
        //Ir lista de clientes
        binding.cvNewReference.setOnClickListener {
            val intent = Intent(this, ClientListActivity::class.java)
            startActivity(intent)
        }
        //Cerrar sesión
        binding.cvLogout.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this)
            alertDialog.apply {
                setTitle("Cerrar sesión")
                setMessage("¿Deseas cerrar la sesión?")
                setPositiveButton("Sí"){ dialog, _ ->
                    dialog.dismiss()
                    clearToken()
                    val intent = Intent(this@HomeActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finishAffinity()
                }
                setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            }
            val alertDialogBuilder = alertDialog.create()
            alertDialogBuilder.show()


        }
    }

    private fun clearToken() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = preferences.edit()
        editor.remove("authToken") // Remover el token guardado
        editor.apply()
    }

    private fun getUsername() : String?{
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val username = preferences.getString("nombreUsuario", "")
        return username
    }

}