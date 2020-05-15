package io.devist.chat.utils.messages

import org.springframework.context.MessageSource
import org.springframework.context.support.MessageSourceAccessor
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MessageService(
        val messageSource: MessageSource
) {
    private lateinit var accessor: MessageSourceAccessor

    @PostConstruct
    fun init() {
        this.accessor = MessageSourceAccessor(this.messageSource)
    }

    operator fun get(code: String): String {
        return accessor.getMessage(code)
    }

    operator fun get(code: String, vararg args: Any): String {
        return accessor.getMessage(code, args)
    }
}