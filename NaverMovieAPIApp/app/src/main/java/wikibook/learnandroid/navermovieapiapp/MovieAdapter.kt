package wikibook.learnandroid.navermovieapiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(private val datalist: List<Movie>, val ctx: Context) :RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    class MovieViewHolder(val view: View) : RecyclerView.ViewHolder(view){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        var movieTitle = holder.view.findViewById<TextView>(R.id.movie_title)
        var movieRating = holder.view.findViewById<RatingBar>(R.id.movie_rating)

        movieTitle.text = datalist[position].title
        movieRating.rating = datalist[position].rating!!.toFloat()
    }

    override fun getItemCount(): Int {
        return datalist.size
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.movie_item
    }
}