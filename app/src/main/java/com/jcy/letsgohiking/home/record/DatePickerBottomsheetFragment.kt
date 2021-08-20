package com.jcy.letsgohiking.home.record

import android.os.Bundle
import android.widget.DatePicker
import com.hdh.base.fragment.BaseDataBindingBottomSheetFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentDatePickerBottomsheetBinding
import com.jcy.letsgohiking.util.Log
import java.text.SimpleDateFormat
import java.util.*


class DatePickerBottomsheetFragment : BaseDataBindingBottomSheetFragment<FragmentDatePickerBottomsheetBinding>(
    R.layout.fragment_date_picker_bottomsheet){
    private lateinit var calendar : Calendar
    private var date : String=""
    private var clickOk: ((String) -> Unit)? = null

    companion object {
        fun getInstance() = DatePickerBottomsheetFragment()
    }
    override fun FragmentDatePickerBottomsheetBinding.onBind() {
        vi = this@DatePickerBottomsheetFragment

        initDatePicker()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    private fun initDatePicker(){
        calendar = Calendar.getInstance()
        binding.datePicker.init(
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ){ datePicker: DatePicker, year: Int, month: Int, day: Int ->
            date = "등산일 : ${year}년 ${month + 1}월 ${day}일"
        }
    }
    fun onClickCancel(){
        dismiss()
    }

    fun setOnClickOk(clickOk: ((String) -> Unit)): DatePickerBottomsheetFragment {
        this.clickOk = clickOk
        return this
    }
    fun onClickOk(){
        val dateFormat = SimpleDateFormat("등산일 : yyyy년 MM월 dd일")
        val dateDefault = dateFormat.format(calendar.time)
        if (date == "") {
            clickOk?.invoke(dateDefault)
        } else {
            clickOk?.invoke(date)
        }
    }

}