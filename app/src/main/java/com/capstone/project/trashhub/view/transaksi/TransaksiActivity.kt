package com.capstone.project.trashhub.view.transaksi

import android.annotation.SuppressLint
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.capstone.project.trashhub.R
import com.capstone.project.trashhub.network.model.ListBankSampah
import com.capstone.project.trashhub.view.customview.CustomSpinner
import com.capstone.project.trashhub.view.success.SuccessActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat

class TransaksiActivity : AppCompatActivity(), CustomSpinner.OnSpinnerEventsListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var databaseReferences : DatabaseReference
    private lateinit var nameUserText: TextView
    private lateinit var alamatUser: TextView
    private lateinit var noHpUser: EditText
    private lateinit var catatanUser: EditText
    private lateinit var buttonTransaksi: Button
    private lateinit var spinnerGender: Spinner
    private lateinit var imageTransaksi : ImageButton
    private lateinit var constraintLayoutAlamat : ConstraintLayout

    private lateinit var paymentIntentClientSecret: String
    private lateinit var paymentSheet: PaymentSheet
    private lateinit var idBankSampah : String
    private lateinit var pictUrlBankSampah : String
    private lateinit var namaBankSampah : String
    private lateinit var alamatBankSampah : String
    private lateinit var spinnerValue : String

    private lateinit var mImageUri : Uri
    private lateinit var tanggalPesan : SimpleDateFormat
    private lateinit var calendar: Calendar
    private lateinit var valueDate: String

    val langganan = arrayOf("harian","mingguan","bulanan")

    @SuppressLint("SimpleDateFormat", "WeekBasedYear")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent.getParcelableExtra<ListBankSampah>(GET_ID_BANK_SAMPAH) as ListBankSampah
        setContentView(R.layout.activity_transaksi)
        idBankSampah = data.id
        pictUrlBankSampah = data.imageUrl
        namaBankSampah = data.name
        alamatBankSampah = data.street

//        inisialisasi stripe
        PaymentConfiguration.init(applicationContext,"pk_test_51Kwh9DKdo0NsmQ4f6QW0ytLjaJzSDfg6ouHQEvxnA42KNYQMu9h2NfhF64bbadokDsgCQ4zbzPtGvx8VLkGEfjAn00NZ7kYpnI")

        nameUserText = findViewById(R.id.tv_nama_user_transaksi)
        alamatUser = findViewById(R.id.tv_alamat_lengkap_penerima)
        noHpUser = findViewById(R.id.nohp_edt_text_transaksi)
        catatanUser = findViewById(R.id.catatan_edt_text_transaksi)
        buttonTransaksi = findViewById(R.id.button_next_to_transaksi_detail)
        spinnerGender = findViewById(R.id.spinner_gender_transaksi)
        imageTransaksi = findViewById(R.id.imageView_transaksi)
        constraintLayoutAlamat = findViewById(R.id.constraint_layout_alamat_checkout)
        tanggalPesan = SimpleDateFormat("EE, dd MM YY")
        calendar = Calendar.getInstance()

        valueDate = tanggalPesan.format(calendar.time)

        Log.d("TransaksiActivity", "onCreate: id bank sampahnya adalah ${data.id}")
        Log.d("TransaksiActivity", "onCreate: id bank sampahnya adalah ")

//        Inisialisasi firebase
        auth = FirebaseAuth.getInstance()
        databaseReferences = FirebaseDatabase.getInstance().getReferenceFromUrl("https://trashhub-e7744-default-rtdb.firebaseio.com/")
        fireStore = FirebaseFirestore.getInstance()
        supportActionBar?.elevation = 0f
        buttonTransaksi.isEnabled = false
        spinnerValue = ""
