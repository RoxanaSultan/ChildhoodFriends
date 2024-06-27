import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cst.cstacademy2024.R
import com.cst.cstacademy2024.models.Place

class MyPlacesAdapter(
    private var items: List<Place> = emptyList(),
    private var onDeleteClickListener: ((Place) -> Unit)? = null
) : RecyclerView.Adapter<MyPlacesAdapter.PlaceViewHolder>() {

    fun setOnDeleteClickListener(listener: (Place) -> Unit) {
        onDeleteClickListener = listener
    }

    fun updateList(newList: List<Place>) {
        items = newList
        notifyDataSetChanged()
        notifyItemRemoved(items.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_place_name)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)

        init {
            deleteButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClickListener?.invoke(items[position])
                }
            }

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClickListener?.invoke(items[position])
                }
            }
        }

        fun bind(place: Place) {
            nameTextView.text = place.name
        }
    }
}