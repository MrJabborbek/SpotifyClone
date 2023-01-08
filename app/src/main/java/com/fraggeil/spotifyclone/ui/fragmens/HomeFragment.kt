package com.fraggeil.spotifyclone.ui.fragmens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.fraggeil.spotifyclone.R
import com.fraggeil.spotifyclone.adapters.SongsAdapter
import com.fraggeil.spotifyclone.databinding.FragmentHomeBinding
import com.fraggeil.spotifyclone.other.Resource
import com.fraggeil.spotifyclone.other.Status
import com.fraggeil.spotifyclone.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.list_item) {

    private val mainViewModel: MainViewModel by viewModels()
    lateinit var binding: FragmentHomeBinding
//
//    @Inject
//    lateinit var songsAdapter: SongsAdapter

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = FragmentHomeBinding.inflate(inflater)
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), "gjhgh", Toast.LENGTH_SHORT).show()

//        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

//        setupRecView()
//        subscribeToObservers()

//        songsAdapter.setOnItemClickListener { song->
//            viewModel.playOrToggleSong(song)
//        }
    }

//    private fun setupRecView(){
//        binding.rvAllSongs.apply {
//            adapter = songsAdapter
//            layoutManager = LinearLayoutManager(requireContext())
//        }
//    }

//    private fun subscribeToObservers(){
//        viewModel.mediaItems.observe(viewLifecycleOwner, Observer { result ->
//            when(result.status){
//                Status.SUCCESS -> {
//                    binding.allSongsProgressBar.visibility = View.GONE
//                    result.data?.let { songs ->
//                        songsAdapter.songs = songs
//                    }
//                }
//                Status.ERROR -> Unit
//                Status.LOADING -> binding.allSongsProgressBar.visibility = View.VISIBLE
//            }
//        })
//    }

}