//        spinner
        val arrayAdapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,langganan)
        spinnerGender.adapter = arrayAdapter
        spinnerGender.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (langganan[p2] == "harian"){
                    spinnerValue = "2000"
                }
                else if (langganan[p2] == "mingguan"){
                    spinnerValue = "5000"
                }else if (langganan[p2] == "bulanan"){
                    spinnerValue = "10000"
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        paymentSheet = PaymentSheet(this, ::onPaymentSheetResult)
        imageTransaksi.setOnClickListener {
            openFileChooser()
        }

        fetchPaymentIntent()

        fireStore.collection("Users").whereEqualTo("id",auth.uid.toString())
            .get()
            .addOnSuccessListener {result ->
                for (r in result){
                    alamatUser.text = r.data.get("alamat").toString()
                    nameUserText.text = r.data.get("name").toString()
                    Log.d(TAG, "onCreate: ${r.data.get("alamat")}")
                }
            }
        validationData()

    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.setType("image/*")
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){
            mImageUri = data.data!!

            Picasso.get().load(mImageUri).into(imageTransaksi)
        }
    }

    private fun validationData() {

        buttonTransaksi.setOnClickListener {

            when {
                nameUserText.text == "" -> {
    //                showLoading(false)
                    nameUserText.error = "Nama tidak boleh kosong"
                    return@setOnClickListener
                }
                noHpUser.text.isEmpty() -> {
    //                showLoading(false)
                    noHpUser.error = "No Hp tidak boleh kosong"
                    noHpUser.requestFocus()
                    return@setOnClickListener
                }
                alamatUser.text == "" -> {
                    alamatUser.error = "Alamat tidak boleh kosong"
                    return@setOnClickListener
                }
            }
            onPayClicked()
        }
    }

    private fun getData(idBankSampah: String,nama: String, noHp: String, catatan: String, alamat: String, pictUrlBankSampah:String, spinner: String) {
        val idUser = auth.currentUser?.uid

        databaseReferences.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("transaksi").hasChild(idBankSampah)){
                    Toast.makeText(this@TransaksiActivity,"Bank Sampah Already Exists", Toast.LENGTH_SHORT).show()
//                    showLoading(false)
                }else{
                    Toast.makeText(this@TransaksiActivity,"Bank Sampah Successfully added", Toast.LENGTH_SHORT).show()
                    databaseReferences.child("transaksi").child(idBankSampah).child("id_bank_sampah").setValue(idBankSampah)
                    databaseReferences.child("transaksi").child(idBankSampah).child("id_user").setValue(idUser)
                    databaseReferences.child("transaksi").child(idBankSampah).child("name").setValue(nama)
                    databaseReferences.child("transaksi").child(idBankSampah).child("nama_bank_sampah").setValue(namaBankSampah)
                    databaseReferences.child("transaksi").child(idBankSampah).child("no_hp").setValue(noHp)
                    databaseReferences.child("transaksi").child(idBankSampah).child("banksampah_pict").setValue( pictUrlBankSampah)
                    databaseReferences.child("transaksi").child(idBankSampah).child("bayar_transaksi").setValue(0)
                    databaseReferences.child("transaksi").child(idBankSampah).child("catatan").setValue(catatan)
                    databaseReferences.child("transaksi").child(idBankSampah).child("alamat_user").setValue(alamat)
                    databaseReferences.child("transaksi").child(idBankSampah).child("durasi_transaksi").setValue(spinner)
                    databaseReferences.child("transaksi").child(idBankSampah).child("foto_sampah").setValue(mImageUri.toString())
                    databaseReferences.child("transaksi").child(idBankSampah).child("tanggal_pesan").setValue(valueDate)
                    databaseReferences.child("transaksi").child(idBankSampah).child("alamat_bank_sampah").setValue(alamatBankSampah)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TransaksiActivity, "Error : ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun fetchPaymentIntent() {
        val url = BACKEND_URL +"create-payment-intent"
        val idUser = auth.currentUser?.uid
        val shoppingCartContent = """
            {
                "items": [
                    {"id":"${idUser.toString() + idBankSampah}"}
                ]
            }
        """

        val mediaType = "application/json; charset=utf-8".toMediaType()

        val payMap: MutableMap<String, Any> = HashMap()
        val itemMap: MutableMap<String, Any> = HashMap()
        val itemList: MutableList<Map<String, Any>> = ArrayList()
        payMap["currency"] = "usd" //dont change currency in testing phase otherwise it won't work

        itemMap["id"] = "photo_subscription"
        itemMap["amount"] = spinnerValue
        itemList.add(itemMap)
        payMap["items"] = itemList

        val json = Gson().toJson(payMap)
        val body = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        OkHttpClient()
            .newCall(request)
            .enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    showAlert("Failed to load data", "Error: $e")
                }

                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        showAlert("Failed to load page", "Error: $response")
                    } else {
                        val responseData = response.body?.string()
                        val responseJson = responseData?.let { JSONObject(it) } ?: JSONObject()
                        paymentIntentClientSecret = responseJson.getString("clientSecret")
                        runOnUiThread { buttonTransaksi.isEnabled = true }
                        Log.i(TAG, "Retrieved PaymentIntent")
                    }
                }
            })
    }

    private fun showAlert(title: String, message: String? = null) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
            builder.setPositiveButton("Ok", null)
            builder.create().show()
        }
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this,  message, Toast.LENGTH_LONG).show()
        }
    }

    private fun onPayClicked() {
        val configuration = PaymentSheet.Configuration("Example, Inc.")

        // Present Payment Sheet
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration)
    }

    private fun onPaymentSheetResult(paymentResult: PaymentSheetResult) {
        when (paymentResult) {
            is PaymentSheetResult.Completed -> {
                getData(idBankSampah, nameUserText.text.toString(), noHpUser.text.toString(),  catatanUser.text.toString(), alamatUser.text.toString(), pictUrlBankSampah, spinnerValue)

                showToast("Payment complete!")
                Intent(this,SuccessActivity::class.java).let {
                    startActivity(it)
                }
            }
            is PaymentSheetResult.Canceled -> {
                Log.i(TAG, "Payment canceled!")
            }
            is PaymentSheetResult.Failed -> {
                showAlert("Payment failed", paymentResult.error.localizedMessage)
            }
        }
    }

    companion object{
        val TAG = "TransaksiActivity"
        const val GET_ID_BANK_SAMPAH = "GET_ID_BANK_SAMPAH"
        private const val BACKEND_URL = "https://stripe-learning.herokuapp.com/"
        private const val PICK_IMAGE_REQUEST = 1
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onPopupWindowOpened(spinner: Spinner?) {
        spinnerGender.background = resources.getDrawable(R.drawable.spinner_background_up)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onPopupWindowClosed(spinner: Spinner?) {
        spinnerGender.background = resources.getDrawable(R.drawable.spinner_background_down)
    }
}