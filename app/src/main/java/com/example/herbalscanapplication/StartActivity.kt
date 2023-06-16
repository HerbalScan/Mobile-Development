package com.example.herbalscanapplication

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.herbalscanapplication.databinding.ActivityStartBinding
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

class StartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStartBinding
    private var getFile: File? = null

    companion object{
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION){
            if(!allPermissionGranted()){
                Toast.makeText(
                    this, "Tidak mendapatkan izin.", Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSION.all {
        ContextCompat.checkSelfPermission(baseContext, it)  == PackageManager.PERMISSION_GRANTED
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSION,
                REQUEST_CODE_PERMISSION
            )
        }

        binding.btnCamera.setOnClickListener {bukaCamera()}
        binding.btnGallery.setOnClickListener {bukaGallery()}
        binding.btnUpload.setOnClickListener { kirimImage() }
    }

    private fun bukaCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun bukaGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun kirimImage() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)


            val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "file",
                file.name,
                requestImageFile
            )

            val apiService = ApiConfig().getApiService().uploadImage(imageMultipart)
            apiService.enqueue(object : Callback<Informasi> {
                override fun onResponse(
                    call : Call<Informasi>, response: Response<Informasi>
                ) {
                    if (response.isSuccessful){
                        val responseBody = response.body()
                        startResultActicity(responseBody.toString())
                    } else {
                        Toast.makeText(this@StartActivity, "ERROR", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<Informasi>, t: Throwable) {
                    Toast.makeText(this@StartActivity, "gagal memanggil API", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this@StartActivity, "Silakan masukkan berkas gambar terlebih dahulu.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startResultActicity(message: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("message", message)
        startActivity(intent)
    }

    private fun reduceFileImage(file: File): File {
        return file
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewImageView.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@StartActivity)
                getFile = myFile
                binding.previewImageView.setImageURI(uri)
            }
        }
    }
}