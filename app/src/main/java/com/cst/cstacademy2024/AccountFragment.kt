package com.cst.cstacademy2024

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.cst.cstacademy2024.models.User
import com.cst.cstacademy2024.viewModels.SharedViewModel
import com.cst.cstacademy2024.viewModels.UserViewModel

private var user: User? = null
private lateinit var userViewModel: UserViewModel

class AccountFragment : Fragment() {

    private lateinit var viewModel: SharedViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_account, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        viewModel.user.observe(viewLifecycleOwner) { userVM ->
            user = userVM
            view.findViewById<TextView>(R.id.email).text = userVM.email
            view.findViewById<TextView>(R.id.first_name).text = userVM.firstName
            view.findViewById<TextView>(R.id.last_name).text = userVM.lastName
            view.findViewById<TextView>(R.id.phone_number).text = userVM.phone
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val myPlacesButton = view.findViewById<Button>(R.id.my_places_button)
        myPlacesButton.setOnClickListener {
            findNavController().navigate(R.id.action_accountPlacesFragment_to_myPlacesFragment)
        }

        val saveChangesButton = view.findViewById<Button>(R.id.save_button)
        saveChangesButton.setOnClickListener {
            user?.let {
                it.email = view.findViewById<TextView>(R.id.email).text.toString()
                it.firstName = view.findViewById<TextView>(R.id.first_name).text.toString()
                it.lastName = view.findViewById<TextView>(R.id.last_name).text.toString()
                it.phone = view.findViewById<TextView>(R.id.phone_number).text.toString()

                userViewModel.updateUser(it.id, it.email, it.firstName, it.lastName, it.phone)
            }
        }
    }
}