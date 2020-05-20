package io.devist.chat.utils.validators

/**
 * @author Orlando Burli
 * @param string Text to be validated as empty
 * @return <b>true</b> if is a valid filled string, <b>false</b> otherwise
 */
fun Validators.isValid(string: String?): Boolean = (string != null && !string.trim().isNullOrBlank())

/**
 * @author Orlando Burli
 * @param string Text to be validated as empty or with minimum size
 * @param minSize Minimum size of the string
 * @return <b>true</b> if is a valid filled string, with the minimum size, <b>false</b> otherwise
 */
fun Validators.isValid(string: String?, minSize: Int): Boolean = (isValid(string)) && string!!.trim().length >= minSize

/**
 * @author Orlando Burli
 * @param string Text to be validated as empty or with minimum size
 * @param minSize Minimum size of the string
 * @param maxSize Maximum size of the string
 * @return <b>true</b> if is a valid filled string, with the minimum and maximum size, <b>false</b> otherwise
 */
fun Validators.isValid(string: String?, minSize: Int, maxSize: Int): Boolean =
        (isValid(string, minSize)) && string!!.trim().length <= maxSize

/**
 * @author Orlando Burli
 * @param name Name of a person
 * @return <b>true</b> if is a valid name, with name and last name, <b>false</b> otherwise
 */
fun Validators.isFullNameValid(name: String?): Boolean = (isValid(name)) && name!!.split(" ").size > 1