package com.cst.cstacademy2024

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class RegisterFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Find the Sign In button by its ID
        val signInButton: Button = view.findViewById(R.id.btn_signin)

        // Set an OnClickListener on the Sign In button
        signInButton.setOnClickListener {
            // Create an Intent to start MainActivity
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
