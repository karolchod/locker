package com.locker_mobile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dictionaryapp.objects.ParcelEntity
import com.google.android.material.textfield.TextInputEditText
import com.locker_mobile.objects.LockerEntity
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.*
import kotlin.Comparator
import okhttp3.RequestBody


//todo import com.google.firebase.auth.FirebaseAuth
//todo import com.google.firebase.firestore.ktx.firestore
//todo import com.google.firebase.ktx.Firebase


class AddNewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new)

        val recipientTV: TextView = findViewById(R.id.addInfoParcelRecipientTextView)
        recipientTV.text = "Odbiorca: " + intent.getIntExtra(
            "recipient_id",
            0
        ) + " - " + intent.getStringExtra("recipient_name") + " (" + intent.getStringExtra("recipient_username") + ")"
        val lockerTV: TextView = findViewById(R.id.addInfoParcelLockerTextView)
        lockerTV.text =
            "Szafa: " + intent.getIntExtra("locker_id", 0) + " - " + intent.getStringExtra(
                "locker_name"
            )
        val boxTV: TextView = findViewById(R.id.addInfoParcelBoxTextView)
        boxTV.text = "Skrytka: " + intent.getIntExtra("box_id", 0)

        val sharedPrefToken = this@AddNewActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return

        val sharedPrefUser = this@AddNewActivity.getSharedPreferences(
            getString(R.string.preference_file_key_user), Context.MODE_PRIVATE
        ) ?: return

        Thread(Runnable {
            try {
                //otworz skrytke
                val client = OkHttpClient().newBuilder()
                    .build()
                val mediaType: MediaType? = MediaType.parse("text/plain")
                val body = RequestBody.create(mediaType, "")
                val request: Request = Request.Builder()
                    .url(
                        resources.getString(R.string.ip_addr_api) + "/api/box/openbox?id=" + intent.getIntExtra(
                            "box_id",
                            0
                        )
                    )
                    .method("POST", body)
                    .addHeader(
                        "Authorization",
                        "Bearer " + sharedPrefToken.getString("access_token", "")
                    )
                    .build()
                val response = client.newCall(request).execute()

                if (response.code() != 200) {
                    runOnUiThread {
                        Toast.makeText(
                            this@AddNewActivity,
                            "Błąd zapytania, proszę zrestartować aplikację.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                println(e)
                runOnUiThread {
                    Toast.makeText(
                        this@AddNewActivity,
                        "Błąd połączenia z serwerem",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }).start()


        val confirmButton: Button = findViewById(R.id.confirmButton)
        confirmButton.setOnClickListener {

            val alert = AlertDialog.Builder(this)
            alert.setTitle("Czy na pewno zamknąć tą skrytkę?")
            alert.setMessage("Dostęp do skrytki zostanie przekazany odbiorcy.")

            alert.setPositiveButton("Zamknij") { _, _ ->
                Thread(Runnable {
                    try {

                        val content = "{\"id\": 1,\"box_id\": " + intent.getIntExtra(
                            "box_id",
                            0
                        ) + ",\"user_sender_id\": " + sharedPrefUser.getString(
                            "id",
                            ""
                        ) + ",\"user_recipient_id\": " + intent.getIntExtra(
                            "recipient_id",
                            0
                        ) + ",\"createddate\": \"2022-01-08\",\"isfinished\": true}"


                        val client2 = OkHttpClient().newBuilder()
                            .build()
                        val mediaType2: MediaType? = MediaType.parse("application/json")
                        val body2 = RequestBody.create(mediaType2, content)
                        //id, created i isfinished sa wymagane, ale nie maja znaczenia
                        val request2: Request = Request.Builder()
                            .url(
                                resources.getString(R.string.ip_addr_api) + "/api/parcel/create"
                            )
                            .method("POST", body2)
                            .addHeader(
                                "Authorization",
                                "Bearer " + sharedPrefToken.getString("access_token", "")
                            )
                            .build()
                        val response2 = client2.newCall(request2).execute()

                        if (response2.code() != 200) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@AddNewActivity,
                                    "Błąd zapytania, proszę zrestartować aplikację.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }


                        } else {

                            //zamknij skrytke
                            val client = OkHttpClient().newBuilder()
                                .build()
                            val mediaType: MediaType? = MediaType.parse("text/plain")
                            val body = RequestBody.create(mediaType, "")
                            val request: Request = Request.Builder()
                                .url(
                                    resources.getString(R.string.ip_addr_api) + "/api/box/closebox?id=" + intent.getIntExtra(
                                        "box_id",
                                        0
                                    )
                                )
                                .method("POST", body)
                                .addHeader(
                                    "Authorization",
                                    "Bearer " + sharedPrefToken.getString("access_token", "")
                                )
                                .build()
                            val response = client.newCall(request).execute()

                            if (response.code() != 200) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@AddNewActivity,
                                        "Błąd zapytania, proszę zrestartować aplikację.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                //udalo sie zamknac i zapisac przesylke w bazie
                                runOnUiThread {
                                    Toast.makeText(
                                        this@AddNewActivity,
                                        "Paczka została nadana",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent =
                                        Intent(this@AddNewActivity, MainActivity::class.java)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Toast.makeText(
                                this@AddNewActivity,
                                "Błąd połączenia z serwerem",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }).start()
            }
            alert.setNegativeButton("Anuluj") { dialog, _ ->
                dialog.dismiss()
            }
            alert.setCancelable(false)
            alert.show()





        }


    }

    override fun onBackPressed() {
//        val setIntent = Intent(Intent.ACTION_MAIN)
//        setIntent.addCategory(Intent.CATEGORY_HOME)
//        setIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(setIntent)

        val sharedPrefToken = this@AddNewActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return

        val sharedPrefUser = this@AddNewActivity.getSharedPreferences(
            getString(R.string.preference_file_key_user), Context.MODE_PRIVATE
        ) ?: return

        Thread(Runnable {
            try {
                //zamknij skrytke
                val client = OkHttpClient().newBuilder()
                    .build()
                val mediaType: MediaType? = MediaType.parse("text/plain")
                val body = RequestBody.create(mediaType, "")
                val request: Request = Request.Builder()
                    .url(
                        resources.getString(R.string.ip_addr_api) + "/api/box/closebox?id=" + intent.getIntExtra(
                            "box_id",
                            0
                        )
                    )
                    .method("POST", body)
                    .addHeader(
                        "Authorization",
                        "Bearer " + sharedPrefToken.getString("access_token", "")
                    )
                    .build()
                val response = client.newCall(request).execute()

                if (response.code() != 200) {
                    runOnUiThread {
                        Toast.makeText(
                            this@AddNewActivity,
                            "Błąd zapytania, proszę zrestartować aplikację.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    //jesli udalo sie zamknac skrytke, cofnij do poprzedniego widoku

                    runOnUiThread { super.onBackPressed() }

                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@AddNewActivity,
                        "Błąd połączenia z serwerem",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }).start()


    }


}
