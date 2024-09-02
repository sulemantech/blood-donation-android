package com.welfare.blood.donation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.welfare.blood.donation.fragments.DonationHistoryFragment
import com.welfare.blood.donation.fragments.ReceivedRequestsFragment
import com.welfare.blood.donation.fragments.RequestHistoryFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RequestHistoryFragment()
            1 -> ReceivedRequestsFragment()
          //  2 -> ReceivedRequestsFragment()
            else -> Fragment()
        }
    }
}
