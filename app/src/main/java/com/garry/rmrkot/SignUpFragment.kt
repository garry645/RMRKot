package com.garry.rmrkot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.garry.rmrkot.databinding.FragmentSignupBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var emailET: TextInputLayout
    private lateinit var usernameET: TextInputLayout
    private lateinit var passwordET: TextInputLayout
    private var rideAdded = false
    private var docRef: DocumentReference? = null
    var newCarReference: DocumentReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signup, container, false
        )
        binding.swapToLoginButton.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_signUpFragment_to_loginFragment)
        )

        (activity as MainActivity).menu?.visibility = View.GONE
        emailET = binding.signUpEmailET
        usernameET = binding.signUpUsernameET
        passwordET = binding.signUpPasswordET

        binding.signUpButton.setOnClickListener {
            signUp()
        }


        binding.addRideBT.setOnClickListener {
            if (!rideAdded) {
                binding.signUpMakeET.visibility = View.VISIBLE
                binding.signUpModelET.visibility = View.VISIBLE
                binding.signUpYearET.visibility = View.VISIBLE
                rideAdded = true
            } else {
                binding.signUpMakeET.visibility = View.GONE
                binding.signUpModelET.visibility = View.GONE
                binding.signUpYearET.visibility = View.GONE
                rideAdded = false
            }
        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        val currentUser: FirebaseUser? = (activity as MainActivity).mAuth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(userIn: FirebaseUser?) {
        //update ui according to user
    }

    private fun signUp() {
        if ((activity as MainActivity).mAuth.currentUser != null) {
            (activity as MainActivity).mAuth.signOut()
        }

        val emailIn = emailET.editText?.text.toString().trim()
        val usernameIn = usernameET.editText?.text.toString().trim()
        val passwordIn = passwordET.editText?.text.toString().trim()

        if (validateForm(emailIn, usernameIn, passwordIn)) {
            (activity as MainActivity).email = emailIn
            (activity as MainActivity).createOwner(emailIn, usernameIn)
            createAccount(emailIn, passwordIn)
        }
    }

    private fun validateForm(emailIn: String?, usernameIn: String?, passwordIn: String?): Boolean {
        return when {
            emailIn == null -> {
                Toast.makeText(context, "Email must not be empty", Toast.LENGTH_SHORT).show()
                false
            }
            usernameIn == null -> {
                Toast.makeText(context, "Username must not be null", Toast.LENGTH_SHORT).show()
                false
            }
            passwordIn == null -> {
                Toast.makeText(context, "Password must not be null", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun createAccount(emailIn: String, passwordIn: String) {
        this.activity?.let {
            (activity as MainActivity).mAuth.createUserWithEmailAndPassword(emailIn, passwordIn)
                .addOnCompleteListener { task1 ->
                    if (task1.isSuccessful) {
                        Toast.makeText(this.context, "Registration successful", Toast.LENGTH_LONG)
                            .show()
                        docRef = (activity as MainActivity).ratersReference?.document(emailIn)
                        (activity as MainActivity).owner?.let { ownerTask -> docRef?.set(ownerTask) }

                        addCarsToDB()


                    }
                }
        }

    }

    private fun addCarsToDB() {
        (activity as MainActivity).carsReference?.get()?.addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    val car: Car = document.toObject(Car::class.java)
                    if (car.carImageUrls?.size == null) {

                        car.dbID?.let { it1 ->
                            (activity as MainActivity).carsReference?.document(
                                it1
                            )?.delete()
                        }
                    }
                    if (car.owner?.email != (activity as MainActivity).owner?.email) {
                        val newCarDocRef: DocumentReference? =
                            car.dbID?.let { it1 ->
                                docRef?.collection("nonRatedCars")?.document(
                                    it1
                                )
                            }
                        val newCar = car.dbID?.let { it1 -> NonRatedCar(it1) }
                        if (newCarDocRef != null) {
                            if (newCar != null) {
                                newCarDocRef.set(newCar)
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(
                    this.context,
                    "Error adding cars",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        if (!rideAdded) {
            docRef?.update("Timestamp", FieldValue.serverTimestamp())
                ?.addOnSuccessListener {
                    this.findNavController().navigate(R.id.action_signUpFragment_to_feedFragment)
                }
        } else {
            docRef?.update("Timestamp", FieldValue.serverTimestamp())
            newCarReference = (activity as MainActivity).carsReference?.document()
            val carDocRefID = newCarReference?.id
            val make = binding.signUpMakeET.editText?.text.toString().trim()
            val model = binding.signUpModelET.editText?.text.toString().trim()
            val year = binding.signUpYearET.editText?.text.toString().trim().toInt()
            val carIn = Car(
                owner = (activity as MainActivity).owner,
                dbID = carDocRefID,
                make = make,
                model = model,
                year = year
            )
            (activity as MainActivity).car = carIn

            newCarReference?.set(carIn)
            newCarReference?.update("Timestamp", FieldValue.serverTimestamp())
                ?.addOnCompleteListener {
                    this.findNavController()
                        .navigate(R.id.action_signUpFragment_to_addPicsFragment)
                }

        }
    }
}

