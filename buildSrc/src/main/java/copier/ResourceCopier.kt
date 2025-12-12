package copier

import java.io.*

object ResourceCopier {
    fun copyResources() {
        try {
            val res = File("./frontend/typescript-api/src/res.ts").bufferedWriter()

            res.appendln("// GENERATED AUTOMATICALLY DO NOT CHANGE!!!").appendln("const resources = {")

            File("./aggregate-api/src/main/java/com/tibbo/aggregate/common/res/Cres_en.properties")
                    .bufferedReader().forEachLine {
                        if (it.trim().isNotEmpty()) {
                            val key = it.substringBefore("=").trim()
                            val value = it.substringAfter("=").removePrefix(" ")
                            val quote = if (value.contains("\'")) "\"" else "\'"

                            res.appendln("  $key: $quote$value$quote,")
                        }
                    }
            res.appendln("};").appendln("export default resources;")
            res.close()
        } catch (ex: IOException) {
            println("Error when copying resources: " + ex.message)
            ex.printStackTrace()
        }
    }
}
