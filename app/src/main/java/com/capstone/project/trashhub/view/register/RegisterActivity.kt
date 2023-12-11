package com.capstone.project.trashhub.view.register

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capstone.project.trashhub.R
import com.capstone.project.trashhub.databinding.ActivityRegisterBinding
import com.capstone.project.trashhub.view.home.HomeActivity
import com.capstone.project.trashhub.view.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var databaseReferences : DatabaseReference
    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        databaseReferences = FirebaseDatabase.getInstance().getReferenceFromUrl("https://trashhub-e7744-default-rtdb.firebaseio.com/")
        fireStore = FirebaseFirestore.getInstance()
        dialog = Dialog(this)
        setupAction()
        showLoading(false)

        binding.btnRegisterBankSampah.setOnClickListener {
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

    private fun setupAction() {
        binding.btnRegister.setOnClickListener {
            showLoading(true)
            val name: String = binding.nameEdtText.text.toString().trim()
            val email: String = binding.emailEdtText.text.toString().trim()
            val pass: String = binding.passwordEdtText.text.toString().trim()
            val confPass: String = binding.confPassEdtText.text.toString().trim()

            when {

                name.isEmpty() -> {
                    showLoading(false)
                    binding.nameEdtText.error = "Nama tidak boleh kosong"
                    binding.nameEdtText.requestFocus()
                    return@setOnClickListener
                }
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
                confPass.isEmpty() || confPass != pass -> {
                    showLoading(false)
                    binding.confPassEdtText.error = "Silahkan masukan password yang sama"
                    binding.confPassEdtText.requestFocus()
                    return@setOnClickListener
                }
            }

            databaseReferences.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.child("users").hasChild(name)){
                        Toast.makeText(this@RegisterActivity,"Name Already Exists", Toast.LENGTH_SHORT).show()
                        binding.nameEdtText.text = null
                        showLoading(false)
                    }else{
                        databaseReferences.child("users").child(name).child("name").setValue(name)
                        databaseReferences.child("users").child(name).child("email").setValue(email)
                        databaseReferences.child("users").child(name).child("profile_pict").setValue("")
                        registerUser(name,email, pass)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
        binding.btnLogin.setOnClickListener {
            showLoading(true)
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
    }

    private fun registerUser(name: String, email: String, pass: String) {

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) {
                showLoading(false)
                if (it.isSuccessful) {
                    val userFirebase = Firebase.auth.currentUser
                    val profileUpdate = userProfileChangeRequest {
                        displayName = name
                    }
                    val userId = userFirebase?.uid
                    val documentReference = fireStore.collection("Users").document(userId.toString())
                    val user = hashMapOf(
                        "id" to userId,
                        "name" to name,
                        "jenisKelamin" to "",
                        "noHp" to "",
                        "photoUrl" to "",
                        "alamat" to "",
                        "poin" to ""
                    )
                    documentReference
                        .set(user)
                        .addOnSuccessListener {
                            Log.d(ContentValues.TAG, "registerUser with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener {e ->
                            Log.e(ContentValues.TAG, "registerUser: Error ", e)
                        }
                    userFirebase!!.updateProfile(profileUpdate)
                        .addOnCompleteListener{ task ->
                            if(task.isSuccessful){
                                Log.d(ContentValues.TAG, "Name Terdaftar")
                            }
                        }
                    Intent(
                        this@RegisterActivity,
                        LoginActivity::class.java
                    ).also { intent ->
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    Toast.makeText(this, "Berhasil Mendaftar", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    showLoading(false)
                    Toast.makeText(this, "Gagal Mendaftar", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser != null){
            Intent(
                this@RegisterActivity,
                LoginActivity::class.java
            ).also { intent ->
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }


    private fun showLoading(state: Boolean) {
            if (state) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
}
