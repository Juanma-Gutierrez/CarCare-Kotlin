package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.spent

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentSpentDetailBinding
import com.juanmaGutierrez.carcare.model.firebase.SpentFB
import com.juanmaGutierrez.carcare.ui.detailActivity.fragment.provider.ProviderDetailViewModel

class SpentDetailFragment : Fragment() {
    private lateinit var binding: FragmentSpentDetailBinding
    private lateinit var viewModel: SpentDetailViewModel
    private lateinit var spent: SpentFB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpentDetailBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SpentDetailViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createEmptySpent()
        // configureUIMessages()
        // checkNewOrCreate()
        // configureUI()
        // configureSelectables()
        // configureObservers()
    }

    private fun createEmptySpent() {
        TODO("Not yet implemented")
    }
}