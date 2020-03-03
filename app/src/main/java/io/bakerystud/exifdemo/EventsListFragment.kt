package io.bakerystud.exifdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*

class EventsListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        populateList()
    }

    private fun populateList() {
        val sub = DataStorage.frenPhotos.subscribe {
            val myPhotos = DataStorage.myPhotos.value
            val overlaps = OverlapFinder(myPhotos, it).findOverlaps()
            if (overlaps.isEmpty()) {
                textNoOverlaps.isVisible = true
            } else {
                textNoOverlaps.isVisible = false
                recyclerOverlaps.layoutManager = LinearLayoutManager(requireContext())
                recyclerOverlaps.adapter = OverlapAdapter().apply {
                    submitList(overlaps)
                }
            }
        }
    }


}