package com.juanmaGutierrez.carcare.ui.detailActivity.fragment.aboutMe

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.databinding.FragmentAboutMeBinding

class AboutMeFragment : Fragment() {
    private lateinit var binding: FragmentAboutMeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutMeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureUI()
    }

    private fun configureUI() {
        hideToolBars()
        configureSocialMediaButtons()
        configureBackButton()
    }

    private fun hideToolBars() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomBar).visibility = View.GONE
        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).visibility = View.GONE
    }

    private fun configureSocialMediaButtons() {
        binding.amBtAboutMeGitHubButton.setOnClickListener {
            openURL("https://github.com/Juanma-Gutierrez")
        }
        binding.amBtAboutMeLinkedInButton.setOnClickListener {
            openURL("https://www.linkedin.com/in/juanmanuelgutierrezm")
        }
    }

    private fun configureBackButton() {
        binding.amBtAboutMeBackButton.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomBar).visibility = View.VISIBLE
            requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).visibility = View.VISIBLE
            requireActivity().onBackPressed()
        }
    }

    private fun openURL(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}