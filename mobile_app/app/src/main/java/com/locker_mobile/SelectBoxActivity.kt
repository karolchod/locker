package com.locker_mobile

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.locker_mobile.objects.BoxEntity
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

class SelectBoxActivity : AppCompatActivity() {

    var listItems = ArrayList<BoxEntity>()

    private var adapter = LockerListCustomAdapter(this, listItems)
    lateinit var browseListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_select)

//        println(FirebaseAuth.getInstance().currentUser!!.uid)
//        println(FirebaseAuth.getInstance().currentUser!!.email)

        val browseTopTV: TextView = findViewById(R.id.browseTopTextView)
        browseTopTV.text = "Wybierz skrytkę"


        browseListView = findViewById(R.id.browseListView)
        browseListView.adapter = adapter

        browseListView.setOnItemClickListener { parent, view, position, id ->
            val chosen = adapter.getItem(position)// The item that was clicked
            //todo if any box is empty in this locker
            if (listItems[position].isused=="true") {
                Toast.makeText(
                    this@SelectBoxActivity,
                    "Skrytka jest zajęta!",
                    Toast.LENGTH_SHORT
                ).show()
            }else{

                val alert = AlertDialog.Builder(this)
                alert.setTitle("Czy na pewno otworzyć tą skrytkę?")
                alert.setMessage(intent.getStringExtra("locker_name")+", skrytka "+listItems[position].id)

                alert.setPositiveButton("Otwórz") { _, _ ->
                    val intentN = Intent(this@SelectBoxActivity, AddNewActivity::class.java)

                    intentN.putExtra("recipient_id", intent.getIntExtra("recipient_id",0))
                    intentN.putExtra("recipient_name", intent.getStringExtra("recipient_name"))
                    intentN.putExtra("recipient_username", intent.getStringExtra("recipient_username"))
                    intentN.putExtra("locker_id", intent.getIntExtra("locker_id",0))
                    intentN.putExtra("locker_name", intent.getStringExtra("locker_name"))
                    intentN.putExtra("box_id", listItems[position].id)
                    startActivity(intentN)
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
        listItems.clear()

        adapter.updateListItems(listItems)
        adapter.notifyDataSetChanged()
        //todo uzupelnianie listy na ekranie

        val sharedPrefToken = this@SelectBoxActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return

        val sharedPrefUser = this@SelectBoxActivity.getSharedPreferences(
            getString(R.string.preference_file_key_user), Context.MODE_PRIVATE
        ) ?: return


        Thread(Runnable {
            try {

                val client = OkHttpClient().newBuilder()
                    .build()
                val request: Request = Request.Builder()
                    .url(
                        resources.getString(R.string.ip_addr_api) + "/api/box/boxesinlocker?id="+intent.getIntExtra("locker_id",0)
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
                            this@SelectBoxActivity,
                            "Błąd zapytania, proszę zrestartować aplikację.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val jsonArray = JSONArray(response.body()!!.string())

                    //dodawanie do widoku
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val box = BoxEntity(
                            json.getString("id").toInt(),
                            json.getString("isused"),
                        )
                        listItems.add(box)

                        Collections.sort(listItems,
                            Comparator { o1, o2 -> o1.id.compareTo(o2.id) })

                    }

                    runOnUiThread {
                        adapter.updateListItems(listItems)
                        adapter.notifyDataSetChanged()
                    }

                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@SelectBoxActivity,
                        "Błąd połączenia z serwerem",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }).start()

    }

    private class LockerListCustomAdapter(context: Context, listItems: ArrayList<BoxEntity>) :
        BaseAdapter() {

        private val mContext: Context
        private var listItemsToShow: ArrayList<BoxEntity>

        init {
            mContext = context
            listItemsToShow = listItems
        }

        fun updateListItems(listItems: ArrayList<BoxEntity>) {
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
            nameTV.text = "Skrytka "+listItemsToShow[position].id.toString()

            val descriptionTV = rowMain.findViewById<TextView>(R.id.list_description)
            if(listItemsToShow[position].isused=="true"){
                descriptionTV.text = "Zajęta"
            }else{
                descriptionTV.text = ""
            }


            return rowMain

        }

    }
}