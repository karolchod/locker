package com.locker_mobile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dictionaryapp.objects.ParcelEntity
import okhttp3.*
import org.json.JSONObject
//todo import com.google.firebase.auth.FirebaseAuth
//todo import com.google.firebase.firestore.ktx.firestore
//todo import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

import okhttp3.OkHttpClient
import org.json.JSONArray


class MainActivity : AppCompatActivity() {

    var listItems = ArrayList<ParcelEntity>()

    private var adapter = ParcelsListCustomAdapter(this, listItems)
    lateinit var parcelsListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        parcelsListView = findViewById(R.id.parcelsListView)
        parcelsListView.adapter = adapter


        val settingsButton: Button = findViewById(R.id.settingsButton)
        settingsButton.setOnClickListener {
            println("settings")
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        val fromMeButton: Button = findViewById(R.id.frommeButton)
        fromMeButton.setOnClickListener {
            println("from me")
            val intent = Intent(this, ParcelsFromMeActivity::class.java)
            startActivity(intent)
        }

        val AddButton: Button = findViewById(R.id.confirmButton)
        AddButton.setOnClickListener {
            println("add")
            val intent = Intent(this, SelectRecipientActivity::class.java)
            startActivity(intent)
        }

        parcelsListView.setOnItemClickListener { parent, view, position, id ->
            val chosen = adapter.getItem(position)// The item that was clicked

            if (!listItems[position].isfinished) {


                val alert = AlertDialog.Builder(this)
                alert.setTitle("Czy na pewno otworzyć tą skrytkę?")
                alert.setMessage(listItems[position].locker_name+", skrytka "+listItems[position].box_id)

                alert.setPositiveButton("Otwórz") { _, _ ->
                    val intent = Intent(this@MainActivity, ParcelViewActivity::class.java)
                    intent.putExtra("parcel_id", listItems[position].id)
                    intent.putExtra("locker_id", listItems[position].locker_id)
                    intent.putExtra("locker_name", listItems[position].locker_name)
                    intent.putExtra("box_id", listItems[position].box_id)
                    intent.putExtra("sender_id", listItems[position].user_sender_id)
                    intent.putExtra("sender_name", listItems[position].user_sender_name)
                    intent.putExtra("sender_username", listItems[position].user_sender_username)
                    intent.putExtra("createddate", listItems[position].createddate)
                    startActivity(intent)
                }
                alert.setNegativeButton("Anuluj") { dialog, _ ->
                    dialog.dismiss()
                }
                alert.setCancelable(false)
                alert.show()


            }

        }

    }

