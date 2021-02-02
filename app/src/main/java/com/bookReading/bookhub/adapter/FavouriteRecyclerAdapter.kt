package com.bookReading.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bookReading.bookhub.R
import com.bookReading.bookhub.activity.DescriptionActivity
import com.bookReading.bookhub.database.BookEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, val bookList: List<BookEntity>): RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_favorite_single_row, parent, false)

        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val book = bookList[position]
        holder.favBookName.text = book.bookNAme
        holder.favBookAuthor.text = book.bookAuthor
        holder.favBookPrice.text = book.bookPrice
        holder.favBookRating.text = book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.default_book_cover).into(holder.favBookImage)

        holder.favLlContent.setOnClickListener{
            val intent  = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("book_id", book.book_id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    inner class FavouriteViewHolder(view: View): RecyclerView.ViewHolder(view){
        val favBookName: TextView = view.findViewById(R.id.tvFavBookTitle)
        val favBookAuthor: TextView = view.findViewById(R.id.tvFavBookAuthor)
        val favBookPrice: TextView = view.findViewById(R.id.tvFavBookPrice)
        val favBookRating: TextView = view.findViewById(R.id.tvFavBookRating)
        val favBookImage: ImageView = view.findViewById(R.id.imgFavBookImage)
        val favLlContent: LinearLayout = view.findViewById(R.id.llFavContent)
    }
}