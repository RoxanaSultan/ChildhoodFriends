import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cst.cstacademy2024.R
import com.cst.cstacademy2024.models.Place
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.viewModels.PlaceUserViewModel
import com.cst.cstacademy2024.viewModels.PlaceViewModel

class MyPlacesFragment : Fragment() {

    private lateinit var recyclerViewAddress: RecyclerView
    private lateinit var recyclerViewSchool: RecyclerView
    private lateinit var recyclerViewHighSchool: RecyclerView
    private lateinit var recyclerViewCollege: RecyclerView
    private lateinit var recyclerViewFavouritePlaces: RecyclerView

    private lateinit var adapterAddress: MyPlacesAdapter
    private lateinit var adapterSchool: MyPlacesAdapter
    private lateinit var adapterHighSchool: MyPlacesAdapter
    private lateinit var adapterCollege: MyPlacesAdapter
    private lateinit var adapterFavouritePlaces: MyPlacesAdapter

    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var placeUserViewModel: PlaceUserViewModel
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retrieve the User object from the arguments
        user = arguments?.getSerializable("USER") as? User
        return inflater.inflate(R.layout.fragment_my_places, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModels
        placeViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)
        placeUserViewModel = ViewModelProvider(this).get(PlaceUserViewModel::class.java)

        // Initialize RecyclerViews
        recyclerViewAddress = view.findViewById(R.id.recycler_view_address)
        recyclerViewSchool = view.findViewById(R.id.recycler_view_school)
        recyclerViewHighSchool = view.findViewById(R.id.recycler_view_high_school)
        recyclerViewCollege = view.findViewById(R.id.recycler_view_college)
        recyclerViewFavouritePlaces = view.findViewById(R.id.recycler_view_favourite_places)

        // Set layout managers
        recyclerViewAddress.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewSchool.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewHighSchool.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewCollege.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewFavouritePlaces.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapters with lambdas
        adapterAddress = MyPlacesAdapter(listOf(), { place -> onDeleteClick(place) })
        adapterSchool = MyPlacesAdapter(listOf(), { place -> onDeleteClick(place) })
        adapterHighSchool = MyPlacesAdapter(listOf(), { place -> onDeleteClick(place) })
        adapterCollege = MyPlacesAdapter(listOf(), { place -> onDeleteClick(place) })
        adapterFavouritePlaces = MyPlacesAdapter(listOf(), { place -> onDeleteClick(place) })

        // Set adapters to RecyclerViews
        recyclerViewAddress.adapter = adapterAddress
        recyclerViewSchool.adapter = adapterSchool
        recyclerViewHighSchool.adapter = adapterHighSchool
        recyclerViewCollege.adapter = adapterCollege
        recyclerViewFavouritePlaces.adapter = adapterFavouritePlaces

        // Load data for each category
        loadData("Addresses")
        loadData("Schools")
        loadData("High Schools")
        loadData("Colleges")
        loadData("Favourite Places")
    }

    private fun onDeleteClick(place: Place) {
        user?.let {
            placeUserViewModel.deletePlaceUser(place.id, it.id)
            placeViewModel.deletePlace(place.id)
        }
    }

    private fun loadData(category: String) {
        placeUserViewModel.getPlacesByCategory(category).observe(viewLifecycleOwner) { placesUser ->
            val places = mutableListOf<Place>()
            for (placeId in placesUser) {
                placeViewModel.getPlaceById(placeId).observe(viewLifecycleOwner) { place ->
                    place?.let {
                        places.add(place)
                        when (category) {
                            "Addresses" -> adapterAddress.updateList(places)
                            "Schools" -> adapterSchool.updateList(places)
                            "High Schools" -> adapterHighSchool.updateList(places)
                            "Colleges" -> adapterCollege.updateList(places)
                            "Favourite Places" -> adapterFavouritePlaces.updateList(places)
                        }
                    }
                }
            }
        }
    }
}