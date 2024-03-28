package com.juanmaGutierrez.carcare.localData

class UserLocalData {
    var userID: String = ""

    override fun toString(): String {
        return "-UserLocalData-\nUserID: $userID\n"
    }

    companion object {
        private var instance: UserLocalData? = null

        fun getInstance(): UserLocalData {
            if (instance == null) {
                instance = UserLocalData()
            }
            return instance!!
        }
    }
}