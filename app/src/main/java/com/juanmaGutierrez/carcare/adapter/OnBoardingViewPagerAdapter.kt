package com.juanmaGutierrez.carcare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.juanmaGutierrez.carcare.R
import com.juanmaGutierrez.carcare.model.localData.OnBoardingData

/**
 * OnBoardingViewPagerAdapter: Adapter for the onboarding view pager
 * @param context: Context of the application
 * @param onBoardingDataList: List of onboarding data
 */
class OnBoardingViewPagerAdapter(
    private var context: Context,
    private var onBoardingDataList: List<OnBoardingData>
) : PagerAdapter() {

    /**
     * Determines whether a page View is associated with a specific key object as returned by instantiateItem(ViewGroup, int).
     * @param view: View to check
     * @param `object`: Object to compare with the view
     * @return Boolean indicating whether the view is associated with the object
     */
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    /**
     * Returns the number of views available
     * @return Int indicating the size of the onboarding data list
     */
    override fun getCount(): Int {
        return onBoardingDataList.size
    }

    /**
     * Remove an item from the container
     * @param container: ViewGroup where the page view will be removed
     * @param position: Position of the item in the adapter
     * @param object: Object to remove
     */
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    /**
     * Creates and initializes a page view for the given position
     * @param container: ViewGroup where the page view will be added
     * @param position: Position of the item in the adapter
     * @return Any: The newly created view
     */
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.onboarding_screen_layout, null)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val title: TextView = view.findViewById(R.id.tv_title)
        val desc: TextView = view.findViewById(R.id.tv_desc)

        imageView.setImageResource(onBoardingDataList[position].imageURL)
        title.text = onBoardingDataList[position].title
        desc.text = onBoardingDataList[position].desc
        container.addView(view)
        return view
    }
}