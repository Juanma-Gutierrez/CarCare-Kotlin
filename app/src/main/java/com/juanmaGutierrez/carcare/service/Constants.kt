package com.juanmaGutierrez.carcare.service


class Constants {
    companion object {
        const val TAG = "Info-CarCare"
        const val TAG_ERROR = "Error-CarCare"

        // Database
        const val COL_LOG = "log"
        const val COL_LOG_DOC = "generalLog"
        const val COL_LOG_ARRAYLIST = "logs"

        // Errors
        const val ERROR_DATABASE = "Error in database operation"
        const val ERROR_CREATE_USER_WITH_EMAIL= "createUserWithEmail:failure"
    }
}