package com.welfare.blood.donation.models

class BloodDonorModel(
    private var pic: Int,
    private var name: String,
    private var age: Int,
    private var bloodType: String,
    private var contactNumber: String,
    private var email: String,
    private var address: String
) {
    fun getPic(): Int {
        return pic
    }

    fun setPic(pic: Int) {
        this.pic = pic
    }
    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getAge(): Int {
        return age
    }

    fun setAge(age: Int) {
        this.age = age
    }

    fun getBloodType(): String {
        return bloodType
    }

    fun setBloodType(bloodType: String) {
        this.bloodType = bloodType
    }

    fun getContactNumber(): String {
        return contactNumber
    }

    fun setContactNumber(contactNumber: String) {
        this.contactNumber = contactNumber
    }

    fun getEmail(): String {
        return email
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getAddress(): String {
        return address
    }

    fun setAddress(address: String) {
        this.address = address
    }
}
