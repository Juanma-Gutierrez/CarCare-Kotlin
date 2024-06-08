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

/**
 * Fragment for displaying information about the user.
 */
class AboutMeFragment : Fragment() {
    private lateinit var binding: FragmentAboutMeBinding

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, using findViewById(int)
     * to programmatically interact with widgets in the UI, and binding data to list views.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being
     *                            shut down then this Bundle contains the data it most recently supplied
     *                            in onSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /**
     * Called to create the fragment's view hierarchy.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     *                 any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     *                  UI should be attached to. The fragment should not add the view itself,
     *                  but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                            from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutMeBinding.inflate(layoutInflater)
        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored
     * into the view. It is safe to perform UI operations in this method.
     *
     * @param view The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                            from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureUI()
    }

    /**
     * Configures the UI elements of the fragment.
     */
    private fun configureUI() {
        hideToolBars()
        configureSocialMediaButtons()
        configureBackButton()
    }

    /**
     * Hides the toolbar and bottom navigation view.
     */
    private fun hideToolBars() {
        requireActivity().findViewById<BottomNavigationView>(R.id.bottomBar).visibility = View.GONE
        requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).visibility = View.GONE
    }

    /**
     * Configures the click listeners for the social media buttons.
     */
    private fun configureSocialMediaButtons() {
        binding.amIvAboutMeGitHubIcon.setOnClickListener {
            openURL("https://github.com/Juanma-Gutierrez")
        }
        binding.amIvAboutMeLinkedInIcon.setOnClickListener {
            openURL("https://www.linkedin.com/in/juanmanuelgutierrezm")
        }
    }

    /**
     * Configures the click listener for the back button.
     */
    private fun configureBackButton() {
        binding.amBtAboutMeBackButton.setOnClickListener {
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomBar).visibility = View.VISIBLE
            requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar).visibility = View.VISIBLE
            requireActivity().onBackPressed()
        }
    }

    /**
     * Opens the provided URL in a browser.
     *
     * @param url The URL to open.
     */
    private fun openURL(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}