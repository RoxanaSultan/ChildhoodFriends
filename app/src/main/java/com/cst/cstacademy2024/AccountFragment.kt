package com.cst.cstacademy2024

import MyPlacesFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cst.cstacademy2024.models.User

private var user: User? = null

class AccountFragment : Fragment(){
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        user = arguments?.getSerializable("USER") as? User
        // Afișează datele utilizatorului în interfață
        user?.let {
            view.findViewById<TextView>(R.id.email).text = it.email
            view.findViewById<TextView>(R.id.first_name).text = it.firstName
            view.findViewById<TextView>(R.id.last_name).text = it.lastName
            view.findViewById<TextView>(R.id.phone_number).text = it.phone
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myPlacesButton = view.findViewById<Button>(R.id.my_places_button)
        myPlacesButton.setOnClickListener {
            val fragment = MyPlacesFragment()
            val bundle = Bundle()
            bundle.putSerializable("USER", user)
            fragment.arguments = bundle

            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)  // Optional: Adds the transaction to the back stack
            transaction.commit()
        }
    }
}