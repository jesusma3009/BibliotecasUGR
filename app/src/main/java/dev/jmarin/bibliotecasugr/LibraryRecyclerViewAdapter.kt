package dev.jmarin.bibliotecasugr

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LibraryRecyclerViewAdapter(private val libraryList: List<LibraryData>): RecyclerView.Adapter<LibraryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.list_item, parent, false)
        return LibraryViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        holder.bind(libraryList[position])
    }

    override fun getItemCount(): Int {
        return libraryList.size
    }

}

class LibraryViewHolder(private val view: View) : RecyclerView.ViewHolder(view){
    fun bind(libraryData: LibraryData){
        val nameTextView = view.findViewById<TextView>(R.id.tvLibraryName)
        val occupiedTextView = view.findViewById<TextView>(R.id.tvOccupied)
        val freeTextView = view.findViewById<TextView>(R.id.tvFree)
        nameTextView.text = libraryData.name
        occupiedTextView.text = libraryData.seatsOccupied
        freeTextView.text = libraryData.freePlaces
    }
}