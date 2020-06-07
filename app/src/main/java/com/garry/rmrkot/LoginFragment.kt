package com.garry.rmrkot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.garry.rmrkot.databinding.FragmentLoginBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*
import java.util.*


class LoginFragment : Fragment() {

    var emailIn: TextInputLayout? = null
    var passwordIn: TextInputLayout? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        (activity as MainActivity).menu?.visibility = View.GONE
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container,false)
        binding.swapToSignUpButton.setOnClickListener (
            Navigation.createNavigateOnClickListener(R.id.action_loginFragment_to_signUpFragment)
        )
        (activity as MainActivity).mAuth.signOut()
        emailIn = binding.loginEmailET
        passwordIn = binding.loginPasswordET

        //signIn onClickListener
        binding.logInButton.setOnClickListener {
            val email = binding.loginEmailET.editText?.text.toString().toLowerCase(Locale.ROOT)
            login(email) }

        return binding.root
    }

    private fun login(email:String) {
        if(emailIn?.editText?.text.toString().equals("") or passwordIn?.editText?.text.toString().equals("")) {
            Toast.makeText(context, "Email and Password must not be empty", Toast.LENGTH_SHORT).show()
        } else {
            logInButton.isEnabled = false
            (activity as MainActivity).mAuth.signInWithEmailAndPassword(email,
                passwordIn?.editText?.text.toString())
                .addOnCompleteListener {task ->
                    if(task.isSuccessful) {
                        (activity as MainActivity).email = email
                        (activity as MainActivity).setOwner(email)
                        this.findNavController().navigate(R.id.action_loginFragment_to_feedFragment)
                    } else {
                        Toast.makeText(context, "Authentication Failed", Toast.LENGTH_SHORT).show()
                        logInButton.isEnabled = true
                    }
                }
        }
    }

}