package com.example.financieraapp.ui.login

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.financieraapp.data.models.auth.AuthEntity
import com.example.financieraapp.databinding.ActivityMainBinding
import com.example.financieraapp.domain.repository.auth.AuthRepository
import com.example.financieraapp.ui.core.LoadingDialog
import com.example.financieraapp.ui.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.attribute.AclEntry.Builder


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var authRepository: AuthRepository
    private lateinit var authEntity: AuthEntity
    val loadingDialog = LoadingDialog(this@MainActivity)


    private var cancellationSignal : CancellationSignal? = null
    private val authenticationCallback:BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    val preferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                    val username = preferences.getString("username", "")
                    val password = preferences.getString("password", "")
                    authEntity = AuthEntity(username!!, password!!)
                    login(authEntity)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                }
            }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initUI(){
        authRepository = AuthRepository(this)
        binding.btnLogin.setOnClickListener {
            if (validateEmail() && validatePassword()){
                authEntity = AuthEntity(binding.etEmail.text.toString(), binding.etPassword.text.toString())
                login(authEntity)
            }
        }
        checkBiometricSupport()
        binding.btnFingerprint.setOnClickListener {
            if (validateCredentialsSharedPref()){
                if (checkBiometricSupport()){
                    confBiometrciPrompt()
                }
            }
        }
    }

    private fun login(authEntity: AuthEntity){
        loadingDialog.startLoadingDialog()
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val (response, status) = authRepository.login(authEntity)
                if (response){
                    if (status == 1){
                        runOnUiThread{
                            handleLoginSuccess()
                        }
                    }else{
                        runOnUiThread{
                            Toast.makeText(this@MainActivity, "Usuario inactivo", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.i("error: ", " $e")
                withContext(Dispatchers.Main){
                    handleLoginError(e)
                }
            }finally {
                loadingDialog.dismissDialog()
            }
        }
    }


    private fun handleLoginSuccess() {
        val intent = Intent(this, HomeActivity::class.java)
        Toast.makeText(this@MainActivity, "Bienvenido/a", Toast.LENGTH_SHORT).show()
        startActivity(intent)
        finish()
    }


    private fun handleLoginError(exception: Exception) {
        Log.i("error2: ", " $exception")
        Toast.makeText(this@MainActivity, "Correo y/o contraseña incorrectos", Toast.LENGTH_SHORT).show()
    }

    private fun validateEmail():Boolean{
        val username = binding.etEmail.text.toString().trim()

        if (username.isEmpty()) {
            binding.etEmail.error = "Correo electrónico requerido"
            binding.etEmail.requestFocus()
            return false
        }
//        else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
//            binding.etEmail.error = "Correo electrónico no válido"
//            binding.etEmail.requestFocus()
//            return false
//        }
        return true
    }

    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString().trim()

        if (password.isEmpty()) {
            binding.etPassword.error = "Contraseña requerida"
            binding.etPassword.requestFocus()
            return false
        }
        return true
    }

    private fun getCancellationSignal():CancellationSignal{
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            Toast.makeText(this@MainActivity, "Autenticación por huella cancelada", Toast.LENGTH_SHORT).show()
        }
        return cancellationSignal as CancellationSignal
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun confBiometrciPrompt(){
        val biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle("Ingresar a la app")
            .setSubtitle("Escanea tu huella para ingresar")
            .setNegativeButton("Cancelar", this.mainExecutor) {_, _ ->
                Toast.makeText(this@MainActivity, "Operación cancelada", Toast.LENGTH_SHORT).show()
            }.build()

        biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
    }

    private fun checkBiometricSupport() :Boolean{
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyguardManager.isKeyguardSecure){
            Toast.makeText(this@MainActivity, "No disponible en configuraciones", Toast.LENGTH_SHORT).show()
            return false
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this@MainActivity, "Permisos de autenticación por huella no enabled", Toast.LENGTH_SHORT).show()
            return false
        }
        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)){
            true
        }else true
    }

    private fun validateCredentialsSharedPref() : Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val usernameExists = preferences.contains("username")
        val passwordExists = preferences.contains("password")

        if (usernameExists && passwordExists) {
            return true
        } else {
            Toast.makeText(this@MainActivity, "Es necesario el inicio de sesión manual", Toast.LENGTH_SHORT).show()
            return false
        }
    }

}
