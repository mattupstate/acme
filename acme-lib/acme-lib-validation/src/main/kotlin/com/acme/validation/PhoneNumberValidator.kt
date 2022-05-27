package com.acme.validation

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class PhoneNumberValidator : ConstraintValidator<PhoneNumber, String> {
  private val phoneUtil by lazy {
    PhoneNumberUtil.getInstance()
  }

  private var annotation: PhoneNumber? = null

  override fun initialize(constraintAnnotation: PhoneNumber?) {
    annotation = constraintAnnotation
  }

  override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean =
    try {
      phoneUtil.isValidNumber(phoneUtil.parse(value!!, annotation!!.region))
    } catch (e: NumberParseException) {
      false
    }
}
