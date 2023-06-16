package com.example.herbalscanapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isEmpty
import com.example.herbalscanapplication.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputLayout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btn_register = findViewById<AppCompatButton>(R.id.btn_register)
        btn_register.setOnClickListener{
            register()
        }

        val btn_login = findViewById<TextView>(R.id.tv_have_account)
        btn_login.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    fun register() {
        val et_email = findViewById<EditText>(R.id.et_email)
        val et_pass = findViewById<EditText>(R.id.et_password)
        val et_conf_pass = findViewById<EditText>(R.id.et_confirm_password)

        if(et_email.text.isEmpty()){
            et_email.error = "Kolom nama tidak boleh kosong"
            et_email.requestFocus()
            return
        } else if (et_pass.text.isEmpty()) {
            et_pass.error = "Kolom password tidak boleh kosong"
            et_pass.requestFocus()
            return
        } else if (et_conf_pass.text.isEmpty()) {
            et_conf_pass.error = "Kolom konfirmasi password tidak boleh kosong"
            et_conf_pass.requestFocus()
            return
        } else if (et_conf_pass.text.toString() != et_pass.text.toString()) {
            et_conf_pass.error = "Konfirmasi password anda salah"
            et_conf_pass.requestFocus()
            return
        }

        com.example.herbalscanapplication.api.ApiConfig().getApiService()
                .register(et_email.text.toString(), et_conf_pass.text.toString())
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        Toast.makeText(this@RegisterActivity, "SUCCESS", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RegisterActivity,MainActivity::class.java)
                        startActivity(intent)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(this@RegisterActivity, "ERROR"+t.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
}
