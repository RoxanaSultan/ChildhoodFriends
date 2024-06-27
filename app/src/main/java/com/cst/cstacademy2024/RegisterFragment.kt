package com.cst.cstacademy2024

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.cst.cstacademy2024.models.PlaceUser
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.viewModels.PlaceUserViewModel
import com.cst.cstacademy2024.viewModels.UserViewModel

class RegisterFragment : Fragment() {

    private lateinit var userViewModel: UserViewModel
    private lateinit var placeUserViewModel: PlaceUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        placeUserViewModel = ViewModelProvider(this).get(PlaceUserViewModel::class.java)

        val usernameField: EditText = view.findViewById(R.id.et_username)
        val passwordField: EditText = view.findViewById(R.id.et_password)
        val firstNameField: EditText = view.findViewById(R.id.et_firstname)
        val lastNameField: EditText = view.findViewById(R.id.et_lastname)
        val emailField: EditText = view.findViewById(R.id.et_email)
        val phoneField: EditText = view.findViewById(R.id.et_phone)
        val signInButton: Button = view.findViewById(R.id.btn_signin)

        signInButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()
            val firstName = firstNameField.text.toString()
            val lastName = lastNameField.text.toString()
            val email = emailField.text.toString()
            val phone = phoneField.text.toString()

            if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(activity, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(activity, "Invalid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.length != 10 || !phone.matches(Regex("[0-9]+"))) {
                Toast.makeText(activity, "Invalid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launchWhenStarted {
                if (userViewModel.checkUserExists(username)) {
                    Toast.makeText(activity, "Username already exists", Toast.LENGTH_SHORT).show()
                } else {
                    val user = User(
                        username = username,
                        password = password,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        phone = phone
                    )
                    userViewModel.insertUser(user)

                    // Navigate to MainActivity
                    val intent = Intent(activity, MainActivity::class.java).apply {
                        putExtra("USER", user)  // Passing User object to MainActivity
                    }
                    startActivity(intent)
                }
            }
        }
    }
}
