package com.garry.rmrkot

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.garry.rmrkot.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    var email: String? = null
    var owner: Owner? = null
    var car: Car? = null
    var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    var ratersReference: CollectionReference? = null
    var carsReference: CollectionReference? = null

    var menu: BottomNavigationView? = null
    var user: DocumentReference? = null
    var userSnapshot: DocumentSnapshot? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val bindingUtil = setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        //set up Bottom Navigation
        menu = bindingUtil.bottomNavigation
        val navController = Navigation.findNavController(this, R.id.myNavHostFragment)
        NavigationUI.setupWithNavController(menu!!, navController)
        //set up db values
        ratersReference = db.collection("raters")
        carsReference = db.collection("cars")

    }

    fun createOwner(emailIn: String, usernameIn: String) {
        owner = Owner(emailIn, usernameIn)
    }

    fun setOwner(emailIn: String) {
        user = ratersReference?.document(emailIn)
        user?.get()?.addOnCompleteListener {
            userSnapshot = it.result
            if(userSnapshot == null) {
                bottom_navigation.visibility = View.GONE
                supportFragmentManager.beginTransaction().replace(R.id.myNavHostFragment, LoginFragment())
            }
            owner = userSnapshot?.toObject(Owner::class.java)
        }
    }

}
