package com.locker_mobile

//todo import com.google.android.gms.tasks.OnCompleteListener
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import okhttp3.*
import org.json.JSONObject

import okhttp3.Response

import okhttp3.OkHttpClient


//import java.awt.PageAttributes.MediaType


//import com.google.firebase.auth.AuthResult
//import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val et_email: TextInputEditText = findViewById(R.id.etloginemail)
        val et_password: TextInputEditText = findViewById(R.id.etloginpassword)


        val sharedPrefUser = this@LoginActivity.getSharedPreferences(
            getString(R.string.preference_file_key_user), Context.MODE_PRIVATE
        ) ?: return
        val sharedPrefToken = this@LoginActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return

        if (sharedPrefToken.getString("refresh_token", "") != "") {
            Thread(Runnable {
                try {
                    val client = OkHttpClient().newBuilder()
                        .build()
                    val request: Request = Request.Builder()
                        .url(resources.getString(R.string.ip_addr_api) + "/api/refreshtoken")
                        .method("GET", null)
                        .addHeader(
                            "Authorization",
                            "Bearer " + sharedPrefToken.getString("refresh_token", "")
                        )
                        .build()
                    val response = client.newCall(request).execute()
                    if (response.code() == 200) {
//                zaloguj ponownie
                        val jsonData: String = response.body()!!.string()
                        val Jobject = JSONObject(jsonData)


                        val sharedPref = this@LoginActivity?.getSharedPreferences(
                            getString(R.string.preference_file_key_token),
                            Context.MODE_PRIVATE
                        ) ?: return@Runnable
                        with(sharedPref.edit()) {
                            putString(
                                "access_token",
                                Jobject.get("access_token").toString()
                            )
                            apply()
                        }

                        getSaveUserData()

                        runOnUiThread {

                            val intent =
                                Intent(this@LoginActivity, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        //usuwanie zapisanych danych ktore wygasly
                        with(sharedPrefUser.edit()) {
                            clear()
                            apply()
                        }
                        with(sharedPrefToken.edit()) {
                            clear()
                            apply()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            "Błąd połączenia z serwerem",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                }
            }).start()
        }
//        if (FirebaseAuth.getInstance().currentUser != null) {
//            val intent = Intent(this@LoginActivity, MainActivity::class.java)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            intent.putExtra("user_id", FirebaseAuth.getInstance().currentUser!!.uid)
//            intent.putExtra("email_id", FirebaseAuth.getInstance().currentUser!!.email)
//            startActivity(intent)
//            finish()
//        }

        val loginButton: Button = findViewById(R.id.LoginButton)
        loginButton.setOnClickListener {
            when {
                TextUtils.isEmpty(
                    et_email.text.toString()
                        .trim { it <= ' ' }) || !android.util.Patterns.EMAIL_ADDRESS.matcher(
                    et_email.text.toString().trim { it <= ' ' }).matches() -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Proszę wpisać email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Proszę wpisać hasło.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    Thread(Runnable {
                        try {
                            val email: String = et_email.text.toString().trim { it <= ' ' }
                            val password: String = et_password.text.toString().trim { it <= ' ' }

                            val client: OkHttpClient = OkHttpClient().newBuilder()
                                .build()
                            val mediaType: MediaType? =
                                MediaType.parse("application/x-www-form-urlencoded")
                            val body: RequestBody =
                                RequestBody.create(mediaType, "username=$email&password=$password")
                            val request: Request = Request.Builder()
                                .url(resources.getString(R.string.ip_addr_api) + "/api/login")
                                .method("POST", body)
                                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                                .build()
                            val response: Response = client.newCall(request).execute()

                            if (response.code() != 200) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Błędny email lub hasło",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                //logowanie

                                val jsonData: String = response.body()!!.string()
                                val Jobject = JSONObject(jsonData)
//                                val Jarray = Jobject.getJSONArray("employees")
//                                for (i in 0 until Jarray.length()) {
//                                    val `object` = Jarray.getJSONObject(i)
//                                }

                                //zaloguj
                                val sharedPref2 = this@LoginActivity?.getSharedPreferences(
                                    getString(R.string.preference_file_key_token),
                                    Context.MODE_PRIVATE
                                ) ?: return@Runnable
                                with(sharedPref2.edit()) {
                                    putString(
                                        "access_token",
                                        Jobject.get("access_token").toString()
                                    )
                                    apply()
                                }

                                with(sharedPref2.edit()) {
                                    putString(
                                        "refresh_token",
                                        Jobject.get("refresh_token").toString()
                                    )
                                    apply()
                                }
                                getSaveUserData() //pobranie danych o użytkowniku
                                runOnUiThread {

                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                    finish()
                                }

                            }


                        } catch (e: Exception) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Błąd połączenia z serwerem",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }


                    }).start()

                }
            }
        }


        val registerTV: TextView = findViewById(R.id.registerTextView)
        registerTV.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

//        val forgotTV: TextView = findViewById(R.id.forgotpassTextView)
//        forgotTV.setOnClickListener {
//            val intent = Intent(this, ForgotPasswordActivity::class.java)
//            startActivity(intent)
//        }


    }

    fun getSaveUserData() {

        val sharedPref = this@LoginActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return
//        Thread(Runnable {
        try {

            val client = OkHttpClient().newBuilder()
                .build()
            val request = Request.Builder()
                .url(resources.getString(R.string.ip_addr_api) + "/api/user/bytoken")
                .method("GET", null)
                .addHeader(
                    "Authorization",
                    "Bearer " + sharedPref.getString("access_token", "")
                )
                .build()
            val response = client.newCall(request).execute()
            if (response.code() == 200) {
                val jsonData: String = response.body()!!.string()
                val Jobject = JSONObject(jsonData)
                runOnUiThread {
                    //zapis danych o uzytkowniku
                    val sharedPref2 = this@LoginActivity?.getSharedPreferences(
                        getString(R.string.preference_file_key_user),
                        Context.MODE_PRIVATE
                    ) ?: return@runOnUiThread
                    with(sharedPref2.edit()) {
                        putString(
                            "id",
                            Jobject.get("id").toString()
                        )
                        apply()
                    }
                    with(sharedPref2.edit()) {
                        putString(
                            "name",
                            Jobject.get("name").toString()
                        )
                        apply()
                    }
                    with(sharedPref2.edit()) {
                        putString(
                            "username",
                            Jobject.get("username").toString()
                        )
                        apply()
                    }
                    with(sharedPref2.edit()) {
                        putString(
                            "role_id",
                            Jobject.get("role_id").toString()
                        )
                        apply()
                    }

                }
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(
                    this@LoginActivity,
                    "Błąd połączenia z serwerem",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
//        }).start()
    }


}