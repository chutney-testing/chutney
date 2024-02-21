package com.chutneytesting.idea.util

import org.hjson.JsonValue
import org.hjson.Stringify
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

object HJsonUtils {
    fun hjsonFromClipboard(): String? {
        val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
        return try {
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                val clipboardContent = transferable.getTransferData(DataFlavor.stringFlavor) as String
                var jsonValue: JsonValue? = null
                try {
                    jsonValue = JsonValue.readJSON(clipboardContent)
                    return null
                } catch (e: Exception) {
                    //e.printStackTrace();
                    //do nothing and try hjson
                }
                val hJson = convertHjson(clipboardContent)
                hJson.replace("\r\n".toRegex(), "\n")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun convertHjson(hjson: String?): String {
        return JsonValue.readHjson(hjson).toString(Stringify.PLAIN)
    }

    fun convertHjsonPrettyPrint(hjson: String?): String {
        return JsonValue.readHjson(hjson).toString(Stringify.FORMATTED)
    }

    fun toHJson(json: String?): String {
        return JsonValue.readHjson(json).toString(Stringify.HJSON)
    }

    fun fromClipboard(): String? {
        val transferable = Toolkit.getDefaultToolkit().systemClipboard.getContents(null)
        return try {
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                transferable.getTransferData(DataFlavor.stringFlavor) as String
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
