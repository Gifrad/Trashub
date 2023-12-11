package com.capstone.project.trashhub.view.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.project.trashhub.R
import com.capstone.project.trashhub.databinding.ActivityHomeBinding
import com.capstone.project.trashhub.network.model.ListBankSampah
import com.capstone.project.trashhub.view.adapter.ListBankSampahAdapter
import com.capstone.project.trashhub.view.detailbanksampah.DetailBankSampahActivity
import com.capstone.project.trashhub.view.login.LoginActivity
import com.capstone.project.trashhub.view.maps.MapsActivity
import com.capstone.project.trashhub.view.profile.ProfileActivity
import com.capstone.project.trashhub.view.scan.ResultScanActivity
import com.capstone.project.trashhub.view.search.SearchActivity
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var homeViewModel: HomeViewModel
    var sliderView: ImageSlider? = null
    var textSapaan: TextView? = null
    private lateinit var dateFormat: SimpleDateFormat
    private lateinit var calendar: Calendar
    private lateinit var dialog: Dialog
    private lateinit var poin :String

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        firebaseFirestore = FirebaseFirestore.getInstance()
        val documentReference = firebaseFirestore.collection("Users")
        val firebaseUser = auth.currentUser

        documentReference.document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                if (it.data?.get("poin")?.equals("") == false){
                    binding.tvPrice.text = it.data?.get("poin").toString()
                    poin = it.data?.get("poin").toString()
                }else{
                    binding.tvPrice.text = "-"
                    poin = "-"
                }
                Log.d(TAG, "onCreate: ${it.data}")
            }
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (firebaseUser == null) {
            // Not signed in, launch the Login activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        } else {
            binding.tvUsername.text = "Hai ${firebaseUser.displayName}"
        }
        showLoading(true)
        setupAction()
        setupViewModel()


        sliderView = findViewById(R.id.img_home)
        textSapaan = findViewById(R.id.tv_sapaan_waktu)
        dateFormat = SimpleDateFormat("HH:mm:ssaa")
        calendar = Calendar.getInstance()
        dialog = Dialog(this)
        val valueDate = dateFormat.format(calendar.time)
        Log.d(TAG, "onCreateDateFormat: ${valueDate}")
        if (valueDate.endsWith("PM")){
            textSapaan?.text = "Selamat Siang"
            Log.d(TAG, "onCreate: selamat Siang")
        }else{
            textSapaan?.text = "Selamat Pagi"
            Log.d(TAG, "onCreate: Selamat pagi")
        }
            

        val images : ArrayList<SlideModel> = ArrayList()
        Log.d(TAG, "onCreateImage: ${images.size}")
            images.add(SlideModel(R.string.image_home_1))
            images.add(SlideModel(R.string.image_home_2))
            images.add(SlideModel(R.string.image_home_3))
            images.add(SlideModel(R.string.image_home_4))
        sliderView?.setImageList(images,true)
    }

    private fun setupAction() {
        binding.imgLogout.setOnClickListener {
            showLoading(true)
            signOut()
        }
        binding.imgProfile.setOnClickListener {
            val intentProfile = Intent(this, ProfileActivity::class.java)
            intentProfile.putExtra("poin",poin)
            startActivity(intentProfile)
        }
        binding.etSearch.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
        binding.iconMaps.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
        binding.layoutBgScan.setOnClickListener {
            startActivity(Intent(this, ResultScanActivity::class.java))
        }
        binding.imgMessage.setOnClickListener {
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
        binding.iconCoin.setOnClickListener {
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
        binding.iconAngkutSampah.setOnClickListener {
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
        binding.iconJualSampah.setOnClickListener {
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
        binding.iconRiwayat.setOnClickListener {
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
    private fun setupViewModel() {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        homeViewModel.listBankSampah.observe(this) {
            if (it != null) {
                getAdapter(it)
                showLoading(false)
                Log.d(ContentValues.TAG, "onCreate: ${it[1]}")
            }
        }

        homeViewModel.getBankSampah()

    }

    private fun getAdapter(listBankSampah: ArrayList<ListBankSampah>) {
        val adapter = ListBankSampahAdapter()
        adapter.setList(listBankSampah)
        binding.apply {
            rvRecomendasi.layoutManager = GridLayoutManager(this@HomeActivity, 2)
            rvRecomendasi.adapter = adapter
        }
        adapter.setOnClickCallback(object : ListBankSampahAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ListBankSampah) {
                Intent(this@HomeActivity, DetailBankSampahActivity::class.java).let {
                    it.putExtra(DetailBankSampahActivity.EXTRA_DATA_BANK_SAMPAH, data)
                    startActivity(it)
                }
                Log.d(TAG, "onItemClicked: ${data}")
            }
        })
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }


    private fun showLoading(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object{
        const val TAG = "HomeActivity"
    }
}