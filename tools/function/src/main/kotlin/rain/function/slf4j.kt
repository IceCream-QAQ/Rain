package rain.function

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun slf4j(): Logger = LoggerFactory.getLogger(getCaller())