package com.locker_mobile

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
//todo import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import okhttp3.Response

import okhttp3.RequestBody

import okhttp3.OkHttpClient




//todo import com.google.firebase.auth.AuthResult
//todo import com.google.firebase.auth.FirebaseAuth
//todo import com.google.firebase.auth.FirebaseUser

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        val et_email: TextInputEditText = findViewById(R.id.etregisteremail)
        val et_password: TextInputEditText = findViewById(R.id.etregisterpassword)

        val registerButton: Button = findViewById(R.id.ForgotButton)
        registerButton.setOnClickListener{
            when{
                TextUtils.isEmpty(et_email.text.toString().trim {it <= ' '}) ->{
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(et_password.text.toString().trim {it <= ' '}) ->{
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {

                    Thread(Runnable {
                        try {
                            val email: String = et_email.text.toString().trim {it <= ' '}
                            val password: String = et_password.text.toString().trim {it <= ' '}

                            val client = OkHttpClient().newBuilder()
                                .build()
                            val mediaType = MediaType.parse("application/json")
                            val body = RequestBody.create(
                                mediaType,
                                "{\r\n        \"id\": 1,\r\n        \"name\": \"$email\",\r\n        \"username\": \"$email\",\r\n        \"password\": \"$password\",\r\n        \"role_id\": 1\r\n    }"
                            )
                            val request: Request = Request.Builder()
                                .url(resources.getString(R.string.ip_addr_api)+"/api/user/register")
                                .method("POST", body)
                                .addHeader("Content-Type", "application/json")
                                .build()
                            val response = client.newCall(request).execute()

                            if (response.code() != 200) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Błąd",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "Utworzono nowe konto użytkownika.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
//                                intent.flags =
//                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                finish()

                                //logowanie
//                                val jsonData: String = response.body()!!.string()
//                                val Jobject = JSONObject(jsonData)

//                                val mPrefs = getPreferences(MODE_PRIVATE)
//                                val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
//                                val gson = Gson()
//                                val json = gson.toJson(Jobject)
//                                prefsEditor.putString("MyObject", json)
//                                prefsEditor.apply()
//                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
//                                intent.flags =
//                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                                startActivity(intent)
//                                finish()
                            }

                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@RegisterActivity,
                                    "Błąd połączenia z serwerem",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }


                    }).start()

                }
            }
        }

        val loginTV: TextView = findViewById(R.id.loginTextView)
        loginTV.setOnClickListener{
            onBackPressed()
        }


    }
}