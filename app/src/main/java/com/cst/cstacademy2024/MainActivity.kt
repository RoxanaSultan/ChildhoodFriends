package com.cst.cstacademy2024

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cst.cstacademy2024.adapters.UsersApiAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.cst.cstacademy2024.helpers.extensions.logErrorMessage
import com.cst.cstacademy2024.models.PlaceUser
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.models.UserAPI
import com.cst.cstacademy2024.viewModels.PlaceUserViewModel
import com.cst.cstacademy2024.viewModels.PlaceViewModel
import com.cst.cstacademy2024.viewModels.SharedViewModel
import com.cst.cstacademy2024.viewModels.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var placeUserViewModel: PlaceUserViewModel
    private lateinit var userViewModel: UserViewModel
    private var user: User? = null
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://fakestoreapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val api = retrofit.create(FakeApiService::class.java)
    private lateinit var usersApiList: List<UserAPI>

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.Theme_Childhood)

        super.onCreate(savedInstanceState)

        when (ThemeUtils.loadThemeMode(this)) {
            ThemeUtils.LIGHT_MODE -> setTheme(R.style.Theme_Childhood)
            ThemeUtils.DARK_MODE -> setTheme(R.style.Theme_CSTAcademy2024_Dark)
        }

        setContentView(R.layout.activity_main)
        setupThemeToggle()

        // Receive user object passed from previous activity
        user = intent.getSerializableExtra("USER") as? User
        user?.let {
            supportActionBar?.title = "Welcome, ${it.username}!"
        }

        // Initialize ViewModels
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        placeUserViewModel = ViewModelProvider(this).get(PlaceUserViewModel::class.java)
        placeViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)

        user?.let {
            userViewModel.getUser(it.username, it.password).observe(this, Observer { userObs ->
                // Use the user object here
                if (userObs != null) {
                    this.user = userObs
                    sharedViewModel.setUser(user!!)
                    deleteUsersApi()
                    insertAPIUsers()
                }
            })
        }

        user?.let {

        }


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // Set up Navigation Controller with Bottom Navigation View
        bottomNavigationView.setupWithNavController(navController)

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
        "onDestroy".logErrorMessage()
    }

    private fun setupThemeToggle() {
        val toggleButton: Button = findViewById(R.id.toggle_button)
        toggleButton.setOnClickListener {
            val newTheme = if (ThemeUtils.loadThemeMode(this) == ThemeUtils.LIGHT_MODE) {
                ThemeUtils.DARK_MODE
            } else {
                ThemeUtils.LIGHT_MODE
            }
            ThemeUtils.saveThemeMode(this, newTheme)
            recreate()  // Recreate the activity to apply the new theme
        }
    }

    private fun deleteUsersApi() {
        lifecycleScope.launch {
            try {
                usersApiList = api.getUsers()
                for (userAPI in usersApiList) {
                    userViewModel.getUser(userAPI.username, userAPI.password)
                        .observe(this@MainActivity, Observer { user ->
                            user?.let {
                                placeUserViewModel.deletePlacesAndUsers(it.id)
                                userViewModel.deleteUser(it.id)
                            }
                        })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun insertAPIUsers() {
        lifecycleScope.launch {
            try {
                usersApiList = api.getUsers()

                for (userAPI in usersApiList) {
                    delay(500)
                    val insertedUser = withContext(Dispatchers.IO) {
                        // Insert user into database
                        userAPI.name.firstname = userAPI.name.firstname.capitalize()
                        userAPI.name.lastname = userAPI.name.lastname.capitalize()
                        userViewModel.addUser(userAPI)
                        // Retrieve inserted user synchronously
                        userViewModel.getUserSync(userAPI.username, userAPI.password)
                    }
                }

                for (myUser in userViewModel.getAllUsers()) {
                    if (myUser.id != user!!.id) {
                        assignLocationToUser2(
                            myUser.firstName,
                            myUser.lastName,
                            user!!.id,
                            myUser.id
                        )
                    }
                }

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

    private fun assignLocationToUser2(
        firstname: String,
        lastname: String,
        currentUserId: Int,
        user2Id: Int
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val placesLiveData: LiveData<List<Int>> =
                placeUserViewModel.getPlacesByUserId(currentUserId)
            placesLiveData.observe(this@MainActivity, Observer { placeIds ->
                placesLiveData.removeObservers(this@MainActivity)

                if (placeIds != null && placeIds.isNotEmpty()) {
                    val totalLocations = placeIds.size
                    val locationIndex = hashNameToNumber(firstname, lastname, totalLocations)
                    val assignedPlaceId = placeIds[locationIndex]

                    val userLiveData: LiveData<User> = userViewModel.getUserById(user2Id)
                    userLiveData.observe(this@MainActivity, Observer { user2 ->
                        user2?.let {
                            val categoryLiveData: LiveData<String> =
                                placeUserViewModel.getCategoryByUserAndPlace(
                                    currentUserId,
                                    assignedPlaceId
                                )
                            categoryLiveData.observe(this@MainActivity, Observer { category ->
                                category?.let {
                                    val placeUser = PlaceUser(
                                        placeId = assignedPlaceId,
                                        userId = user2.id,
                                        category = it
                                    )

                                    placeUserViewModel.insertPlaceUser(placeUser)
                                }
                            })

                        }
                    })

                }
            })
        }
    }
}
