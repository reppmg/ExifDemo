package io.bakerystud.exifdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
        val sub = DataStorage.frenPhotos.subscribe { frenPhotos ->
            val myPhotos = DataStorage.myPhotos.value

            Single.create<List<Overlap>> {
                it.onSuccess(OverlapFinder(myPhotos, frenPhotos).findOverlaps())
            }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { overlaps ->
                    progress.isVisible = false
                    if (overlaps.isEmpty()) {
                        textNoOverlaps.isVisible = true
                    } else {
                        textNoOverlaps.isVisible = false
                        recyclerOverlaps.layoutManager = LinearLayoutManager(requireContext())
                        recyclerOverlaps.adapter = OverlapAdapter().apply {
                            submitList(overlaps)
                        }
                        recyclerOverlaps.apply {
                            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                            hasFixedSize()
                        }
                    }
                }


        }
    }


}