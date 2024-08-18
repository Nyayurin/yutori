package cn.yurn.yutori

class EventParsingException(e: Throwable) : RuntimeException(e)
class NumberParsingException(value: String) : RuntimeException(value)
class MessageElementParsingException(message: String) : RuntimeException(message)
class MessageElementPropertyParsingException(type: String) : RuntimeException(type)
class TimeoutException(message: String) : RuntimeException(message)
class ModuleReinstallException(module: String) : RuntimeException(module)