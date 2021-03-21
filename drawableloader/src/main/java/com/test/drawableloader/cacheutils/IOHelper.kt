package com.test.drawableloader.cacheutils

import java.io.*
import java.nio.charset.Charset

/**
 * Collection of Input / Output helper methods
 */
internal object IOHelper {
    @JvmField
    val US_ASCII = Charset.forName("US-ASCII")
    @JvmField
    val UTF_8 = Charset.forName("UTF-8")
    const val IO_BUFFER_SIZE = 8 * 1024
    @JvmStatic
    @Throws(IOException::class)
    fun readFully(reader: Reader): String {
        return try {
            val writer = StringWriter()
            val buffer = CharArray(1024)
            var count: Int
            while (reader.read(buffer).also { count = it } != -1) {
                writer.write(buffer, 0, count)
            }
            writer.toString()
        } finally {
            reader.close()
        }
    }

    /**
     * Deletes the contents of `dir`. Throws an IOException if any file
     * could not be deleted, or if `dir` is not a readable directory.
     */
    @JvmStatic
    @Throws(IOException::class)
    fun deleteContents(dir: File) {
        val files = dir.listFiles() ?: throw IOException("not a readable directory: $dir")
        for (file in files) {
            if (file.isDirectory) {
                deleteContents(file)
            }
            if (!file.delete()) {
                throw IOException("failed to delete file: $file")
            }
        }
    }

    @JvmStatic
    fun closeQuietly( /*Auto*/
        closeable: Closeable?
    ) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (rethrown: RuntimeException) {
                throw rethrown
            } catch (ignored: Exception) {
            }
        }
    }
}