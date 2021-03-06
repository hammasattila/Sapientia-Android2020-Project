package hamm.android.project.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import hamm.android.project.R
import hamm.android.project.databinding.FragmentRestaurantDetailBinding
import hamm.android.project.utils.*
import hamm.android.project.viewmodels.RestaurantDetailFragmentViewModel
import java.io.File


class RestaurantDetailFragment : Fragment() {

    private val args: RestaurantDetailFragmentArgs by navArgs()
    private val binding by viewBinding(FragmentRestaurantDetailBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.viewModel = ViewModelProvider(this).get(RestaurantDetailFragmentViewModel::class.java)
        binding.viewModel?.restaurant = args.restaurant

        view?.let {
            binding.viewModel?.restaurant?.let { restaurant ->

                postponeEnterTransition()
                it.findViewById<ImageView>(R.id.item_restaurant_image).load(restaurant.getImageUrl()) { startPostponedEnterTransition() }
                restaurant.setBasicTextContent(it)
                restaurant.setTransitionNames(it)
                restaurant.setFavoriteButton(it)
                restaurant.setDetailActions(it)

                // Click listeners
                it.findViewById<Button>(R.id.button_open_maps).setOnClickListener { openMaps() }
                it.findViewById<Button>(R.id.button_open_phone).setOnClickListener { dialNumber() }
                it.findViewById<Button>(R.id.button_set_favorite).setOnClickListener { togaeFavorites() }
                it.findViewById<Button>(R.id.button_set_photo).setOnClickListener { setPhoto() }
                it.findViewById<Button>(R.id.button_unset_favorite).setOnClickListener { togaeFavorites() }
                val restaurantImage = it.findViewById<ImageView>(R.id.item_restaurant_image)
                restaurant.ext?.let { ext ->
                    if (0 < ext.images.size) {
                        restaurantImage.isClickable = true
                        restaurantImage.setOnClickListener {
                            findNavController().navigate(RestaurantDetailFragmentDirections.restaurantImagesFragment(restaurant.info.name, ext))
                        }
                    }
                }


                binding.viewModel!!.getRestaurantById(restaurant.info.id).observe(viewLifecycleOwner, {
                    binding.viewModel!!.restaurant = it
                    setContent()
                })
            }
        }
    }

    private fun setContent() {
        view?.let {
            binding.viewModel?.restaurant?.let { restaurant ->
                it.findViewById<ImageView>(R.id.item_restaurant_image).load(restaurant.getImageUrl())
                restaurant.setBasicTextContent(it)
                restaurant.setFavoriteButton(it)

                val restaurantImage = it.findViewById<ImageView>(R.id.item_restaurant_image)
                restaurant.ext?.let { ext ->
                    if (0 < ext.images.size) {
                        restaurantImage.isClickable = true
                        restaurantImage.setOnClickListener {
                            findNavController().navigate(RestaurantDetailFragmentDirections.restaurantImagesFragment(restaurant.info.name, ext))
                        }
                    }
                }
            }
        }
    }

    private fun setPhoto() {
        startActivityForResult(Intent.createChooser(Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT), "Select the image"), IMAGE_REQUEST_CODE)
    }

    private fun dialNumber() {
        binding.viewModel?.restaurant?.let { restaurant ->
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("tel:${restaurant.info.phone}")))
        }
    }

    private fun openMaps() {
        binding.viewModel?.restaurant?.let { restaurant ->
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?q=loc:${restaurant.info.lat},${restaurant.info.lng}")
                )
            )
        }
    }

    private fun togaeFavorites() {
        binding.viewModel?.restaurant?.toggleFavorite()
        binding.viewModel?.updateRestaurant()
        binding.viewModel?.restaurant?.setFavoriteButton(view)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
//            data?.dataString?.let { imageUrl ->
//                view?.findViewById<ImageView>(R.id.item_restaurant_image)?.load(imageUrl)
//                binding.viewModel?.restaurant?.addImage(imageUrl)
//                binding.viewModel?.updateRestaurant()
//            }
            data?.data?.let { it ->
                context?.let { context ->
                    val path = FileUtil.from(context, it).path
                    view?.findViewById<ImageView>(R.id.item_restaurant_image)?.load(path)
                    binding.viewModel?.restaurant?.addImage(path)
                    binding.viewModel?.updateRestaurant()
                }
            }
        }
    }
}