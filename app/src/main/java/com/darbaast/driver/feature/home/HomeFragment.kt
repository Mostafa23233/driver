package com.darbaast.driver.feature.home

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.darbaast.driver.R
import com.darbaast.driver.data.model.CarouselData
import com.darbaast.driver.feature.darbastrequest.RequestsActivity
import com.darbaast.driver.socket.SocketService
import com.google.android.material.card.MaterialCardView
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val mainViewModel:HomeViewModel by viewModels()
    private lateinit var cardViewRequest:MaterialCardView
    private lateinit var carousel: CarouselView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getCarouselData()
        clicks()

    }

    private fun clicks() {
        cardViewRequest.setOnClickListener { 
            startActivity(Intent(requireContext(),RequestsActivity::class.java))
        }
    }

    private fun getCarouselData() {
        mainViewModel.getCarouselData()
        mainViewModel.carouselLiveData.observe(viewLifecycleOwner){
            carouselView(it)
        }

    }
    private fun carouselView(list:List<CarouselData>) {
        val imageListener = ImageListener { position2, imageView ->
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.load(list[position2].img_url) {
                crossfade(true)
                crossfade(300)
            }

        }

        carousel.setImageListener(imageListener)
        carousel.pageCount = list.size
        carousel.setImageClickListener {
            if (list[it].clickable){
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(list[it].click_url)
                startActivity(intent)
            }
        }

    }
    private  fun init(view: View){
        carousel = view.findViewById(R.id.carousel_home)
        cardViewRequest = view.findViewById(R.id.cardRequest)
    }


}