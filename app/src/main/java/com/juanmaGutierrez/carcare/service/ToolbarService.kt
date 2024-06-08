package com.juanmaGutierrez.carcare.service

/**
 * Service class for managing toolbar titles.
 */
class ToolbarService {
    var title: String = ""
    var detailTitle: String = ""

    /**
     * A singleton class providing toolbar-related services.
     */
    companion object {
        private var instance: ToolbarService? = null

        /**
         * Retrieves the singleton instance of ToolbarService.
         *
         * @return The singleton instance of ToolbarService.
         */
        fun getInstance(): ToolbarService {
            if (instance == null) {
                instance = ToolbarService()
            }
            return instance!!
        }
    }
}
