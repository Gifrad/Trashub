package com.capstone.project.trashhub.view.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.capstone.project.trashhub.R
import com.capstone.project.trashhub.databinding.ActivityLoginBinding
import com.capstone.project.trashhub.view.home.HomeActivity
import com.capstone.project.trashhub.view.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        auth = Firebase.auth
        dialog = Dialog(this)

        setupAction()
        googleBtnClick()
        showLoading(false)
    }

    private fun setupAction() {
        binding.btnLogin.setOnClickListener {
            showLoading(true)
            val email: String = binding.emailEdtText.text.toString().trim()
            val pass: String = binding.passwordEdtText.text.toString().trim()

            when {
                email.isEmpty() -> {
                    showLoading(false)
                    binding.emailEdtText.error = "Email tidak boleh kosong"
                    binding.emailEdtText.requestFocus()
                    return@setOnClickListener
                }
                pass.isEmpty() -> {
                    showLoading(false)
                    binding.passwordEdtText.error = "Password tidak boleh kosong"
                    binding.passwordEdtText.requestFocus()
                    return@setOnClickListener
                }
            }
            loginUser(email, pass)
        }
        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    showLoading(false)
                    Intent(
                        this@LoginActivity,
                        HomeActivity::class.java
                    ).also { intent ->
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                } else {
                    showLoading(false)
                    Toast.makeText(
                        this,
                        "Login gagal Periksa Email dan Password",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
//        if (auth.currentUser != null) {
//            Intent(
//                this@LoginActivity,
//                HomeActivity::class.java
//            ).also { intent ->
//                intent.flags =
//                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
//            }
//        }
    }

    private fun googleBtnClick() {
        binding.btnGoogle.setOnClickListener {
            dialog.setContentView(R.layout.perbaikan)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val imageViewClose = dialog.findViewById<ImageButton>(R.id.img_button_close_repair)
            val buttonOk = dialog.findViewById<Button>(R.id.btn_ok_repair)

            imageViewClose.setOnClickListener {
                dialog.dismiss()
                Toast.makeText(this,"Dialog Close", Toast.LENGTH_SHORT).show()
            }
            buttonOk.setOnClickListener {
                dialog.dismiss()
                Toast.makeText(this,"Dialog Close", Toast.LENGTH_SHORT).show()
            }
            dialog.show()
        }
    }


    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}