package com.locker_mobile

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.locker_mobile.objects.LockerEntity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.*
import kotlin.Comparator
//todo import com.google.firebase.auth.FirebaseAuth
//todo import com.google.firebase.firestore.ktx.firestore
//todo import com.google.firebase.ktx.Firebase
//todo import java.util.*
import kotlin.collections.ArrayList

class SelectLockerActivity : AppCompatActivity() {

    var listItems = ArrayList<LockerEntity>()

    private var adapter = LockerListCustomAdapter(this, listItems)
    lateinit var browseListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_select)

//        println(FirebaseAuth.getInstance().currentUser!!.uid)
//        println(FirebaseAuth.getInstance().currentUser!!.email)

        val browseTopTV: TextView = findViewById(R.id.browseTopTextView)
        browseTopTV.text = "Wybierz szafę"


        browseListView = findViewById(R.id.browseListView)
        browseListView.adapter = adapter

        browseListView.setOnItemClickListener { parent, view, position, id ->
            val chosen = adapter.getItem(position)// The item that was clicked
            //todo if any box is empty in this locker
            if (listItems[position].emptyBoxes=="0") {
                Toast.makeText(
                    this@SelectLockerActivity,
                    "Nie ma wolnych skrytek w tej szafie!",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                val intentN = Intent(this@SelectLockerActivity, SelectBoxActivity::class.java)

                intentN.putExtra("recipient_id", intent.getIntExtra("recipient_id",0))
                intentN.putExtra("recipient_name", intent.getStringExtra("recipient_name"))
                intentN.putExtra("recipient_username", intent.getStringExtra("recipient_username"))
                intentN.putExtra("locker_id", listItems[position].id)
                intentN.putExtra("locker_name", listItems[position].name)
                startActivity(intentN)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        listItems.clear()

        adapter.updateListItems(listItems)
        adapter.notifyDataSetChanged()
        //todo uzupelnianie listy na ekranie

        val sharedPrefToken = this@SelectLockerActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return

        val sharedPrefUser = this@SelectLockerActivity.getSharedPreferences(
            getString(R.string.preference_file_key_user), Context.MODE_PRIVATE
        ) ?: return


        Thread(Runnable {
            try {

                val client = OkHttpClient().newBuilder()
                    .build()
                val request: Request = Request.Builder()
                    .url(
                        resources.getString(R.string.ip_addr_api) + "/api/locker/lockers"
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
                            this@SelectLockerActivity,
                            "Błąd zapytania, proszę zrestartować aplikację.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val jsonArray = JSONArray(response.body()!!.string())

                    //dodawanie do widoku
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val locker = LockerEntity(
                            json.getString("id").toInt(),
                            json.getString("name"),
                            "-1"
                        )

                        //todo get number of empty in locker

                        val clientLockerNumRequest = OkHttpClient().newBuilder()
                            .build()
                        val requestLockerNumRequest: Request = Request.Builder()
                            .url(
                                resources.getString(R.string.ip_addr_api) + "/api/box/emptyinlocker?id=" + locker.id
                            )
                            .method("GET", null)
                            .addHeader(
                                "Authorization",
                                "Bearer " + sharedPrefToken.getString("access_token", "")
                            )
                            .build()
                        val responseLockerNumRequest =
                            clientLockerNumRequest.newCall(requestLockerNumRequest).execute()
                        if (responseLockerNumRequest.code() != 200) {
                            runOnUiThread {
                                Toast.makeText(
                                    this@SelectLockerActivity,
                                    "Błąd zapytania, proszę zrestartować aplikację.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            locker.emptyBoxes= responseLockerNumRequest.body()!!.string()
                        }


                        listItems.add(locker)

                        Collections.sort(listItems,
                            Comparator { o1, o2 -> o2.emptyBoxes.compareTo(o1.emptyBoxes) })

                    }
                    runOnUiThread {
                        adapter.updateListItems(listItems)
                        adapter.notifyDataSetChanged()
                    }
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@SelectLockerActivity,
                        "Błąd połączenia z serwerem",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }).start()

    }

    private class LockerListCustomAdapter(context: Context, listItems: ArrayList<LockerEntity>) :
        BaseAdapter() {

        private val mContext: Context
        private var listItemsToShow: ArrayList<LockerEntity>

        init {
            mContext = context
            listItemsToShow = listItems
        }

        fun updateListItems(listItems: ArrayList<LockerEntity>) {
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
            val rowMain = layoutInflater.inflate(R.layout.items_browse_1, viewGroup, false)

            val nameTV = rowMain.findViewById<TextView>(R.id.list_name)
            nameTV.text = listItemsToShow[position].id.toString()+": "+listItemsToShow[position].name

            val descriptionTV = rowMain.findViewById<TextView>(R.id.list_description)
            descriptionTV.text = "Wolnych skrytek: "+listItemsToShow[position].emptyBoxes


            return rowMain

        }

    }
}