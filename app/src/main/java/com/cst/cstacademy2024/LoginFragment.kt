package com.cst.cstacademy2024

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.cst.cstacademy2024.BuildConfig

import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.cst.cstacademy2024.database.AppDatabase
import com.cst.cstacademy2024.helpers.extensions.VolleyRequestQueue
import com.cst.cstacademy2024.helpers.extensions.logErrorMessage
import com.cst.cstacademy2024.models.LoginModel
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var userRepository : UserRepository
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        userRepository = UserRepository(AppDatabase.getDatabase(requireContext()).userDao())
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inițializează NavController
        navController = NavHostFragment.findNavController(this)

        val registerButton = view.findViewById<Button>(R.id.btn_register)
        registerButton.setOnClickListener(::goToRegister)

        val loginButton = view.findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener { doLogin() }
    }

    private fun goToRegister(view: View) {
        val action = LoginFragmentDirections.actionFragmentLoginToFragmentRegister()
        findNavController().navigate(action)
    }

	
    private fun doLogin() {
        val username = view?.findViewById<EditText>(R.id.et_user_name_login)?.text?.toString() ?: ""
        val password = view?.findViewById<EditText>(R.id.et_password_login)?.text?.toString() ?: ""

        GlobalScope.launch(Dispatchers.IO) {
            val userExists = userRepository.checkUserExists(username, password)
            withContext(Dispatchers.Main) {
                if (userExists) {
                    // User exists, navigate to MainActivity
                    val user = userRepository.getUser(username, password)
                    navigateToMainActivity(user)
                } else {
                    // Show error message or handle login failure
                    Toast.makeText(requireContext(), "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }


        }
    }

    private fun navigateToMainActivity(user: User?) {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("USER", user)
        startActivity(intent)
        requireActivity().finish() // Close login activity
    }


}