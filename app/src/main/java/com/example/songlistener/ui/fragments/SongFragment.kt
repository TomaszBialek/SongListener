package com.example.songlistener.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.example.songlistener.R
import com.example.songlistener.data.entity.Song
import com.example.songlistener.exoplayer.toSong
import com.example.songlistener.other.Status
import com.example.songlistener.ui.viewmodels.MainViewModel
import com.example.songlistener.ui.viewmodels.SongViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_song.*
import javax.inject.Inject

@AndroidEntryPoint
class SongFragment : Fragment(R.layout.fragment_song) {

    @Inject
    lateinit var glide: RequestManager

    private val mainViewModel: MainViewModel by activityViewModels()
    private val songViewModel: SongViewModel by viewModels()

    private var currentPlayingSong: Song? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        subscribeToObservers()
    }

    private fun updateTitleAndSongImage(song: Song) {
        val title = "${song.title} - ${song.subtitle}"
        tvSongName.text = title
        glide.load(song.imageUrl).into(ivSongImage)
    }

    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(viewLifecycleOwner) {
            it?.let {  result ->
                when(result.status) {
                    Status.SUCCESS -> {
                        result.data?.let { songs ->
                            if (currentPlayingSong == null && songs.isNotEmpty()) {
                                currentPlayingSong = songs[0]
                                updateTitleAndSongImage(songs[0])
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }

        mainViewModel.currentPlayingSong.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            currentPlayingSong = it.toSong()
            updateTitleAndSongImage(currentPlayingSong!!)
        }
    }

}