    override fun onResume() {
        super.onResume()

        //todo val db = Firebase.firestore

        listItems.clear()
        adapter.updateListItems(listItems)
        adapter.notifyDataSetChanged()
        //todo uzupelnianie listy na ekranie

        val sharedPrefToken = this@MainActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return

        val sharedPrefUser = this@MainActivity.getSharedPreferences(
            getString(R.string.preference_file_key_user), Context.MODE_PRIVATE
        ) ?: return


        Thread(Runnable {
            try {

                val client = OkHttpClient().newBuilder()
                    .build()
                val request: Request = Request.Builder()
                    .url(
                        resources.getString(R.string.ip_addr_api) + "/api/parcel/to?id=" + sharedPrefUser.getString(
                            "id",
                            ""
                        )
                    )
                    .method("GET", null)
                    .addHeader(
                        "Authorization",
                        "Bearer " + sharedPrefToken.getString("access_token", "")
                    )
                    .build()
                val response = client.newCall(request).execute()

                if (response.code() != 200) {
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Błąd zapytania, proszę zrestartować aplikację.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val jsonArray = JSONArray(response.body()!!.string())

                    //dodawanie do widoku
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val parcel = ParcelEntity(
                            json.getString("id").toInt(),
                            json.getString("box_id").toInt(),
                            json.getString("user_sender_id").toInt(),
                            json.getString("user_recipient_id").toInt(),
                            json.getString("createddate"),
                            json.getString("isfinished").toBoolean(),
                            -1, "", "", "", "", ""
                        )

                        //todo get parcel name and user sender name

                        val clientUserRequest = OkHttpClient().newBuilder()
                            .build()
                        val requestUserRequest: Request = Request.Builder()
                            .url(
                                resources.getString(R.string.ip_addr_api) + "/api/user/appuser?id=" + parcel.user_sender_id
                            )
                            .method("GET", null)
                            .addHeader(
                                "Authorization",
                                "Bearer " + sharedPrefToken.getString("access_token", "")
                            )
                            .build()
                        val responseUserRequest =
                            clientUserRequest.newCall(requestUserRequest).execute()
                        if (responseUserRequest.code() != 200) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Błąd zapytania, proszę zrestartować aplikację.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            val jsonDataUser: String = responseUserRequest.body()!!.string()
                            val JobjectUser = JSONObject(jsonDataUser)
                            parcel.user_sender_name = JobjectUser.get("name").toString()
                            parcel.user_sender_username = JobjectUser.get("username").toString()
                        }

                        val clientLockerRequest = OkHttpClient().newBuilder()
                            .build()
                        val requestLockerRequest: Request = Request.Builder()
                            .url(
                                resources.getString(R.string.ip_addr_api) + "/api/box/lockerbybox?id=" + parcel.box_id
                            )
                            .method("GET", null)
                            .addHeader(
                                "Authorization",
                                "Bearer " + sharedPrefToken.getString("access_token", "")
                            )
                            .build()
                        val responseLockerRequest =
                            clientLockerRequest.newCall(requestLockerRequest).execute()
                        if (responseLockerRequest.code() != 200) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Błąd zapytania, proszę zrestartować aplikację.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            val jsonDataLocker: String = responseLockerRequest.body()!!.string()
                            val JobjectLocker = JSONObject(jsonDataLocker)
                            parcel.locker_id = JobjectLocker.get("id").toString().toInt()
                            parcel.locker_name = JobjectLocker.get("name").toString()
                        }

                        if (parcel.isfinished) {
                            parcel.user_sender_name = parcel.user_sender_name + " (odebrana)"
                        }

                        listItems.add(parcel)

                        Collections.sort(listItems,
                            Comparator { o1, o2 -> o2.id.compareTo(o1.id) })

                        Collections.sort(listItems,
                            Comparator { o1, o2 -> o1.isfinished.compareTo(o2.isfinished) })


                    }
                    runOnUiThread {
                        adapter.updateListItems(listItems)
                        adapter.notifyDataSetChanged()
                    }


                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Błąd połączenia z serwerem",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }).start()

    }

    private class ParcelsListCustomAdapter(context: Context, listItems: ArrayList<ParcelEntity>) :
        BaseAdapter() {

        private val mContext: Context
        private var listItemsToShow: ArrayList<ParcelEntity>

        init {
            mContext = context
            listItemsToShow = listItems
        }

        fun updateListItems(listItems: ArrayList<ParcelEntity>) {
            listItemsToShow = listItems
        }

        //how many rows in list
        override fun getCount(): Int {
            return listItemsToShow.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return listItemsToShow[position].id
        }

        //rendering each row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
//            val textView = TextView(mContext)
//            textView.text = "Row for listview"
//            return textView

            val layoutInflater = LayoutInflater.from(mContext)
            val rowMain = layoutInflater.inflate(R.layout.items_list_1, viewGroup, false)

            val nameTV = rowMain.findViewById<TextView>(R.id.list_name)
            nameTV.text =
                "Nr " + listItemsToShow[position].id.toString() + " od " + listItemsToShow[position].user_sender_id + ": " + listItemsToShow[position].user_sender_name

            val descriptionTV = rowMain.findViewById<TextView>(R.id.list_description)
            descriptionTV.text =
                listItemsToShow[position].createddate + ", Szafa " + listItemsToShow[position].locker_id + ": " + listItemsToShow[position].locker_name + ", Skrytka: " + listItemsToShow[position].box_id

            return rowMain

        }

    }

}