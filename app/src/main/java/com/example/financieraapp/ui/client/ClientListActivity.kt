package com.example.financieraapp.ui.client

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.financieraapp.data.models.client.Client
import com.example.financieraapp.databinding.ActivityClientListBinding
import com.example.financieraapp.domain.repository.client.ClientRepository
import com.example.financieraapp.domain.repository.contract.ContractRepository
import com.example.financieraapp.ui.core.LoadingDialog
import com.example.financieraapp.ui.credit.CreditListActivity
import com.example.financieraapp.ui.credit.RegisterCreditActivity
import com.example.financieraapp.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClientListActivity : AppCompatActivity() {
    lateinit var binding: ActivityClientListBinding
    private lateinit var adapter: ClientAdapter
    lateinit var clientRepository: ClientRepository
    lateinit var clients : List<Client>
    val loadingDialog = LoadingDialog(this@ClientListActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUi()
    }

    private fun initUi(){
        clientRepository = ClientRepository(this)
        adapter = ClientAdapter(onItemSelected = { newCredit(it) }, getCredits = { getCredits(it) })
        binding.rvClients.setHasFixedSize(true)
        binding.rvClients.layoutManager = LinearLayoutManager(this)
        binding.rvClients.adapter = adapter
        getClients()
        binding.btnSearch.setOnClickListener {
            if (binding.svClients.visibility == View.VISIBLE) {
                binding.svClients.setQuery("", false)
                binding.svClients.visibility = View.GONE
            } else {
                binding.svClients.visibility = View.VISIBLE
                binding.svClients.isIconified = false
                binding.svClients.requestFocus()
            }
        }
        binding.svClients.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val query = binding.svClients.query.toString().lowercase()

                val clientsFilter = clients.filter { client ->
                    val nombreCompleto = "${client.nombre} ${client.paterno} ${client.materno}".lowercase()
                    nombreCompleto.contains(query) ||
                            client.paterno.lowercase().contains(query) ||
                            client.materno.lowercase().contains(query)
                }
                adapter.updateList(clientsFilter)
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = binding.svClients.query.toString().lowercase()

                val clientsFilter = clients.filter { client ->
                    val nombreCompleto = "${client.nombre} ${client.paterno} ${client.materno}".lowercase()
                    nombreCompleto.contains(query) ||
                            client.paterno.lowercase().contains(query) ||
                            client.materno.lowercase().contains(query)
                }
                adapter.updateList(clientsFilter)
                return false;
            }

        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun getClients(){
        loadingDialog.startLoadingDialog()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                clients = clientRepository.getClients()
                if (clients.isEmpty()) {
                    binding.msgClients.text = "No hay clientes registrados"
                    binding.msgClients.visibility = View.VISIBLE
                }else{
                    runOnUiThread{
                        adapter.updateList(clients)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main){
                    binding.msgClients.text = "Error al cargar los clientes"
                    binding.msgClients.visibility = View.VISIBLE
                    Toast.makeText(this@ClientListActivity, "Error al cargar los clientes", Toast.LENGTH_SHORT).show()
                }
            }finally {
                loadingDialog.dismissDialog()
            }
        }
    }

    private fun newCredit(id:Int){
        val intent = Intent(this, RegisterCreditActivity::class.java)
        intent.putExtra("idClient", id)
        startActivity(intent)
    }

    private fun getCredits(id: Int){
        val intent = Intent(this, CreditListActivity::class.java)
        intent.putExtra("idClient", id)
        startActivity(intent)
    }
}