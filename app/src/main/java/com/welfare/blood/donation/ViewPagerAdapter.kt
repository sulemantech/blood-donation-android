package com.welfare.blood.donation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.welfare.blood.donation.fragments.DonationHistoryFragment
import com.welfare.blood.donation.fragments.ReceivedRequestsFragment
import com.welfare.blood.donation.fragments.RequestHistoryFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3 // Update this count to include the new tab
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RequestHistoryFragment()
            1 -> DonationHistoryFragment()
            2 -> ReceivedRequestsFragment() // Add this line
            else -> Fragment()
        }
    }
}
