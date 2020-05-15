package io.devist.chat.utils.validators

import org.apache.commons.validator.routines.EmailValidator

/**
 * Validate if is a valid email
 * @author Orlando Burli
 * @param  email Email to be validated
 * @return <b>true</b> if is a valid email, <b>false</b> otherwise
 */
fun Validators.isEmailValid(email: String?): Boolean = if (isValid(email)) EmailValidator.getInstance().isValid(email) else false