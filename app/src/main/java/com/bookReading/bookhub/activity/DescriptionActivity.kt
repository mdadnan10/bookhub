package com.bookReading.bookhub.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bookReading.bookhub.R
import com.bookReading.bookhub.database.BookDatabase
import com.bookReading.bookhub.database.BookEntity
import com.bookReading.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.lang.Exception

lateinit var detailBookName: TextView
lateinit var detailBookCover: ImageView
lateinit var detailAuthorName: TextView
lateinit var detailBookPrice: TextView
lateinit var detailBookRating: TextView
lateinit var aboutBookDescTitle: TextView
lateinit var bookDesc: TextView
lateinit var btnAddToFav: Button
lateinit var detailProgressLayout: RelativeLayout
lateinit var detailProgressBar: ProgressBar
lateinit var toolbar: Toolbar

var bookId: String? = "100"

class DescriptionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        detailBookName = findViewById(R.id.tvDetailBookName)
        detailBookCover = findViewById(R.id.imgBookCoverDetail)
        detailAuthorName = findViewById(R.id.tvDetailAuthorName)
        detailBookPrice = findViewById(R.id.tvDetailBookPrice)
        detailBookRating = findViewById(R.id.tvDetailBookRating)
        aboutBookDescTitle = findViewById(R.id.tvAboutTheBook)
        bookDesc = findViewById(R.id.tvBookDesc)
        btnAddToFav = findViewById(R.id.btnAddToFav)
        detailProgressLayout = findViewById(R.id.rlDetail)
        detailProgressLayout.visibility = View.VISIBLE
        detailProgressBar = findViewById(R.id.progressBarDetail)
        detailProgressBar.visibility = View.VISIBLE

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(this, "Some unexpected error occurred!", Toast.LENGTH_LONG).show()
        }
        if (bookId == "100") {
            finish()
            Toast.makeText(this, "Some unexpected error occurred!", Toast.LENGTH_LONG).show()
        }

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v1/book/get_book/"

        val jsonParams = JSONObject()
        jsonParams.put("book_id", bookId)

        if (ConnectionManager().checkConnectivity(this)) {
            val jsonRequest =
                @SuppressLint("SetTextI18n")
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            detailProgressLayout.visibility = View.GONE

                            val bookImageUrl = bookJsonObject.getString("image")

                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(detailBookCover)
                            detailBookName.text = bookJsonObject.getString("name")
                            detailAuthorName.text = bookJsonObject.getString("author")
                            detailBookPrice.text = bookJsonObject.getString("price")
                            detailBookRating.text = bookJsonObject.getString("rating")
                            aboutBookDescTitle.text = "About ${bookJsonObject.getString("name")}"
                            bookDesc.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                detailBookName.text.toString(),
                                detailAuthorName.text.toString(),
                                detailBookPrice.text.toString(),
                                detailBookRating.text.toString(),
                                bookDesc.text.toString(),
                                bookImageUrl
                            )

                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get()

                            if (isFav) {
                                btnAddToFav.text = "Remove From Favourites"
                                val favColor = ContextCompat.getColor(
                                    applicationContext,
                                    R.color.colorFavourite
                                )
                                btnAddToFav.setBackgroundColor(favColor)
                            } else {
                                btnAddToFav.text = "Add to Favourites"
                                val noFavColor = ContextCompat.getColor(applicationContext, R.color.teal_700)
                                btnAddToFav.setBackgroundColor(noFavColor)
                            }

                            btnAddToFav.setOnClickListener {
                                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()

                                    if (result) {
                                        Toast.makeText(
                                            this,
                                            "Book Added to Favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        btnAddToFav.text = "Remove From Favourites"
                                        val favColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorFavourite
                                        )
                                        btnAddToFav.setBackgroundColor(favColor)
                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Some Error Occurred!! zoom",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                } else {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 3).execute()

                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this,
                                            "Book Removed From Favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        btnAddToFav.text = "Add to Favourites"
                                        val noFavColor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.teal_700
                                        )
                                        btnAddToFav.setBackgroundColor(noFavColor)

                                    } else {
                                        Toast.makeText(
                                            this,
                                            "Some Error Occurred!!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(this, "Some error occurred!", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Some error occurred!", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(this, "Volley error $it", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "8fe672b65fb8ba"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Turn on mobile data")
            dialog.setMessage("Enable mobile data to receive Book Information.")
            dialog.setPositiveButton("Enable") { _, _ ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { _, _ ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }

    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        mode 1 -> Check Db if the book is favourite or not
        mode 2 -> Save the book into Db as Favourite
        mode 3 -> Remove the Favourite book
         */

        val db = Room.databaseBuilder(context, BookDatabase::class.java, "book-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {

                1 -> {
                    //Check Db if the book is favourite or not
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }

                2 -> {
                    //Save the book into Db as Favourite
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true
                }

                3 -> {
                    //Remove the Favourite book
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }

            }

            return false
        }

    }
}