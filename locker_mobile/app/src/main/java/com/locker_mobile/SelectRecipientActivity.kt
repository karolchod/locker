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
import com.locker_mobile.objects.UserEntity
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

class SelectRecipientActivity : AppCompatActivity() {

    var listItems = ArrayList<UserEntity>()

    private var adapter = LockerListCustomAdapter(this, listItems)
    lateinit var browseListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browse_select)

//        println(FirebaseAuth.getInstance().currentUser!!.uid)
//        println(FirebaseAuth.getInstance().currentUser!!.email)

        val browseTopTV: TextView = findViewById(R.id.browseTopTextView)
        browseTopTV.text = "Wybierz odbiorcę"


        browseListView = findViewById(R.id.browseListView)
        browseListView.adapter = adapter

        browseListView.setOnItemClickListener { parent, view, position, id ->
            val chosen = adapter.getItem(position)// The item that was clicked
            val intent = Intent(this@SelectRecipientActivity, SelectLockerActivity::class.java)
            intent.putExtra("recipient_id", listItems[position].id)
            intent.putExtra("recipient_name", listItems[position].name)
            intent.putExtra("recipient_username", listItems[position].username)
            startActivity(intent)

        }

    }

    override fun onResume() {
        super.onResume()
        listItems.clear()

        adapter.updateListItems(listItems)
        adapter.notifyDataSetChanged()
        //todo uzupelnianie listy na ekranie

        val sharedPrefToken = this@SelectRecipientActivity.getSharedPreferences(
            getString(R.string.preference_file_key_token), Context.MODE_PRIVATE
        ) ?: return

        val sharedPrefUser = this@SelectRecipientActivity.getSharedPreferences(
            getString(R.string.preference_file_key_user), Context.MODE_PRIVATE
        ) ?: return


        Thread(Runnable {
            try {

                val client = OkHttpClient().newBuilder()
                    .build()
                val request: Request = Request.Builder()
                    .url(
                        resources.getString(R.string.ip_addr_api) + "/api/user/users"
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
                            this@SelectRecipientActivity,
                            "Błąd zapytania, proszę zrestartować aplikację.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    val jsonArray = JSONArray(response.body()!!.string())

                    //dodawanie do widoku
                    for (i in 0 until jsonArray.length()) {
                        val json = jsonArray.getJSONObject(i)
                        val user = UserEntity(
                            json.getString("id").toInt(),
                            json.getString("name"),
                            json.getString("username")
                        )

                        if (user.id != sharedPrefUser.getString("id", "")!!.toInt()) {
                            listItems.add(user)
                            Collections.sort(listItems,
                                Comparator { o1, o2 -> o1.name.compareTo(o2.name) })


                        }
                    }
                    runOnUiThread {
                        adapter.updateListItems(listItems)
                        adapter.notifyDataSetChanged()
                    }

                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@SelectRecipientActivity,
                        "Błąd połączenia z serwerem",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }).start()

    }

    private class LockerListCustomAdapter(context: Context, listItems: ArrayList<UserEntity>) :
        BaseAdapter() {

        private val mContext: Context
        private var listItemsToShow: ArrayList<UserEntity>

        init {
            mContext = context
            listItemsToShow = listItems
        }

        fun updateListItems(listItems: ArrayList<UserEntity>) {
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
            nameTV.text =
                listItemsToShow[position].name

            val descriptionTV = rowMain.findViewById<TextView>(R.id.list_description)
            descriptionTV.text = listItemsToShow[position].username


            return rowMain

        }

    }
}