import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cst.cstacademy2024.R
import com.cst.cstacademy2024.UsersAdapter
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.viewModels.UserViewModel

class SearchFragment : Fragment() {
    private lateinit var spinnerCategory: Spinner
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var userViewModel: UserViewModel
    private var usersList: List<User> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        spinnerCategory = view.findViewById(R.id.dropdown_menu)
        searchEditText = view.findViewById(R.id.search_bar)
        recyclerView = view.findViewById(R.id.search_results_recycler_view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSpinner()
        setupRecyclerView()
        setupSearchEditText()

        // Initialize ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

//        // Observe changes in user list
//        userViewModel.getAllUsers().observe(viewLifecycleOwner, Observer { users ->
//            users?.let {
//                usersAdapter.updateList(users)
//            }
//        })
    }

    private fun setupSpinner() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerCategory.adapter = adapter
        }

        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val category = parent.getItemAtPosition(position) as String
                search(category, searchEditText.text.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun setupRecyclerView() {
        usersAdapter = UsersAdapter(emptyList()) // Initialize with empty list
        recyclerView.adapter = usersAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSearchEditText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val category = spinnerCategory.selectedItem.toString()
                search(category, s.toString())
            }
        })
    }

    private fun search(category: String, placeName: String) {
        // Call the viewModel to get the filtered users based on category and place
        userViewModel.getUsersByCategoryPlace(category, placeName).observe(viewLifecycleOwner) { filteredUsers ->
            // Update the RecyclerView with the filtered users
            usersAdapter.updateList(filteredUsers)
        }
    }


}
