package com.capstone.project.trashhub.view.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.project.trashhub.R
import com.capstone.project.trashhub.databinding.ActivityProfileBinding
import com.capstone.project.trashhub.view.home.HomeActivity
import com.capstone.project.trashhub.view.home.HomeViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var imageUri: Uri
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var imageProfile : CircleImageView

    private lateinit var poin: String
    private lateinit var spinnerValue: String
    private lateinit var arrayAdapter : ArrayAdapter<String>
    val gender = arrayOf("laki-laki","perempuan")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        poin = intent.getStringExtra("poin").toString()
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageProfile = findViewById(R.id.img_profile)
        auth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()

        spinnerValue = ""
        arrayAdapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,gender)
        binding.spinnerGenderProfile.adapter = arrayAdapter
        binding.spinnerGenderProfile.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (gender[p2] == "laki-laki"){
                    spinnerValue = "laki-laki"
                }
                else if (gender[p2] == "perempuan"){
                    spinnerValue = "perempuan"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
        setupAction()
        showLoading(true)

    }

    private fun setupAction() {
        val user = auth.currentUser

        if (user != null) {
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.imgProfile)
            } else {
                Picasso.get().load(R.drawable.ic_baseline_person_50).into(binding.imgProfile)
            }

            binding.emailEdtText.setText(user.email)
            firebaseFirestore.collection(PROFILE_NAME).document(user.uid)
                .get()
                .addOnSuccessListener { result ->
                        Log.d("TAG", " => ${result.data?.get("alamat")}")
                    if (result.data?.get("photoUrl") != null) {
                        Glide.with(this)
                            .load(result.data?.get("photoUrl"))
                            .into(binding.imgProfile)
                        showLoading(false)
                    }else{
                        Glide.with(this)
                            .load(R.drawable.icon_camera)
                            .into(binding.imgProfile)
                        showLoading(false)
                    }
                        binding.nameEdtText.setText(result.data?.get("name").toString())
                        binding.alamatEdtText.setText(result.data?.get("alamat").toString())
                        binding.noHpEdtText.setText(result.data?.get("noHp").toString())
                    if (result.data?.get("noHp").toString() != null){
                        val spinnerPosition = arrayAdapter.getPosition(result.data?.get("jenisKelamin").toString())
                        binding.spinnerGenderProfile.setSelection(spinnerPosition)
                        if (spinnerPosition == 0){
                            spinnerValue = "laki-laki"
                        }else if(spinnerPosition == 1){
                            spinnerValue = "perempuan"
                        }
                        Log.d("ProfileActivity", "setupAction: $spinnerPosition")
                    }


                }
                .addOnFailureListener { exception ->
                    Log.w("TAG", "Error getting documents.", exception)
                }


        }
        binding.imgProfile.setOnClickListener {
            intentCamera()
        }
        binding.btnSimpan.setOnClickListener {

            Toast.makeText(this,"klik buttn simpan",Toast.LENGTH_SHORT).show()

//            Toast.makeText(this,"image : $image",Toast.LENGTH_SHORT).show()
            firebaseFirestore.collection(PROFILE_NAME)
                .whereEqualTo("id", user?.uid)
                .get()
                .addOnCompleteListener(object : OnCompleteListener<QuerySnapshot> {
                    override fun onComplete(snapshot: Task<QuerySnapshot>) {
                        if (snapshot.isSuccessful && !snapshot.getResult().isEmpty) {
                            val documentSnapshot = snapshot.getResult().documents.get(0)
                            val documentId = documentSnapshot.id
                            Log.d("TAG", "onComplete: ${documentSnapshot.data?.get("photoUrl")}")
                            val d = Log.d("TAG", "onCompleteID: ${documentId}")

                            val image = when {
                                ::imageUri.isInitialized -> imageUri
                                else -> documentSnapshot.data?.get("photoUrl")
                            }
                            firebaseFirestore.collection(PROFILE_NAME)
                                .document(documentId)
                                .update(mapOf(
                                    "id" to user?.uid.toString(),
                                    "name" to binding.nameEdtText.text.toString(),
                                    "jenisKelamin" to spinnerValue,
                                    "noHp" to binding.noHpEdtText.text.toString(),
                                    "photoUrl" to image.toString(),
                                    "alamat" to binding.alamatEdtText.text.toString(),
                                    "poin" to poin
                                ))
                                .addOnSuccessListener(object : OnSuccessListener<Void> {
                                    override fun onSuccess(p0: Void?) {
                                        Toast.makeText(
                                            this@ProfileActivity, "Successfully Update Data",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                })
                                .addOnFailureListener(object : OnFailureListener {
                                    override fun onFailure(p0: Exception) {
                                        Toast.makeText(
                                            this@ProfileActivity, "Some Error for Update Data",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                })
                        }
                    }
                })
                .addOnFailureListener { e ->
                    Log.w(
                        "ProfileActivity",
                        "Error updating document",
                        e
                    )
                }

        }
        binding.btnBack.setOnClickListener {
            Intent(this, HomeActivity::class.java).let {
                startActivity(it)
                finishAffinity()
            }
        }
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun intentCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            packageManager?.let {
                intent.resolveActivity(it).also {
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            Log.d("TAG", "onActivityResult: ${imageBitmap}")
            showLoading(true)
            uploadImage(imageBitmap)
        }
    }


    private fun uploadImage(imageBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val ref =
            FirebaseStorage.getInstance().reference.child("images/${FirebaseAuth.getInstance().currentUser?.uid}")

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image: ByteArray = baos.toByteArray()

        ref.putBytes(image)
            .addOnCompleteListener { upload ->
                if (upload.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener { task ->
                        task.result?.let { uri ->
                            imageUri = uri
                            showLoading(false)
                            imageProfile.setImageBitmap(imageBitmap)
                        }
                    }
                }else{
                    showLoading(false)
                    Toast.makeText(this,"upload gagal",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBarProfile.visibility = View.VISIBLE
        } else {
            binding.progressBarProfile.visibility = View.GONE
        }
    }
    companion object {
        const val REQUEST_CAMERA = 100
        const val PROFILE_NAME = "Users"
    }
}