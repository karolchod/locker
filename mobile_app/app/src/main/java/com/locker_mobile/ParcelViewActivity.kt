package com.locker_mobile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.dictionaryapp.objects.ParcelEntity
import com.dictionaryapp.objects.Word
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.Comparator

//todo import com.google.firebase.firestore.ktx.firestore
//todo import com.google.firebase.ktx.Firebase


class ParcelViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parcel_view)


        val sharedPrefToken = this@ParcelViewActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return

        val sharedPrefUser = this@ParcelViewActivity.getSharedPreferences(
            getString(R.string.preference_file_key_user), Context.MODE_PRIVATE
        ) ?: return


        val numTV: TextView = findViewById(R.id.parcelviewInfoParcelNumberTextView)
        numTV.text = "Numer przesyłki: " + intent.getIntExtra("parcel_id", 0)
        val recipientTV: TextView = findViewById(R.id.parcelviewInfoParcelRecipientTextView)
        recipientTV.text = "Nadawca: " + intent.getIntExtra(
            "sender_id",
            0
        ) + " - " + intent.getStringExtra("sender_name") + " (" + intent.getStringExtra("sender_username") + ")"
        val lockerTV: TextView = findViewById(R.id.parcelviewInfoParcelLockerTextView)
        lockerTV.text =
            "Szafa: " + intent.getIntExtra("locker_id", 0) + " - " + intent.getStringExtra(
                "locker_name"
            )
        val boxTV: TextView = findViewById(R.id.parcelviewInfoParcelBoxTextView)
        boxTV.text = "Skrytka: " + intent.getIntExtra("box_id", 0)
        val dateTV: TextView = findViewById(R.id.parcelviewInfoParcelDateTextView)
        dateTV.text = "Data nadania: " + intent.getStringExtra("createddate")


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
                            this@ParcelViewActivity,
                            "Błąd zapytania, proszę zrestartować aplikację.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                println(e)
                runOnUiThread {
                    Toast.makeText(
                        this@ParcelViewActivity,
                        "Błąd połączenia z serwerem",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }).start()


        val confirmButton: Button = findViewById(R.id.confirmParcelFinishButton)
        confirmButton.setOnClickListener {

            Thread(Runnable {
                try {

                    val client2 = OkHttpClient().newBuilder()
                        .build()
                    val mediaType2: MediaType? = MediaType.parse("text/plain")
                    val body2 = RequestBody.create(mediaType2, "content")
                    val request2: Request = Request.Builder()
                        .url(
                            resources.getString(R.string.ip_addr_api) + "/api/parcel/finish?id=" + intent.getIntExtra(
                                "parcel_id",
                                0
                            )
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
                                this@ParcelViewActivity,
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
                                    this@ParcelViewActivity,
                                    "Błąd zapytania, proszę zrestartować aplikację.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            //udalo sie zamknac i zapisac przesylke w bazie
                            runOnUiThread {
                                Toast.makeText(
                                    this@ParcelViewActivity,
                                    "Paczka została odebrana",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent =
                                    Intent(this@ParcelViewActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@ParcelViewActivity,
                            "Błąd połączenia z serwerem",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }).start()

        }
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onBackPressed() {
//        val setIntent = Intent(Intent.ACTION_MAIN)
//        setIntent.addCategory(Intent.CATEGORY_HOME)
//        setIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(setIntent)

        val sharedPrefToken = this@ParcelViewActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return

        val sharedPrefUser = this@ParcelViewActivity.getSharedPreferences(
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
                            this@ParcelViewActivity,
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
                        this@ParcelViewActivity,
                        "Błąd połączenia z serwerem",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }).start()


    }


}