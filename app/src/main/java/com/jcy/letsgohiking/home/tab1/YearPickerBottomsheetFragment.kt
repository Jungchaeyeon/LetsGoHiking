package com.jcy.letsgohiking.home.tab1

import android.os.Bundle
import android.widget.DatePicker
import com.hdh.base.fragment.BaseDataBindingBottomSheetFragment
import com.jcy.letsgohiking.R
import com.jcy.letsgohiking.databinding.FragmentDatePickerBottomsheetBinding
import com.jcy.letsgohiking.databinding.FragmentYearPickerBottomsheetBinding
import com.jcy.letsgohiking.util.Log
import java.text.SimpleDateFormat
import java.util.*


class YearPickerBottomsheetFragment : BaseDataBindingBottomSheetFragment<FragmentYearPickerBottomsheetBinding>(
    R.layout.fragment_year_picker_bottomsheet){
    private lateinit var calendar : Calendar
    private var date : String=""
    private var clickOk: ((String) -> Unit)? = null

    companion object {
        fun getInstance() = YearPickerBottomsheetFragment()
    }
    override fun FragmentYearPickerBottomsheetBinding.onBind() {
        vi = this@YearPickerBottomsheetFragment

        initDatePicker()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    private fun initDatePicker(){
        calendar = Calendar.getInstance()
        binding.yearPicker.minValue= 2000
        binding.yearPicker.maxValue= 2090
        binding.yearPicker.value =calendar.get(Calendar.YEAR)
        binding.yearPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            date = newVal.toString()+"년"
        }
    }
    fun onClickCancel(){
        dismiss()
    }

    fun setOnClickOk(clickOk: ((String) -> Unit)): YearPickerBottomsheetFragment {
        this.clickOk = clickOk
        return this
    }
    fun onClickOk(){
        val dateFormat = SimpleDateFormat("yyyy년")
        val dateDefault = dateFormat.format(calendar.time)
        if (date == "") {
            clickOk?.invoke(dateDefault)
            dismiss()
        } else {
            clickOk?.invoke(date)
            dismiss()
        }
    }

}