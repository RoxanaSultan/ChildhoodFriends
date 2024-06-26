package com.cst.cstacademy2024

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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cst.cstacademy2024.FakeApiService
import com.cst.cstacademy2024.R
import com.cst.cstacademy2024.UsersAdapter
import com.cst.cstacademy2024.adapters.UsersApiAdapter
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.viewModels.SharedViewModel
import com.cst.cstacademy2024.viewModels.UserViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class SearchFragment : Fragment() {
    private lateinit var spinnerCategory: Spinner
    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var userViewModel: UserViewModel
    private lateinit var viewModel: SharedViewModel
    private var usersList: List<User> = emptyList()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(FakeApiService::class.java)
    private lateinit var users: List<User>
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        spinnerCategory = view.findViewById(R.id.dropdown_menu)
        searchEditText = view.findViewById(R.id.search_bar)
        recyclerView = view.findViewById(R.id.search_results_recycler_view)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        viewModel.user.observe(viewLifecycleOwner) { userVM ->
            // Update your UI here with user information
            // For example: textView.text = user.name
            user = userVM
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize ViewModel
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        setupSpinner()
        setupRecyclerView()
        setupSearchEditText()



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
        lifecycleScope.launch {
            try {
                users = userViewModel.getAllUsers()
                usersAdapter = UsersAdapter(usersList) // Initialize with empty list
                usersAdapter.updateList(users)
                recyclerView.adapter = usersAdapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun setupSearchEditText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val category = spinnerCategory.selectedItem.toString()
                val placeQuery = "%${s.toString().toLowerCase(Locale.getDefault())}%"
                search(category, placeQuery)
            }
        })
    }

    private fun search(category: String, placeName: String) {
        // Call the viewModel to get the filtered users based on category and place
        user?.let {
            userViewModel.getUsersByCategoryPlace(category, placeName, it.id).observe(viewLifecycleOwner) { filteredUsers ->
                // Create a HashSet with a custom equality check
                val uniqueUsers = filteredUsers.distinctBy { it.firstName to it.lastName }
                // Update the RecyclerView with the deduplicated users
                usersAdapter.updateList(uniqueUsers)
            }
        }
    }




}
