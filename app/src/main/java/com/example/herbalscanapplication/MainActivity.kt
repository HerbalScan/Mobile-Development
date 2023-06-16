package com.example.herbalscanapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.herbalscanapplication.databinding.ActivityMainBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_login = findViewById<TextView>(R.id.btn_login)
        btn_login.setOnClickListener{
            login()
        }

        val tv_register = findViewById<TextView>(R.id.tv_havent_account)
        tv_register.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        val et_email = findViewById<EditText>(R.id.et_email)
        val et_pass = findViewById<EditText>(R.id.et_password)
        val pb = findViewById<ProgressBar>(R.id.pb)

        if(et_email.text.isEmpty()){
            et_email.error = "Kolom nama tidak boleh kosong"
            et_email.requestFocus()
            return
        } else if (et_pass.text.isEmpty()) {
            et_pass.error = "Kolom password tidak boleh kosong"
            et_pass.requestFocus()
            return
        }

        pb.visibility = View.VISIBLE
        com.example.herbalscanapplication.api.ApiConfig().getApiService()
            .login(et_email.text.toString(), et_pass.text.toString())
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>){
                    pb.visibility = View.GONE
                    val intent = Intent(this@MainActivity,HomeActivity::class.java)
                    startActivity(intent)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    pb.visibility = View.GONE
                    Toast.makeText(this@MainActivity, "Error"+t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }
}