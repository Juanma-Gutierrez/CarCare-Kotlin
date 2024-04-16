package com.juanmaGutierrez.carcare.ui.itemListActivity.fragment.spentsList

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentSpentsListBinding
import com.juanmaGutierrez.carcare.service.ToolbarService
import com.juanmaGutierrez.carcare.ui.detailActivity.DetailActivity

class SpentsListFragment : Fragment() {
    private lateinit var viewModel: SpentsListViewModel
    private lateinit var binding: FragmentSpentsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[SpentsListViewModel::class.java]
        binding = FragmentSpentsListBinding.inflate(layoutInflater)
        binding.slFabAddSpent.setOnClickListener {
            val ts = ToolbarService.getInstance()
            ts.detailTitle = getString(R.string.new_spent)
            val intent = Intent(requireContext(), DetailActivity::class.java)
            intent.putExtra("fragmentType", "newSpent")
            startActivity(intent)
        }
        return binding.root
    }
}