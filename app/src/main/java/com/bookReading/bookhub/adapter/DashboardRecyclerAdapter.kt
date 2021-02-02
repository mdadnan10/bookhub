package com.bookReading.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bookReading.bookhub.R
import com.bookReading.bookhub.activity.DescriptionActivity
import com.bookReading.bookhub.model.Book
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recycler_dashboard_single_row.view.*

class DashboardRecyclerAdapter(val context: Context, val itemList: List<Book>): RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row, parent, false)

        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val book = itemList[position]
        holder.bookName.text = book.bookName
        holder.authorName.text = book.bookAuthor
        holder.price.text = book.bookPrice
        holder.ratting.text = book.bookRatting
//        holder.bookImage.setImageResource(book.bookImage)
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.bookImage)

        holder.llContent.setOnClickListener{
            val intent  = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("book_id", book.bookId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class DashboardViewHolder(view: View): RecyclerView.ViewHolder(view){
        val bookName: TextView = view.findViewById(R.id.tvRecyclerRowItem)
        val bookImage: ImageView = view.findViewById(R.id.imgBookImage)
        val authorName: TextView = view.findViewById(R.id.tvBookAuthor)
        val price: TextView = view.findViewById(R.id.tvBookPrice)
        val ratting: TextView = view.findViewById(R.id.tvBookRating)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }
}