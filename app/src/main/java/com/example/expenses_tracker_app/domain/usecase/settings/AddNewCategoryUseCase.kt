package com.example.expenses_tracker_app.domain.usecase.settings

import com.example.expenses_tracker_app.domain.repository.IAppRepository
import javax.inject.Inject

class AddNewCategoryUseCase @Inject constructor(
    private val  appRepository: IAppRepository
) {

}