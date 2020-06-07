package com.garry.rmrkot

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.garry.rmrkot.databinding.FragmentAddPicsBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.delete_image_dialog.*
import kotlinx.android.synthetic.main.fragment_add_pics.*
import java.util.jar.Manifest

class AddPicsFragment : Fragment() {
    
    private val ImageRequest1 = 1
    private val ImageRequest2 = 2
    private val ImageRequest3 = 3
    private val ImageRequest4 = 4
    private val ImageRequest5 = 5
    private val ImageRequest6 = 6 
    private val ImageRequest7 = 7
    private val ImageRequest8 = 8
    private val ImageRequest9 = 9
    private val ImageRequest10 = 10
    
    
    private var mDialog: Dialog? = null
    private var yesBT: Button? = null
    private var linearLayout1: LinearLayout? = null
    private var mStorageRef: StorageReference? = null
    private var carDdId: String? = null
    private var carIn: Car? = (activity as MainActivity).car
    
    private lateinit var binding: FragmentAddPicsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_pics, container, false
        )
        //Hide bottom menu then get Car Database ID
        (activity as MainActivity).menu?.visibility = View.GONE
        carDdId = arguments?.getString("CarDBID")

        //Reference to Car bucket in Firebase Storage
        mStorageRef = FirebaseStorage.getInstance().getReference((activity as MainActivity).owner?.email + "/" + carDdId)

        //Set up the Delete Pictures Dialog
        mDialog = context?.let { Dialog(it) }
        mDialog?.setContentView(R.layout.delete_image_dialog)
        val noButt : Button? = mDialog?.findViewById<Button>(R.id.noBT)
        noButt?.setOnClickListener {mDialog?.dismiss()}
        yesBT = mDialog?.findViewById(R.id.yesBT)


        loadImageButtons()

        return binding.root
    }

    private fun loadImageButtons() {
        if(Build.VERSION.SDK_INT >= 23) {
            requestPermissions(listOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE).toTypedArray(), 2)
        }

        binding.carImage1.setOnClickListener {
            val intent1 = activity?.let { it1 ->
                CropImage.activity()
                    .setAspectRatio(4,3)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .getIntent(it1)
            }
            startActivityForResult(intent1, ImageRequest1)
        }
        binding.carImage1.setOnLongClickListener {
            mDialog?.show()
            yesBT?.setOnClickListener {
                deleteImage(carImage1)
            }
            return@setOnLongClickListener false
        }
        
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        
    }


    private fun deleteImage(iBToDelete: ImageButton) {
        val layoutIndex = linearLayout1?.indexOfChild(iBToDelete)
        if (layoutIndex != null) {
            carIn?.carImageUrls?.get(layoutIndex)?.let {
                FirebaseStorage.getInstance().getReferenceFromUrl(
                    it
                )
            }
        }
        if (layoutIndex != null) {
            carIn?.deleteImageUrl(layoutIndex)
        }
        //remove Image Button and reset its image
        iBToDelete.visibility = View.GONE
        iBToDelete.setImageResource(R.drawable.plus_icon)

        //add to back of line in layout
        linearLayout1?.removeView(iBToDelete)
        linearLayout1?.addView(iBToDelete, 9)

        if(carIn?.carImageUrls?.size?.plus(1)!! > 10) {
            (carIn!!.carImageUrls?.size)?.plus(1)?.let { linearLayout1?.getChildAt(it)?.visibility = View.VISIBLE}
        }

    }
}