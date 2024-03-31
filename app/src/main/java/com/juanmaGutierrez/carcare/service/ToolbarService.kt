package com.juanmaGutierrez.carcare.service

class ToolbarService {
    var title: String = ""
    var detailTitle: String = ""

    companion object {
        private var instance: ToolbarService? = null

        fun getInstance(): ToolbarService {
            if (instance == null) {
                instance = ToolbarService()
            }
            return instance!!
        }
    }
}
