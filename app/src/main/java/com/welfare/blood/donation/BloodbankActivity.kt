package com.welfare.blood.donation

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.welfare.blood.donation.adapters.BloodBankAdapter
import com.welfare.blood.donation.databinding.ActivityBloodbankBinding
import com.welfare.blood.donation.models.BloodBankItem

class BloodbankActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBloodbankBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBloodbankBinding.inflate(layoutInflater)
        setContentView(binding.root)
//http://www.kmu.edu.pk/
        val bloodBankItems = listOf(
            BloodBankItem("Khyber Medical University(KMU)", "Khyber University,Phase5,Hayatabad", "+92-91-9217703",""),
            BloodBankItem("Lady Reading Hospital (LRH)", "Soekarno Square,Peshawar", "+92-91-9211444","https://www.lrh.edu.pk/"),
            BloodBankItem("Hayatabad Medical Complex(HMC)", "Phase 4, Hayatabad, Peshawar", "+92-91-9217140-46","http://www.hmckp.gov.pk/"),
            BloodBankItem("Fatimid Foundation Peshawar", "Hayatabad Phase-4,St.10,Peshawar", "+92-91-5821375","http://www.fatimid.org"),
            BloodBankItem("Frontier Foundation Blood", "H.No.21,Jamrud Road,Peshawar,", "+92-91-5855747","http://www.frontierfoundation.org/"),
            BloodBankItem("Rehman Medical Institute(RMI)", "5-B/2,Phase-V,Hayatabad,Peshawar", "+92-91-5838000","https://rmi.edu.pk/"),
            BloodBankItem("Al-Khidmat Foundation", "Office.62,Zargarabad,Peshawar", "+92-91-2260545","https://al-khidmatfoundation.org/"),
            BloodBankItem("Thalassemia Welfare Society", "Qazi Complex,Dabgari Gardens,Peshawar", "+92-91-2215453",""),
            BloodBankItem("Pakistan Red Crescent Society", "National Headquarters, H-8, Isb", "+92-51-9250404-06","https://www.prcs.org.pk"),
            BloodBankItem("Fatimid Foundation", "393, Britto Road, Garden East, Karachi", "+92-21-32225202","http://www.fatimid.org"),
            BloodBankItem("Fatimid Foundation Lahore", "91, Shadman-II, Lahore", "+92-42-37588435-37","http://www.fatimid.org"),
            BloodBankItem("Fatimid Foundation Islamabad", "Sector G-9/1, Mauve Area, Isb", "+92-51-2256976","http://www.fatimid.org"),
            BloodBankItem("Indus Hospital Blood Center", "Korangi Crossing, Karachi", "+92-21-35112709","https://indushospital.org.pk/"),
            BloodBankItem("Hussaini Blood Bank", "ST.2 Block B,Dastagir,Karachi", "+92-21-36363218","http://www.hussainibloodbank.org"),
            BloodBankItem("Akhuwat Blood Bank", "19 Civic Centre, Township, Lhr", "+92-42-35111550","https://akhuwat.org.pk/"),
            BloodBankItem("Liaquat National Hospital", "Stadium Road, Karachi", "+92-21-111456456","https://www.lnh.edu.pk/"),
            BloodBankItem("Arif Memorial Hospital", "Mansoora, Lahore", "+92-42-35401620","http://www.amc.edu.pk/"),
            BloodBankItem("Al-Mustafa Welfare Society", "Gulshan-e-Iqbal, Karachi", "+92-21-34966078","https://almustafatrust.org/"),
            BloodBankItem("Sundas Foundation", "654, Poonch Road, Samanabad, Lhr", "+92-42-37588683-4","http://www.sundas.org/"),
            BloodBankItem("Safe Blood Transfusion Programme", "Office No. 3, Block C-1, Gulberg-III, Lhr", "+92-42-35884950","http://www.sbtp.gov.pk/"),

        )


        setupRecyclerView(bloodBankItems)
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        }

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView(items: List<BloodBankItem>) {
        val adapter = BloodBankAdapter(items)
        binding.bloodbankRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.bloodbankRecyclerview.adapter = adapter
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }
}
