package com.juanmaGutierrez.carcare.localData

class UserLocalData {
    var user: Any? = null

    override fun toString(): String {
        return "-UserLocalData-\nUSER: $user\n"
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