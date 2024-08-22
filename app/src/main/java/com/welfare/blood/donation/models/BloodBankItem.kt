package com.welfare.blood.donation.models


import android.os.Parcel
import android.os.Parcelable

data class BloodBankItem(
    val name: String,
    val phone: String,
    val address: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BloodBankItem> {
        override fun createFromParcel(parcel: Parcel): BloodBankItem {
            return BloodBankItem(parcel)
        }

        override fun newArray(size: Int): Array<BloodBankItem?> {
            return arrayOfNulls(size)
        }
    }
}
