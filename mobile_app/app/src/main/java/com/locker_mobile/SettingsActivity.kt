package com.locker_mobile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.locker_mobile.objects.BoxEntity
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
//todo import com.google.firebase.auth.FirebaseAuth
//todo import com.google.firebase.auth.ktx.auth
//todo import com.google.firebase.firestore.ktx.firestore
//todo import com.google.firebase.ktx.Firebase
import java.util.*

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPrefUser = this@SettingsActivity.getSharedPreferences(
            getString(R.string.preference_file_key_user), Context.MODE_PRIVATE) ?: return

        val sharedPrefToken = this@SettingsActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE) ?: return

        val idTV: TextView = findViewById(R.id.idTextView)
        idTV.text = "Numer: " + sharedPrefUser.getString("id", "")
        val nameTV: TextView = findViewById(R.id.nameTextView)
        nameTV.text = "Nazwa: " + sharedPrefUser.getString("name", "")
        val usernameTV: TextView = findViewById(R.id.usernameTextView)
        usernameTV.text = "Login: " + sharedPrefUser.getString("username", "")


        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener {

            with(sharedPrefUser.edit()) {
                clear()
                apply()
            }
            with(sharedPrefToken.edit()) {
                clear()
                apply()
            }

            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

//        val newusersButton: Button = findViewById(R.id.newUsersButton)
//        newusersButton.isVisible = false
//
//        newusersButton.setOnClickListener {
//            val intent = Intent(this, AcceptNewUsersActivity::class.java)
//            startActivity(intent)
//        }


//        Thread(Runnable {
//            try {
//
//                val client = OkHttpClient().newBuilder()
//                    .build()
//                val request: Request = Request.Builder()
//                    .url(
//                        resources.getString(R.string.ip_addr_api) + "/api/user/roles"
//                    )
//                    .method("GET", null)
//                    .addHeader(
//                        "Authorization",
//                        "Bearer " + sharedPrefToken.getString("access_token", "")
//                    )
//                    .build()
//                val response = client.newCall(request).execute()
//
//                if (response.code() != 200) {
//                    runOnUiThread {
//                        Toast.makeText(
//                            this@SettingsActivity,
//                            "Błąd zapytania, proszę zrestartować aplikację.",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                } else {
//                    val jsonArray = JSONArray(response.body()!!.string())
//
//                    for (i in 0 until jsonArray.length()) {
//                        val json = jsonArray.getJSONObject(i)
//                        if(json.getString("id").toInt()== sharedPrefUser.getString("role_id", "")!!.toInt()){
//                            if(json.getString("name")=="ROLE_ADMIN"){
//                                runOnUiThread {
//                                    newusersButton.isVisible=true
//                                }
//                                break
//                            }
//                        }
//                    }
//                }
//
//            } catch (e: Exception) {
//                runOnUiThread {
//                    Toast.makeText(
//                        this@SettingsActivity,
//                        "Błąd połączenia z serwerem",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        }).start()

    }

}