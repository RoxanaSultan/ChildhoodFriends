package com.cst.cstacademy2024

import SearchFragment
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.cst.cstacademy2024.adapters.UsersApiAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.cst.cstacademy2024.helpers.extensions.logErrorMessage
import com.cst.cstacademy2024.models.PlaceUser
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.models.UserAPI
import com.cst.cstacademy2024.viewModels.PlaceUserViewModel
import com.cst.cstacademy2024.viewModels.PlaceViewModel
import com.cst.cstacademy2024.viewModels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var placeUserViewModel: PlaceUserViewModel
    private lateinit var userViewModel: UserViewModel
    private var user: User? = null
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(FakeApiService::class.java)
    private lateinit var usersApiList : List<UserAPI>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user = intent.getSerializableExtra("USER") as? User
        user?.let {
            // Utilizează obiectul User pentru a afișa sau pentru alte operații
            supportActionBar?.title = "Welcome, ${user!!.username}!"
        }

        // Initialize ViewModels
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        placeUserViewModel = ViewModelProvider(this).get(PlaceUserViewModel::class.java)
        placeViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)

        insertAPIUsers()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val selectedFragment: Fragment = when (item.itemId) {
                R.id.nav_account -> {
                    // Creează un nou fragment de AccountFragment
                    val fragment = AccountFragment()

                    // Trimite obiectul User către fragment folosind setArguments()
                    val bundle = Bundle()
                    bundle.putSerializable("USER", user)
                    fragment.arguments = bundle

                    fragment
                }
                R.id.nav_search -> SearchFragment()
                R.id.nav_maps -> {
                    // Creează un nou fragment de AccountFragment
                    val fragment = MapsFragment()

                    // Trimite obiectul User către fragment folosind setArguments()
                    val bundle = Bundle()
                    bundle.putSerializable("USER", user)
                    fragment.arguments = bundle

                    fragment
                }
                else -> AccountFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, selectedFragment)
                .commit()
            true
        }

        // Set default selection
        if (savedInstanceState == null) {
            bottomNavigationView.selectedItemId = R.id.nav_account
        }
    }

    override fun onStart() {
        super.onStart()
        "onStart".logErrorMessage()
    }

    override fun onResume() {
        super.onResume()
        "onResume".logErrorMessage()
    }

    override fun onPause() {
        super.onPause()
        "onPause".logErrorMessage()
    }

    override fun onStop() {
        super.onStop()
        "onStop".logErrorMessage()
    }

    override fun onDestroy() {
        super.onDestroy()
        deleteUsersApi()
        "onDestroy".logErrorMessage()
    }

    private fun insertAPIUsers(){
        lifecycleScope.launch {
            try {
               usersApiList = api.getUsers()
                for (userAPI in usersApiList) {
                    userViewModel.addUser(userAPI)
                    val userLiveData: LiveData<User?> = userViewModel.getUser(userAPI.username, userAPI.password)
                    userLiveData.observe(this@MainActivity, Observer { userInserted ->
                        userInserted?.let {
                            Toast.makeText(
                                this@MainActivity,
                                "User ${it.username} inserted successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            //assignLocationToUser2(it.firstName, it.lastName, user!!.id, it.id)
                        } ?: run {
                            Toast.makeText(
                                this@MainActivity,
                                "Error inserting user. User not found.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })


                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    fun deleteUsersApi(){
        lifecycleScope.launch {
            try {
                placeUserViewModel.deletePlacesAndUsers(usersApiList)
                userViewModel.deleteUsers(usersApiList)
                Toast.makeText(
                    this@MainActivity,
                    "Users deleted successfully!",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun hashNameToNumber(firstname: String, lastname: String, maxNumber: Int): Int {
        val combined = "$firstname$lastname"
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(combined.toByteArray())
        val hashCode = hashBytes.fold(0) { acc, byte -> acc * 31 + byte.toInt() }
        return (hashCode and Int.MAX_VALUE) % maxNumber
    }

    private fun assignLocationToUser2(firstname: String, lastname: String, currentUserId: Int, user2Id: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val placesLiveData: LiveData<List<Int>> =
                placeUserViewModel.getPlacesByUserId(currentUserId)
            placesLiveData.observe(this@MainActivity, Observer { placeIds ->
                placesLiveData.removeObservers(this@MainActivity)

                if (placeIds != null && placeIds.isNotEmpty()) {
                    val totalLocations = placeIds.size
                    val locationIndex = hashNameToNumber(firstname, lastname, totalLocations)
                    val assignedPlaceId = placeIds[locationIndex]

                    // Assuming userViewModel.getUserById(user2Id) returns LiveData<User>
                    val userLiveData: LiveData<User> = userViewModel.getUserById(user2Id)
                    userLiveData.observe(this@MainActivity, Observer { user2 ->
                        user2?.let {
                            // Assuming placeUserViewModel.getCategoryByUserAndPlace(currentUserId, assignedPlaceId) returns LiveData<String>
                            val categoryLiveData: LiveData<String> = placeUserViewModel.getCategoryByUserAndPlace(currentUserId, assignedPlaceId)
                            categoryLiveData.observe(this@MainActivity, Observer { category ->
                                category?.let {
                                    val placeUser = PlaceUser(
                                        placeId = assignedPlaceId,
                                        userId = user2.id,
                                        category = it // Use the observed category string
                                    )

                                    placeUserViewModel.insertPlaceUser(placeUser)
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Location assigned successfully to ${user2.firstName} ${user2.lastName}!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } ?: run {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Category information is missing.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })

                        } ?: run {
                            Toast.makeText(
                                this@MainActivity,
                                "User2 information is missing.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "No locations found for the current user.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}
