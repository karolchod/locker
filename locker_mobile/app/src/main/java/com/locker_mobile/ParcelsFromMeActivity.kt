package com.locker_mobile

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
import okhttp3.Response

import okhttp3.OkHttpClient
import org.json.JSONArray


class ParcelsFromMeActivity : AppCompatActivity() {

    var listItems = ArrayList<ParcelEntity>()

    private var adapter = ParcelsListCustomAdapter(this, listItems)
    lateinit var parcelsListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fromme)

        parcelsListView = findViewById(R.id.parcelsListView)
        parcelsListView.adapter = adapter

//        parcelsListView.setOnItemClickListener { parent, view, position, id ->
//            val chosen = adapter.getItem(position)// The item that was clicked
//
//            Toast.makeText(
//                this@ParcelsFromMeActivity,
//                "clicked " + listItems[position].id,
//                Toast.LENGTH_SHORT
//            ).show()
//
//
//            val intent = Intent(this@ParcelsFromMeActivity, ParcelViewActivity::class.java)
//            intent.putExtra("dictionary_id", listItems[position].id)
//            startActivity(intent)
//
//        }

    }

    override fun onResume() {
        super.onResume()

        //todo val db = Firebase.firestore

        listItems.clear()
        adapter.updateListItems(listItems)
        adapter.notifyDataSetChanged()
        //todo uzupelnianie listy na ekranie

        val sharedPrefToken = this@ParcelsFromMeActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return

        val sharedPrefUser = this@ParcelsFromMeActivity.getSharedPreferences(
            getString(R.string.preference_file_key_user), Context.MODE_PRIVATE
        ) ?: return


        Thread(Runnable {
            try {

                val client = OkHttpClient().newBuilder()
                    .build()
                val request: Request = Request.Builder()
                    .url(
                        resources.getString(R.string.ip_addr_api) + "/api/parcel/from?id=" + sharedPrefUser.getString(
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
                            this@ParcelsFromMeActivity,
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
                            -1, "","","","",""
                        )

                        //todo get parcel name and user recipient name

                        val clientUserRequest = OkHttpClient().newBuilder()
                            .build()
                        val requestUserRequest: Request = Request.Builder()
                            .url(
                                resources.getString(R.string.ip_addr_api) + "/api/user/appuser?id=" + parcel.user_recipient_id
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
                                    this@ParcelsFromMeActivity,
                                    "Błąd zapytania, proszę zrestartować aplikację.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            val jsonDataUser: String = responseUserRequest.body()!!.string()
                            val JobjectUser = JSONObject(jsonDataUser)
                            parcel.user_recipient_name = JobjectUser.get("name").toString()
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
                                    this@ParcelsFromMeActivity,
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

                        if(parcel.isfinished){
                            parcel.user_recipient_name=parcel.user_recipient_name+" (odebrana)"
                        }

                        listItems.add(parcel)

                        Collections.sort(listItems,
                            Comparator { o1, o2 -> o2.id.compareTo(o1.id) })
//
//                        Collections.sort(listItems,
//                            Comparator { o1, o2 -> o2.createddate.compareTo(o1.createddate) })
//
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
                        this@ParcelsFromMeActivity,
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
            nameTV.text = "Nr "+listItemsToShow[position].id.toString()+" do " +listItemsToShow[position].user_recipient_id+": "+ listItemsToShow[position].user_recipient_name

            val descriptionTV = rowMain.findViewById<TextView>(R.id.list_description)
            descriptionTV.text =
                listItemsToShow[position].createddate + ", Szafa " + listItemsToShow[position].locker_id+": "+listItemsToShow[position].locker_name + ", Skrytka: " + listItemsToShow[position].box_id

            return rowMain

        }

    }

}