package com.garry.rmrkot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.garry.rmrkot.databinding.FragmentGarageBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.garage_card_layout.view.*
import java.text.DecimalFormat

class GarageFragment: Fragment() {

    private lateinit var binding: FragmentGarageBinding
    private lateinit var menu: BottomNavigationView
    private lateinit var adapter: GarageRCAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        menu = (activity as MainActivity).menu!!
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_garage, container, false
        )
        (activity as MainActivity).menu?.visibility = View.VISIBLE

        val query: Query? = (activity as MainActivity).carsReference?.whereEqualTo("owner.email", (activity as MainActivity).email)

        val options: FirestoreRecyclerOptions<Car>? = query?.let {
            FirestoreRecyclerOptions.Builder<Car>()
                .setQuery(it, Car::class.java)
                .build()
        }

        adapter = options?.let { GarageRCAdapter(it) }!!
        val recyclerView = binding.garageRecyclerView
        val llm = LinearLayoutManager(activity)
        recyclerView.layoutManager = llm
        recyclerView.adapter = adapter

        return binding.root
    }

    private inner class GarageCarHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {
        val v: View? = view
        val vUsername: TextView? = view.garage_usernameTV
        val vCarImage: ImageButton? = view.garage_carIB
        val vMake: TextView? = view.garage_makeTV
        val vModel: TextView? = view.garage_modelTV
        val vYear: TextView? = view.garage_yearTV
        val vRating: TextView? = view.garage_ratingTV
    }


    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    private inner class GarageRCAdapter internal constructor(options: FirestoreRecyclerOptions<Car>) : FirestoreRecyclerAdapter<Car, GarageCarHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GarageCarHolder {
            val v: View = LayoutInflater.from(parent.context).inflate(R.layout.garage_card_layout,
                parent, false)
            return GarageCarHolder(v)
        }

        override fun onBindViewHolder(holder: GarageCarHolder, position: Int, model: Car) {
            holder.vUsername?.text = model.owner?.username
            model.currentCar = 0
            holder.vCarImage?.context?.let { Glide.with(it)
                .load(model.carImageUrls?.get(model.currentCar))
                .placeholder(R.drawable.plus_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .into(holder.vCarImage)}
            if(model.carImageUrls?.size!! > 1) {
                holder.vCarImage?.context?.let { Glide.with(it)
                    .load(model.carImageUrls!![model.currentCar])
                    .preload()}
            }
            val df = DecimalFormat("#00.##")
            holder.vMake?.text = model.make
            holder.vModel?.text = model.model
            holder.vYear?.text = model.year.toString()
            val ratingString: String =
                df.format((model.posRatings?.div(model.numOfRatings!!))?.times(100)).toString().plus("%")

            holder.vRating?.text = ratingString
            holder.vCarImage?.setOnClickListener {
                if(model.currentCar + 1 > model.carImageUrls!!.size - 1) {
                    model.currentCar = 0
                } else {
                    model.currentCar = model.currentCar + 1
                }
                Glide.with(it.context)
                    .load(model.carImageUrls!![model.currentCar])
                    .placeholder(R.drawable.plus_icon)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .fitCenter()
                    .into(it as ImageView)
                if(model.currentCar + 1 > model.carImageUrls!!.size - 1) {
                    Glide.with(it.context)
                        .load(model.carImageUrls!![0])
                        .preload()
                } else {
                    Glide.with(it.context)
                        .load(model.carImageUrls!![model.currentCar + 1])
                        .preload()
                }
            }
        }

    }


}